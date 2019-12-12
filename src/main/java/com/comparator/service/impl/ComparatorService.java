package com.comparator.service.impl;

//import static com.comparator.utils.CompareUtils.isEmptyNode;
//import static com.comparator.utils.CompareUtils.writeAsJson;
import static com.comparator.utils.CompareUtils.getIgnoreCase;
import static com.comparator.utils.CompareUtils.isEqual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.tuple.Tuple2;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.comparator.model.CompareInput;
import com.comparator.model.JsonDiff;
import com.comparator.model.Key;
import com.comparator.model.Keys;
import com.comparator.model.SelectedNodes;
import com.comparator.service.IComparatorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * @author Mounif.Haydar
 *
 */

@Service
public class ComparatorService implements IComparatorService {

	private static final Logger	LOGGER	= LogManager.getLogger(ComparatorService.class);

	@Autowired
	@Qualifier("mapperIndent")
	private ObjectMapper		mapperIndent;

	@Override
	public JsonDiff compareJson(CompareInput compare) throws IOException, ParseException {

		JsonNode actual = compare.getActual();
		JsonNode expected = compare.getExpected();
		JsonDiff diff;
		//clear memory, recursive call
		compare.setActual(null);
		compare.setExpected(null);

		long startTime = System.currentTimeMillis();
		diff = checkDiff(compare, actual, expected, "", "root", "root", false, false, false);//.addJsonBorder();
		//diff.setDiff(new StringBuilder(jsonBeautify(diff.diff.toString(), mapperIndent)));

		LOGGER.info("execution time:" + ((System.currentTimeMillis() - startTime) / 1000) + " (seconds)");

		return diff;
	}

	/**
	 * 
	 * @param rootLevelActual
	 * @param rootLevelExpected
	 * @param parentRootName
	 * @param rootName
	 * @param path
	 * @param breakOnNull
	 *            : when this flag is <b>false</b>: so <b>missing node, null, 0
	 *            and ""</b> are expressed the same value
	 * @param selectedNodes
	 * @param allowedDiffPrecision
	 * @param keys
	 * @param isItemOfArray
	 * @param nodeSensitiveName
	 * @param caseSensitiveValue
	 * @return
	 * @throws IOException
	 */
	private JsonDiff checkDiff(CompareInput compare, JsonNode rootLevelActual, JsonNode rootLevelExpected, String parentRootName, String rootName, String path, boolean breakOnNullNode, boolean breakOnNullValue,
			boolean isItemOfArray) throws IOException {
		String nodeNotFound = "node does not exist";
		String itemNotFound = "item does not exist";
		String noUniqueItem = "No unique item found";
		String objectIsEmpty = "object is empty";
		String objectFields = "%s...";
		String arrayIsEmpty = "Array is empty";
		String arraySize = "Array size: %s";

		boolean nodeSensitiveName = compare.isNodeSensitiveName();
		boolean caseSensitiveValue = compare.isCaseSensitiveValue();
		Keys keys = compare.getKeys();
		int allowedDiffPrecision = compare.getPrecisions().allowedDiff(path, nodeSensitiveName);
		String[] itemCleaner = compare.getDirtyCleans().itemCleaner(path, nodeSensitiveName);
		String regex = compare.getRegexs().itemRegex(path, nodeSensitiveName);
		String[] dictionary = compare.getDictionaries().getDictionary(path, nodeSensitiveName);

		SelectedNodes selectedNodes = compare.getSelectedNodes();

		//before start the compare check if the node is included in the result
		if (selectedNodes.isSkip(path, nodeSensitiveName)) {
			return JsonDiff.neutralDiff(mapperIndent.createObjectNode());
		}

		//LEVEL 1 : check root if exist
		//------------------------------
		boolean isActualNull = rootLevelActual == null;
		boolean isExpectedNull = rootLevelExpected == null;
		if (isActualNull && isExpectedNull/* 0 0 */) {
			return JsonDiff.noDiff(mapperIndent.createObjectNode());//last child
		} else if (isActualNull && !isExpectedNull/* 0 1 */) {//check input if valid
			if (breakOnNullNode) {
				//return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { nodeNotFound, rootLevelExpected.toString() })).setNodeName(rootName);//last child
				return JsonDiff.diff(nodeNotFound, rootLevelExpected.toString(), mapperIndent.createObjectNode());
			} else {
				rootLevelActual = JsonNodeFactory.instance.nullNode();
			}
		} else if (!isActualNull && isExpectedNull/* 1 0 */) {
			if (breakOnNullNode) {
				//return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { rootLevelActual.toString(), nodeNotFound })).setNodeName(rootName);//last child
				return JsonDiff.diff(rootLevelActual.toString(), nodeNotFound, mapperIndent.createObjectNode());
			} else {
				rootLevelExpected = JsonNodeFactory.instance.nullNode();
			}
		} /*
			 * else if (rootLevelActual.isNull() && rootLevelExpected.isNull())
			 * {//null null return JsonDiff.noDiff(); } else if
			 * ((rootLevelActual.isNull() || rootLevelExpected.isNull())) {//
			 * not null null if (breakOnNullNode) return
			 * JsonDiff.diff(writeAsJson(nodeCompare, new String[] {
			 * rootLevelActual.toString(), rootLevelExpected.toString()
			 * })).setNodeName(rootName);//last child }
			 */

		//input exist, so compare the items: KEY/VALUE

		//LEVEL 2 : load all the child's of root in one list
		//--------------------------------------------
		// Merge: put the keys in one list
		Iterator<String> aFieldsItr = isActualNull ? IteratorUtils.emptyIterator() : rootLevelActual.fieldNames();
		Iterator<String> eFieldsItr = isExpectedNull ? IteratorUtils.emptyIterator() : rootLevelExpected.fieldNames();
		HashSet<String> fieldsSet = new HashSet<String>();
		//Map<String, String> mappingExpected = new HashMap<>();
		//Map<String, String> mappingActual = new HashMap<>();

		//check if one is empty
		if (breakOnNullNode) {
			if (aFieldsItr.hasNext() && !eFieldsItr.hasNext()) {
				//return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(objectFields, aFieldsItr.next()), objectIsEmpty })).setNodeName(rootName);
				return JsonDiff.diff(String.format(objectFields, aFieldsItr.next()), objectIsEmpty, mapperIndent.createObjectNode());//.setNodeName(rootName);
			} else if (!aFieldsItr.hasNext() && eFieldsItr.hasNext()) {
				return JsonDiff.diff(objectIsEmpty, String.format(objectFields, eFieldsItr.next()), mapperIndent.createObjectNode());//.setNodeName(rootName);
			}
		} else {
			//Object Empty so set to JsonNull node, null or {}
			if (!aFieldsItr.hasNext() && rootLevelActual
					.isObject() /*
								 * && !rootLevelActual.isNull() &&
								 * !rootLevelActual.isArray()
								 */) {
				rootLevelActual = JsonNodeFactory.instance.nullNode();
			}
			if (!eFieldsItr.hasNext() && rootLevelExpected
					.isObject() /*
								 * && !rootLevelExpected.isNull() &&
								 * !rootLevelExpected.isArray()
								 */) {
				rootLevelExpected = JsonNodeFactory.instance.nullNode();
			}
		}

		while (aFieldsItr.hasNext()) {
			String aNodeName = aFieldsItr.next();
			fieldsSet.add(/* nodeSensitiveName ? */ aNodeName /*
																 * : aNodeName.
																 * toLowerCase()
																 */);
			//mappingActual.put(aNodeName.toLowerCase(), aNodeName);
		}

		while (eFieldsItr.hasNext()) {
			String eNodeName = eFieldsItr.next();
			fieldsSet.add(/* nodeSensitiveName ? */ eNodeName /*
																 * : eNodeName.
																 * toLowerCase()
																 */);
			//mappingExpected.put(eNodeName.toLowerCase(), eNodeName);
		}

		//completeMapping(mappingExpectedActual, mappingActualExpected);

		// New iterator of unique objects
		Iterator<String> allFieldsItr = fieldsSet.iterator();

		if (allFieldsItr.hasNext()) {//Loop over the Node's
			//LEVEL 3: loop over the child
			JsonDiff output = JsonDiff.init(mapperIndent.createObjectNode());
			while (allFieldsItr.hasNext()) {
				String parentName = allFieldsItr.next();
				String nodePath = path + "." + parentName;
				JsonNode parentActual = isActualNull ? null : getJsonNode(parentName, rootLevelActual, nodeSensitiveName);
				JsonNode parentExpect = isExpectedNull ? null : getJsonNode(parentName, rootLevelExpected, nodeSensitiveName);
				JsonDiff tmpDiff = checkDiff(compare, parentActual, parentExpect, rootName, parentName, nodePath, breakOnNullNode, breakOnNullValue, false);

				if (tmpDiff.isNoDiff()) {
					output.incrementEqual(tmpDiff);
				} else {
					//Diff found
					//if object is empty, so no need to loop over the Node's
					if (rootLevelActual.fieldNames().hasNext() && !rootLevelExpected.fieldNames().hasNext()) {
						return JsonDiff.diff(String.format(objectFields, parentName), objectIsEmpty, mapperIndent.createObjectNode());//.setNodeName(rootName);
					} else if (!rootLevelActual.fieldNames().hasNext() && rootLevelExpected.fieldNames().hasNext()) {
						return JsonDiff.diff(objectIsEmpty, String.format(objectFields, parentName), mapperIndent.createObjectNode());//.setNodeName(rootName);
					}
					output.appendNodeDiff(parentName, tmpDiff, null);
				}
			}
			/*
			 * if (!isItemOfArray) {
			 * output.addJsonBorder().setNodeName(rootName);//parent level, and
			 * grand parent }
			 */

			return output;
		} else {//LEVEL 4: one child, Single Node
			//TODO:IF null, and all array filed are null => return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(objectFields, aFieldsItr.next()), objectIsEmpty })).setNodeName(rootName);
			if (!isActualNull && rootLevelActual.isArray() || !isExpectedNull && rootLevelExpected.isArray()) {//loop as List

				//long startTime = System.currentTimeMillis();
				//System.out.println("Array path:" + path);
				JsonDiff output = JsonDiff.init(mapperIndent.createArrayNode());
				List<JsonNode> actualAsList = new ArrayList<>();
				List<JsonNode> expectedAsList = new ArrayList<>();
				List<JsonNode> actualAsListCopy = new ArrayList<>();
				List<JsonNode> expectedAsListCopy = new ArrayList<>();

				if (!isActualNull)
					for (JsonNode a : rootLevelActual) {
						actualAsList.add(a);
						actualAsListCopy.add(a);
					}
				if (!isExpectedNull)
					for (JsonNode e : rootLevelExpected) {
						expectedAsList.add(e);
						expectedAsListCopy.add(e);
					}

				//check if one is an empty array, => return

				if (actualAsList.size() == 0 && expectedAsList.size() == 0) {
					return JsonDiff.noDiff(mapperIndent.createObjectNode());
				} else if (actualAsList.size() == 0 && expectedAsList.size() != 0) {
					for (int i = 0; i < expectedAsList.size(); i++) {
						JsonNode itemE = expectedAsList.get(i);
						JsonNode itemA = JsonNodeFactory.instance.nullNode();
						JsonDiff tmpDiff = checkDiff(compare, itemA, itemE, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue, true);
						if (!tmpDiff.isNoDiff()) {
							return JsonDiff.diff(arrayIsEmpty, String.format(arraySize, expectedAsList.size()), mapperIndent.createObjectNode());//.setNodeName(rootName);
						}
					}
					return JsonDiff.noDiff(mapperIndent.createObjectNode());

				} else if (actualAsList.size() != 0 && expectedAsList.size() == 0) {//if one is an empty array
					for (int i = 0; i < actualAsList.size(); i++) {
						JsonNode itemE = JsonNodeFactory.instance.nullNode();
						JsonNode itemA = actualAsList.get(i);
						JsonDiff tmpDiff = checkDiff(compare, itemA, itemE, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue, true);
						if (!tmpDiff.isNoDiff()) {
							return JsonDiff.diff(String.format(arraySize, actualAsList.size()), arrayIsEmpty, mapperIndent.createObjectNode());//.setNodeName(rootName);
						}
					}
					return JsonDiff.noDiff(mapperIndent.createArrayNode());
				}

				boolean duplicatedKey = false;
				boolean denyDuplication = keys.isDenyDuplication(path, nodeSensitiveName);
				Set<JsonNode> actualToBeRemoved = new HashSet<JsonNode>();
				for (int i = 0; i < expectedAsList.size(); i++) {
					JsonNode itemE = expectedAsList.get(i);
					List<JsonNode> sA = new ArrayList<>();

					List<Tuple2<JsonNode, JsonDiff>> targetA = new ArrayList<>();

					if (denyDuplication) {
						//check if key duplicated is the list itself
						duplicatedKey = getItemCorrelation(compare, expectedAsList, itemE, 1, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue).size() > 1;
						targetA = getItemCorrelation(compare, actualAsList, itemE, 1, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue);
					} else {
						targetA = getCloserItemForwardAndReverse(compare, actualAsList, itemE, expectedAsList, i, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue);
					}

					if (targetA.size() > 0) {
						for (Tuple2<JsonNode, JsonDiff> tuple2 : targetA) {
							sA.add(tuple2.v1);
						}

					}

					if ((sA.size() > 1 || duplicatedKey) && denyDuplication) {//no unique key,
						//do not remove from list, to find duplication next loop
						String itemFoundKey = generateItemKeyStr(keys.getNodeInfo(path, nodeSensitiveName), itemE);
						/*
						 * output.appendDiff(JsonDiff.diff(writeAsJson(
						 * nodeCompare, new String[] { noUniqueItem,
						 * itemFoundKey })) .setNodeName(rootName + ".Item[" +
						 * generateItemName(compare, path, keys, itemE,
						 * expectedAsListCopy) + "]").addJsonBorder());
						 */

						output.appendNodeDiff(rootName + ".Item[" + generateItemName(compare, path, keys, itemE, expectedAsListCopy) + "]", JsonDiff.diff(noUniqueItem, itemFoundKey, mapperIndent.createObjectNode()),
								mapperIndent);

						for (JsonNode aTb : sA) {
							actualToBeRemoved.add(aTb);
						}
					} else {
						if (sA.size() > 0) {
							JsonNode itemA = sA.get(0);
							JsonDiff tmpDiff = checkDiff(compare, itemA, itemE, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue, true);
							if (tmpDiff.isNoDiff()) {
								output.incrementEqual(tmpDiff);
							} else {
								//tmpDiff.addJsonBorder().setNodeName();
								//tmpDiff.addJsonBorder();
								output.appendNodeDiff(rootName + ".Item[" + generateItemName(compare, path, keys, itemE, expectedAsListCopy) + "]", tmpDiff, mapperIndent);
							}
							actualAsList.remove(itemA);
						} else {//size 0
							String itemFoundKey = generateItemKeyStr(keys.getNodeInfo(path, nodeSensitiveName), itemE);
							output.appendNodeDiff(rootName + ".Item[" + generateItemName(compare, path, keys, itemE, expectedAsListCopy) + "]", JsonDiff.diff(itemNotFound, itemFoundKey, mapperIndent.createObjectNode()),
									mapperIndent);

							;
						}
					}
				}

				actualAsList.removeAll(actualToBeRemoved);

				if (actualAsList.size() != 0) {
					for (JsonNode n : actualAsList) {
						String itemFoundKey = generateItemKeyStr(keys.getNodeInfo(path, nodeSensitiveName), n);
						//check if key duplicated is the list itself
						duplicatedKey = getItemCorrelation(compare, actualAsList, n, 1, parentRootName, rootName, path, breakOnNullNode, breakOnNullValue).size() > 1;
						if (denyDuplication && duplicatedKey) {
							output.appendNodeDiff(rootName + ".Item[" + generateItemName(compare, path, keys, n, actualAsListCopy) + "]", JsonDiff.diff(noUniqueItem, itemFoundKey, mapperIndent.createObjectNode()),
									mapperIndent);
						} else {
							output.appendNodeDiff(rootName + ".Item[" + generateItemName(compare, path, keys, n, actualAsListCopy) + "]", JsonDiff.diff(itemFoundKey, itemNotFound, mapperIndent.createObjectNode()),
									mapperIndent);
						}

					}
				}

				//output.addArrayBorder().setNodeName(rootName);//array level

				//System.out.println("execution time of \n" + path + ": \n" + ((System.currentTimeMillis() - startTime) / 1000)+" (seconds)");
				return output;

			} else {//not an array
				//boolean nodeFound = primaryNodes.isContainsNodeInfo(new NodeInfo(parentRootName, rootName));
				boolean equal = false;
				//boolean skipNode = nodeFound && !primaryIncluded || primaryIncluded && !nodeFound;

				/*
				 * if (primaryNodes.isSkip(new NodeInfo(parentRootName,
				 * rootName))) { equal = true; } else {
				 */
				equal = isEqual(rootName, rootLevelActual, rootLevelExpected, breakOnNullNode, breakOnNullValue, allowedDiffPrecision, caseSensitiveValue, itemCleaner, regex, dictionary);
				/* } */

				if (equal) {
					return JsonDiff.noDiff(mapperIndent.createObjectNode());
				} else {
					return JsonDiff.diff(isActualNull ? nodeNotFound : rootLevelActual.toString(), isExpectedNull ? nodeNotFound : rootLevelExpected.toString(), mapperIndent.createObjectNode());
					//.setNodeName(rootName);//last child
				}

			}
		}

	}

	private JsonNode getJsonNode(String nodeName, JsonNode jsonNode, boolean caseSensitive) {
		/*
		 * JsonNode j = jsonNode.get(nodeName); if (j == null && !caseSensitive
		 * && mappingName.get(nodeName) != null) { j =
		 * jsonNode.get(mappingName.get(nodeName)); } return j;
		 */
		return caseSensitive ? jsonNode.get(nodeName) : getIgnoreCase(nodeName, jsonNode);
	}

	/*
	 * private void completeMapping(Map<String, String> mappingExpectedActual,
	 * Map<String, String> mappingActualExpected) { for (Entry<String, String>
	 * eToA : mappingExpectedActual.entrySet()) { for (Entry<String, String>
	 * aToE : mappingActualExpected.entrySet()) { if
	 * (eToA.getKey().equalsIgnoreCase(aToE.getKey())) {
	 * eToA.setValue(aToE.getKey()); aToE.setValue(eToA.getKey()); break; } } }
	 * }
	 */

	private List<JsonNode> getItemById(List<JsonNode> list, String[] keys, JsonNode item, int allowedDiffPrecision, boolean caseSensitiveValue, boolean breakOnNullNode, boolean breakOnNullValue, String[] dertyClean,
			String regex , String[] dictionary) throws IOException {
		List<JsonNode> matches = new ArrayList<>();
		for (JsonNode s : list) {
			//filter on key
			for (int whereI = 0; whereI < keys.length; whereI++) {
				if (!isEqual("", item.get(keys[whereI]), s.get(keys[whereI]), breakOnNullNode, breakOnNullValue, allowedDiffPrecision, caseSensitiveValue, dertyClean, regex, dictionary)) {
					continue;
				}
			}
			matches.add(s);
		}
		/*
		 * return list.stream().filter(s -> { if (keys != null) {//filter on key
		 * for (int whereI = 0; whereI < keys.length; whereI++) { if
		 * (!compare("", item.get(keys[whereI]), s.get(keys[whereI]), false)) {
		 * return false; } } return true; } else {//filter on equal all fields
		 * return s.equals(item); } }).collect(Collectors.toList());
		 */
		return matches;
	}

	private JsonNode getCloserItem(CompareInput compare, List<JsonNode> list, JsonNode item, boolean primaryIncluded, int allowedDiffPrecision, String parentName, String listName, String nodePath, Keys keys,
			boolean nodeSensitiveName, boolean caseSensitiveValue, boolean breakOnNullNode, boolean breakOnNullValue) throws IOException {

		JsonDiff tmpDiff = null;
		JsonNode closerNode = null;

		for (JsonNode s : list) {
			if (closerNode == null) {//first item
				tmpDiff = checkDiff(compare, item, s, parentName, listName, nodePath, breakOnNullNode, breakOnNullValue, true);
				closerNode = s;
			} else {
				JsonDiff tmpDiff2 = checkDiff(compare, item, s, parentName, listName, nodePath, breakOnNullNode, breakOnNullValue, true);
				if (tmpDiff2.diffCounter < tmpDiff.diffCounter) {
					tmpDiff = tmpDiff2;
					closerNode = s;
				}
			}
			if (tmpDiff.isNoDiff()) {
				break;
			}
		}

		if (tmpDiff.equalCounter == 0) {
			return null;
		} else {
			return closerNode;
		}
	}

	private List<Tuple2<JsonNode, JsonDiff>> getCloserItemForwardAndReverse(CompareInput compare, List<JsonNode> actualAsList, JsonNode itemE, List<JsonNode> expectedAsList, int fromIndex, String parentName,
			String listName, String nodePath, boolean breakOnNullNode, boolean breakOnNullValue) throws IOException {

		List<Tuple2<JsonNode, JsonDiff>> target = new ArrayList<>();
		//Step 1. Forward: fetch iExpected in ListActual
		List<Tuple2<JsonNode, JsonDiff>> forward = getItemCorrelation(compare, actualAsList, itemE, 1, parentName, listName, nodePath, breakOnNullNode, breakOnNullValue);
		if (forward.size() > 0) {
			for (int index = 0; index < forward.size(); index++) {
				Tuple2<JsonNode, JsonDiff> tuple2 = forward.get(index);
				JsonNode fJ2 = tuple2.v1;
				JsonDiff fD2 = tuple2.v2;
				boolean toBeUsed = true;
				if (fD2.diffCounter == 0) {
					return Arrays.asList(tuple2);
				}
				//Step 2. Reveres: fetch iActual in ListExpected
				List<Tuple2<JsonNode, JsonDiff>> reverse = getItemCorrelation(compare, expectedAsList.subList(fromIndex + 1, expectedAsList.size()), fJ2, 1, parentName, listName, nodePath, breakOnNullNode,
						breakOnNullValue);

				for (Tuple2<JsonNode, JsonDiff> tuple22 : reverse) {
					//JsonNode fJ22 = tuple22.v1;
					JsonDiff fD22 = tuple22.v2;

					if (fD22.diffCounter == 0) {
						forward.remove(tuple2);
						index--;
					}

					if (fD22.diffCounter == 0 || fD22.equalCounter > fD2.equalCounter) {
						toBeUsed = false;
						break;
					}
				}

				if (toBeUsed) {
					target.add(tuple2);
				}
			}

			//	The below code removed, forward and reverse should be return the best related item.
			//if (target.size() == 0 && forward.size() > 0) {
			//	if (!keys.isHasKey(nodePath, nodeSensitiveName)) {
			//		target.add(forward.get(forward.size() - 1));
			//	}
			//}
		}
		Collections.sort(target, JsonDiff.EQUAL_DESC_DIFF_ASC);
		return target;
	}

	private List<Tuple2<JsonNode, JsonDiff>> getItemCorrelation(CompareInput compare, List<JsonNode> list, JsonNode item, int equalThreshold, String parentName, String listName, String nodePath, boolean breakOnNullNode,
			boolean breakOnNullValue) throws IOException {

		Keys keys = compare.getKeys();
		int allowedDiffPrecision = compare.getPrecisions().allowedDiff(nodePath, compare.isNodeSensitiveName());
		String[] itemCleaner = compare.getDirtyCleans().itemCleaner(nodePath, compare.isNodeSensitiveName());

		JsonDiff tmpDiff = null;
		List<Tuple2<JsonNode, JsonDiff>> closerNode = new ArrayList<>();

		for (JsonNode s : list) {
			tmpDiff = checkDiff(compare, item, s, parentName, listName, nodePath, breakOnNullNode, breakOnNullValue, true);

			if (tmpDiff.isNoDiff() && !keys.isDenyDuplication(nodePath, compare.isNodeSensitiveName())) {
				closerNode.clear();
				closerNode.add(new Tuple2<JsonNode, JsonDiff>(s, tmpDiff));
				break;
			}

			if (keys.isHasKey(nodePath, compare.isNodeSensitiveName())) {
				boolean sameKeyFound = checkRequiredFieldIsEqual(compare, item, s, parentName, listName, nodePath, allowedDiffPrecision, keys.getNodeInfo(
						nodePath/* parentName, listName, nodePath */, compare.isNodeSensitiveName()), breakOnNullNode, breakOnNullValue, itemCleaner);
				if (sameKeyFound) {
					closerNode.add(new Tuple2<JsonNode, JsonDiff>(s, tmpDiff));
				}
			} else if (tmpDiff.equalCounter >= equalThreshold) {
				closerNode.add(new Tuple2<JsonNode, JsonDiff>(s, tmpDiff));
			}

		}
		Collections.sort(closerNode, JsonDiff.EQUAL_DESC_DIFF_ASC);
		return closerNode;
	}

	private boolean checkRequiredFieldIsEqual(CompareInput compare, JsonNode item, JsonNode s, String parentName, String rootName, String nodePath, int allowedDiffPrecision, Key key, boolean breakOnNullNode,
			boolean breakOnNullValue, String[] dertyClean) throws IOException {

		boolean found = true;
		//filter on key
		for (String where : key.getKeySet()) {
			JsonDiff tmpDiff = checkDiff(compare, item.get(where), s.get(where), rootName, where, nodePath, breakOnNullNode, breakOnNullValue, false);
			//!isEqual("", item.get(where), s.get(where), breakOnNullNode, breakOnNullValue, allowedDiffPrecision, caseSensitiveValue, dertyClean)
			if (!tmpDiff.isNoDiff()) {
				found = false;
				break;
			}
		}
		return found;
	}

	private String generateItemKeyStr(Key keySet, JsonNode n) {
		String itemNotFound = "";
		if (keySet != null) {

			for (String keyI : keySet.getKeySet()) {
				try {
					itemNotFound += itemNotFound.length() > 0 ? "-" : "";
					itemNotFound += keyI;
					itemNotFound += "[";
					itemNotFound += n.get(keyI).toString();
					itemNotFound += "]";

				} catch (Exception e) {
					itemNotFound = keyI + "_missing";
					e.printStackTrace();
				}
			}
		} else {
			itemNotFound = n.toString();
		}
		return itemNotFound;
	}

	private String generateItemName(CompareInput compare, String path, Keys keys, JsonNode n, List<JsonNode> nList) {
		boolean nodeSensitiveName = compare.isNodeSensitiveName();
		String itemName = (keys.isHasKey(path, nodeSensitiveName) ? generateItemKeyStr(keys.getNodeInfo(path, nodeSensitiveName), n) : nList.indexOf(n)) + "";
		return itemName;
	}
}
