package com.comparator.service.impl;

import static com.comparator.utils.CompareUtils.compare;
import static com.comparator.utils.CompareUtils.jsonBeautify;
//import static com.comparator.utils.CompareUtils.isEmptyNode;
import static com.comparator.utils.CompareUtils.writeAsJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.IteratorUtils;
import org.jooq.lambda.tuple.Tuple2;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.comparator.model.CompareInput;
import com.comparator.model.JsonDiff;
import com.comparator.model.Key;
import com.comparator.model.Keys;
import com.comparator.model.NodeInfo;
import com.comparator.model.NodeInfos;
import com.comparator.service.IComparatorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

/**
 * @author Mounif.Haydar
 *
 */
/**
 * @author Mounif.Haydar
 *
 */
/**
 * @author Mounif.Haydar
 *
 */
@Service
public class ComparatorService implements IComparatorService {

	@Autowired
	@Qualifier("mapperIndent")
	private ObjectMapper mapperIndent;

	@Override
	public JsonDiff compareJson(CompareInput compare) throws IOException, ParseException {

		JsonNode actual = compare.getActual();
		JsonNode expected = compare.getExpected();
		JsonDiff diff;

		diff = checkDiff(actual, expected, "", "root", "root", false, new NodeInfos(compare.isPrimaryIncluded(), compare.getPrimaryNodes()), compare.getPrecision(), new Keys(compare.getKeys()), false,
				compare.isNodeSensitiveName(), compare.isCaseSensitiveValue()).addJsonBorder();
		diff.setDiff(new StringBuilder(jsonBeautify(diff.diff.toString(), mapperIndent)));
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
	 * @param primaryNodes
	 * @param allowedDiffPrecision
	 * @param keys
	 * @param isItemOfArray
	 * @param nodeSensitiveName
	 * @param caseSensitiveValue
	 * @return
	 * @throws IOException
	 */
	private JsonDiff checkDiff(JsonNode rootLevelActual, JsonNode rootLevelExpected, String parentRootName, String rootName, String path, boolean breakOnNull, NodeInfos primaryNodes, int allowedDiffPrecision, Keys keys,
			boolean isItemOfArray, boolean nodeSensitiveName, boolean caseSensitiveValue) throws IOException {
		String nodeNotFound = "node does not exist";
		String itemNotFound = "item does not exist";
		String noUniqueItem = "No unique item found";
		String objectIsEmpty = "object is empty";
		String objectFields = "%s...";
		String arrayIsEmpty = "Array is empty";
		String arraySize = "Array size: %s";
		String[] nodeCompare = new String[] { "actual", "expected" };

		//before start the compare check if the node is included in the result
		if (primaryNodes.isSkip(new NodeInfo(parentRootName, rootName, path), nodeSensitiveName)) {
			return JsonDiff.noDiff();
		}

		//LEVEL 1 : check root if exist
		//------------------------------
		boolean isActualNull = false;
		boolean isExpectedNull = false;
		if (rootLevelActual == null && rootLevelExpected == null/* 0 0 */) {
			return JsonDiff.noDiff();//last child
		} else if (rootLevelActual == null && rootLevelExpected != null/* 0 1 */) {//check input if valid
			isActualNull = true;
			if (breakOnNull)
				return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { nodeNotFound, rootLevelExpected.toString() })).setNodeName(rootName);//last child

		} else if (rootLevelActual != null && rootLevelExpected == null/* 1 0 */) {
			isExpectedNull = true;
			if (breakOnNull)
				return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { rootLevelActual.toString(), nodeNotFound })).setNodeName(rootName);//last child

		} else if (rootLevelActual.isNull() && rootLevelExpected.isNull()) {//null null
			return JsonDiff.noDiff();
		} else if ((rootLevelActual.isNull() || rootLevelExpected.isNull())) {// not null null
			if (breakOnNull)
				return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { rootLevelActual.toString(), rootLevelExpected.toString() })).setNodeName(rootName);//last child
		}

		//input exist, so compare the items: KEY/VALUE

		//LEVEL 2 : load all the child's of root in one list
		//--------------------------------------------
		// Merge: put the keys in one list
		Iterator<String> aFieldsItr = isActualNull ? IteratorUtils.emptyIterator() : rootLevelActual.fieldNames();
		Iterator<String> eFieldsItr = isExpectedNull ? IteratorUtils.emptyIterator() : rootLevelExpected.fieldNames();
		HashSet<String> fieldsSet = new HashSet<String>();
		Map<String, String> mappingExpected = new HashMap<>();
		Map<String, String> mappingActual = new HashMap<>();

		//check if one is empty

		if (breakOnNull) {
			if (aFieldsItr.hasNext() && !eFieldsItr.hasNext()) {
				return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(objectFields, aFieldsItr.next()), objectIsEmpty })).setNodeName(rootName);
			} else if (!aFieldsItr.hasNext() && eFieldsItr.hasNext()) {
				return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { objectIsEmpty, String.format(objectFields, eFieldsItr.next()) })).setNodeName(rootName);
			}
		}

		while (aFieldsItr.hasNext()) {
			String aNodeName = aFieldsItr.next();
			fieldsSet.add(nodeSensitiveName ? aNodeName : aNodeName.toLowerCase());
			mappingActual.put(aNodeName.toLowerCase(), aNodeName);
		}

		while (eFieldsItr.hasNext()) {
			String eNodeName = eFieldsItr.next();
			fieldsSet.add(nodeSensitiveName ? eNodeName : eNodeName.toLowerCase());
			mappingExpected.put(eNodeName.toLowerCase(), eNodeName);
		}

		//completeMapping(mappingExpectedActual, mappingActualExpected);

		// New iterator of unique objects
		Iterator<String> allFieldsItr = fieldsSet.iterator();

		if (allFieldsItr.hasNext()) {//Loop over the Node's
			//LEVEL 3: loop over the child
			JsonDiff output = JsonDiff.init();
			while (allFieldsItr.hasNext()) {
				String parentName = allFieldsItr.next();
				String nodePath = path + "." + parentName;
				JsonNode parentActual = isActualNull ? null : getJsonNode(parentName, rootLevelActual, nodeSensitiveName, mappingActual);
				JsonNode parentExpect = isExpectedNull ? null : getJsonNode(parentName, rootLevelExpected, nodeSensitiveName, mappingExpected);
				JsonDiff tmpDiff = checkDiff(parentActual, parentExpect, rootName, parentName, nodePath, breakOnNull, primaryNodes, allowedDiffPrecision, keys, false, nodeSensitiveName, caseSensitiveValue);

				if (tmpDiff.isNoDiff()) {
					output.incrementEqual(tmpDiff);
				} else {
					//Diff found
					//if object is empty, so no need to loop over the Node's
					if (mappingActual.size() > 0 && mappingExpected.size() == 0) {
						return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(objectFields, parentName), objectIsEmpty })).setNodeName(rootName);
					} else if (mappingActual.size() == 0 && mappingExpected.size() > 0) {
						return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { objectIsEmpty, String.format(objectFields, parentName) })).setNodeName(rootName);
					}
					output.appendDiff(tmpDiff);
				}
			}
			if (!isItemOfArray) {
				output.addJsonBorder().setNodeName(rootName);//parent level, and grand parent
			}

			return output;
		} else {//LEVEL 4: one child, Single Node
			//TODO:IF null, and all array filed are null => return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(objectFields, aFieldsItr.next()), objectIsEmpty })).setNodeName(rootName);
			if (!isActualNull && rootLevelActual.isArray() || !isExpectedNull && rootLevelExpected.isArray()) {//loop as List

				long startTime = System.currentTimeMillis();
				//System.out.println("Array path:" + path);
				JsonDiff output = JsonDiff.init();
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

				if (actualAsList.size() == 0 && expectedAsList.size() != 0) {
					for (int i = 0; i < expectedAsList.size(); i++) {
						JsonNode itemE = expectedAsList.get(i);
						JsonNode itemA = JsonNodeFactory.instance.nullNode();
						JsonDiff tmpDiff = checkDiff(itemA, itemE, parentRootName, rootName, path, breakOnNull, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);
						if (!tmpDiff.isNoDiff()) {
							return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { arrayIsEmpty, String.format(arraySize, expectedAsList.size()) })).setNodeName(rootName);
						}
					}
					return JsonDiff.noDiff();

				} else if (actualAsList.size() != 0 && expectedAsList.size() == 0) {//if one is an empty array
					for (int i = 0; i < actualAsList.size(); i++) {
						JsonNode itemE = JsonNodeFactory.instance.nullNode();
						JsonNode itemA = actualAsList.get(i);
						JsonDiff tmpDiff = checkDiff(itemA, itemE, parentRootName, rootName, path, breakOnNull, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);
						if (!tmpDiff.isNoDiff()) {
							return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { String.format(arraySize, actualAsList.size()), arrayIsEmpty })).setNodeName(rootName);
						}
					}
					JsonDiff.noDiff();

				}

				for (int i = 0; i < expectedAsList.size(); i++) {
					JsonNode itemE = expectedAsList.get(i);
					List<JsonNode> sA = new ArrayList<>();
					if (actualAsList.size() > 0) {
						List<Tuple2<JsonNode, JsonDiff>> targetA = getCloserItemForwardAndReverse(actualAsList, itemE, primaryNodes, allowedDiffPrecision, expectedAsList, i, keys, parentRootName, rootName, path,
								nodeSensitiveName, caseSensitiveValue);
						if (targetA.size() > 0) {
							for (Tuple2<JsonNode, JsonDiff> tuple2 : targetA) {
								sA.add(tuple2.v1);
							}

						}
					}

					if (sA.size() > 1 && keys.isDenyDuplication(parentRootName, rootName, path, nodeSensitiveName)) {//no unique key,

						String itemFoundKey = generateItemKeyStr(keys.findPKList(parentRootName, rootName, path, nodeSensitiveName), itemE);
						output.appendDiff(JsonDiff.diff(writeAsJson(nodeCompare, new String[] { noUniqueItem, itemFoundKey })));

					} else {
						if (sA.size() > 0) {
							JsonNode itemA = sA.get(0);
							JsonDiff tmpDiff = checkDiff(itemA, itemE, parentRootName, rootName, path, breakOnNull, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);
							if (tmpDiff.isNoDiff()) {
								output.incrementEqual(tmpDiff);
							} else {
								tmpDiff.addJsonBorder()
										.setNodeName(rootName + ".Item["
												+ (keys.findPKList(parentRootName, rootName, path, nodeSensitiveName) != null
														? generateItemKeyStr(keys.findPKList(parentRootName, rootName, path, nodeSensitiveName), itemE)
														: expectedAsListCopy.indexOf(itemE))
												+ "]");
								tmpDiff.addJsonBorder();
								output.appendDiff(tmpDiff);
							}
							actualAsList.remove(itemA);
						} else {//size 0
							String itemFoundKey = generateItemKeyStr(keys.findPKList(parentRootName, rootName, path, nodeSensitiveName), itemE);
							output.appendDiff(
									JsonDiff.diff(writeAsJson(nodeCompare, new String[] { itemNotFound, itemFoundKey })).setNodeName(rootName + ".Item[" + expectedAsListCopy.indexOf(itemE) + "]").addJsonBorder());
						}
					}
				}

				if (actualAsList.size() != 0) {
					for (JsonNode n : actualAsList) {
						String itemFoundKey = generateItemKeyStr(keys.findPKList(parentRootName, rootName, path, nodeSensitiveName), n);
						output.appendDiff(JsonDiff.diff(writeAsJson(nodeCompare, new String[] { itemFoundKey, itemNotFound })).setNodeName(rootName + ".Item[" + actualAsListCopy.indexOf(n) + "]").addJsonBorder());
					}
				}

				output.addArrayBorder().setNodeName(rootName);//array level

				System.out.println("execution time of \n" + path + ": \n" + ((System.currentTimeMillis() - startTime) / 1000)+" (seconds)");
				return output;

			} else {//not an array
				//boolean nodeFound = primaryNodes.isContainsNodeInfo(new NodeInfo(parentRootName, rootName));
				boolean equal = false;
				//boolean skipNode = nodeFound && !primaryIncluded || primaryIncluded && !nodeFound;

				/*
				 * if (primaryNodes.isSkip(new NodeInfo(parentRootName,
				 * rootName))) { equal = true; } else {
				 */
				equal = compare(rootName, rootLevelActual, rootLevelExpected, breakOnNull, allowedDiffPrecision, caseSensitiveValue);
				/* } */

				if (equal) {
					return JsonDiff.noDiff();
				} else {
					return JsonDiff.diff(writeAsJson(nodeCompare, new String[] { isActualNull ? nodeNotFound : rootLevelActual.toString(), isExpectedNull ? nodeNotFound : rootLevelExpected.toString() }))
							.setNodeName(rootName);//last child
				}

			}
		}

	}

	private JsonNode getJsonNode(String nodeName, JsonNode jsonNode, boolean caseSensitive, Map<String, String> mappingName) {
		/*
		 * JsonNode j = jsonNode.get(nodeName); if (j == null && !caseSensitive
		 * && mappingName.get(nodeName) != null) { j =
		 * jsonNode.get(mappingName.get(nodeName)); } return j;
		 */
		return caseSensitive ? jsonNode.get(nodeName) : jsonNode.get(mappingName.get(nodeName));
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

	private List<JsonNode> getItemById(List<JsonNode> list, String[] keys, JsonNode item, int allowedDiffPrecision, boolean caseSensitiveValue) throws IOException {
		List<JsonNode> matches = new ArrayList<>();
		for (JsonNode s : list) {
			//filter on key
			for (int whereI = 0; whereI < keys.length; whereI++) {
				if (!compare("", item.get(keys[whereI]), s.get(keys[whereI]), false, allowedDiffPrecision, caseSensitiveValue)) {
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

	private JsonNode getCloserItem(List<JsonNode> list, JsonNode item, NodeInfos primaryNodes, boolean primaryIncluded, int allowedDiffPrecision, String parentName, String listName, String nodePath, Keys keys,
			boolean nodeSensitiveName, boolean caseSensitiveValue) throws IOException {

		JsonDiff tmpDiff = null;
		JsonNode closerNode = null;

		for (JsonNode s : list) {
			if (closerNode == null) {//first item
				tmpDiff = checkDiff(item, s, parentName, listName, nodePath, false, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);
				closerNode = s;
			} else {
				JsonDiff tmpDiff2 = checkDiff(item, s, parentName, listName, nodePath, false, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);
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

	private List<Tuple2<JsonNode, JsonDiff>> getCloserItemForwardAndReverse(List<JsonNode> actualAsList, JsonNode itemE, NodeInfos primaryNodes, int allowedDiffPrecision, List<JsonNode> expectedAsList, int fromIndex,
			Keys keys, String parentName, String listName, String nodePath, boolean nodeSensitiveName, boolean caseSensitiveValue) throws IOException {
		List<Tuple2<JsonNode, JsonDiff>> target = new ArrayList<>();
		//Step 1. Forward: fetch iExpected in ListActual
		List<Tuple2<JsonNode, JsonDiff>> forward = getItemCorrelation(actualAsList, itemE, primaryNodes, allowedDiffPrecision, 1, keys, parentName, listName, nodePath, nodeSensitiveName, caseSensitiveValue);
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
				List<Tuple2<JsonNode, JsonDiff>> reverse = getItemCorrelation(expectedAsList.subList(fromIndex + 1, expectedAsList.size()), fJ2, primaryNodes, allowedDiffPrecision, 1, keys, parentName, listName,
						nodePath, nodeSensitiveName, caseSensitiveValue);

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
			if (target.size() == 0 && forward.size() > 0) {
				if (!keys.isHasKey(parentName, listName, nodePath, nodeSensitiveName)) {
					target.add(forward.get(forward.size() - 1));
				}
			}
		}
		Collections.sort(target, JsonDiff.EQUAL_DESC_DIFF_ASC);
		return target;
	}

	private List<Tuple2<JsonNode, JsonDiff>> getItemCorrelation(List<JsonNode> list, JsonNode item, NodeInfos primaryNodes, int allowedDiffPrecision, int equalThreshold, Keys keys, String parentName, String listName,
			String nodePath, boolean nodeSensitiveName, boolean caseSensitiveValue) throws IOException {
		JsonDiff tmpDiff = null;
		List<Tuple2<JsonNode, JsonDiff>> closerNode = new ArrayList<>();

		for (JsonNode s : list) {
			tmpDiff = checkDiff(item, s, parentName, listName, nodePath, false, primaryNodes, allowedDiffPrecision, keys, true, nodeSensitiveName, caseSensitiveValue);

			if (tmpDiff.isNoDiff()) {
				closerNode.clear();
				closerNode.add(new Tuple2<JsonNode, JsonDiff>(s, tmpDiff));
				break;
			}

			if (keys.isHasKey(parentName, listName, nodePath, nodeSensitiveName)) {
				boolean sameKeyFound = checkKeyFound(item, s, allowedDiffPrecision, keys.findPKList(parentName, listName, nodePath, nodeSensitiveName), caseSensitiveValue);
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

	private boolean checkKeyFound(JsonNode item, JsonNode s, int allowedDiffPrecision, Key key, boolean caseSensitiveValue) {

		boolean found = true;
		//filter on key
		for (String where : key.getKeySet()) {
			if (!compare("", item.get(where), s.get(where), false, allowedDiffPrecision, caseSensitiveValue)) {
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
}
