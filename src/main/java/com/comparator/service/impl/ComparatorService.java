package com.comparator.service.impl;

import static com.comparator.utils.CompareUtils.compare;
//import static com.comparator.utils.CompareUtils.isEmptyNode;
import static com.comparator.utils.CompareUtils.writeAsJson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IteratorUtils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.comparator.service.IComparatorService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ComparatorService implements IComparatorService {

	private List<String>	skipNodes	= Arrays.asList("xmlTag", "limitWarningMessage", "conditionDescription");
	@Autowired
	@Qualifier("mapperIndent")
	private ObjectMapper	mapperIndent;

	@Override
	public String compareJson(JsonNode actualJson, JsonNode expectedJson) throws IOException, ParseException {
		String diff = "";

		JsonNode actual = actualJson;// mapperIndent.readTree(actualJson);
		JsonNode expected = expectedJson;//mapperIndent.readTree(expectedJson);
		if (actual == null || expected == null) {
			diff = "actual/expected input null";
		} else {
			diff = checkDiff(actual, expected, "root", false);
			if (!diff.equals("")) {
				diff = "{" + diff + "}";
				JsonFactory factory = mapperIndent.getFactory();
				JsonParser parser = factory.createParser(diff);
				JsonNode compareResult = mapperIndent.readTree(parser);
				diff = mapperIndent.writeValueAsString(compareResult);
			}
		}

		return diff;

	}

	private String checkDiff(JsonNode rootLevelActual, JsonNode rootLevelExpected, String rootName, boolean breakOnNull) throws IOException {
		String nodeNotFound = "missing node";
		String itemFound = "%s.Item[%s]: %s";
		String itemNotFound = "missing item";
		String noUniqueItem = "No unique item found";
		String[] nodeCompare = new String[] { "actual", "expected" };
		Consumer<StringBuilder> separate = (sb) -> {
			if (sb.length() > 0) {
				sb.append(",");
			}
		};

		//LEVEL 1 : check root if exist
		//------------------------------
		boolean isActualNull = false;
		boolean isExpectedNull = false;
		if (rootLevelActual == null && rootLevelExpected == null/* 0 0 */) {
			return "";//writeAsJson(rootName, nodeCompare, new String[] { nodeNotFound, nodeNotFound });//last child
		} else if (rootLevelActual == null && rootLevelExpected != null/* 0 1 */) {//check input if valid
			if (breakOnNull)
				return writeAsJson(rootName, nodeCompare, new String[] { nodeNotFound, rootLevelExpected.toString() });//last child

			isActualNull = true;
		} else if (rootLevelActual != null && rootLevelExpected == null/* 1 0 */) {
			if (breakOnNull)
				return writeAsJson(rootName, nodeCompare, new String[] { rootLevelActual.toString(), nodeNotFound });//last child

			isExpectedNull = true;
		} else if (rootLevelActual.isNull() && rootLevelExpected.isNull()) {//null null
			return "";
		} else if ((rootLevelActual.isNull() || rootLevelExpected.isNull())) {// not null null
			if (breakOnNull)
				return writeAsJson(rootName, nodeCompare, new String[] { rootLevelActual.toString(), rootLevelExpected.toString() });//last child

			isActualNull = rootLevelActual.isNull();
			isExpectedNull = rootLevelExpected.isNull();

		}
		//input exist, so compare the items: KEY/VALUE

		//LEVEL 2 : load all the child's of root in one list
		//--------------------------------------------
		// Merge: put the keys in one list
		Iterator<String> aFieldsItr = isActualNull ? IteratorUtils.emptyIterator() : rootLevelActual.fieldNames();
		Iterator<String> eFieldsItr = isExpectedNull ? IteratorUtils.emptyIterator() : rootLevelExpected.fieldNames();
		HashSet<String> fieldsSet = new HashSet<String>();

		while (eFieldsItr.hasNext())
			fieldsSet.add(eFieldsItr.next());
		while (aFieldsItr.hasNext())
			fieldsSet.add(aFieldsItr.next());
		// New iterator of unique objects
		Iterator<String> allFieldsItr = fieldsSet.iterator();

		if (allFieldsItr.hasNext()) {//Loop over the Node's
			//LEVEL 3: loop over the child
			StringBuilder output = new StringBuilder();
			while (allFieldsItr.hasNext()) {
				String parentName = allFieldsItr.next();
				JsonNode parentActual = isActualNull ? null : rootLevelActual.get(parentName);
				JsonNode parentExpect = isExpectedNull ? null : rootLevelExpected.get(parentName);
				String tmpDiff = checkDiff(parentActual, parentExpect, parentName, breakOnNull);

				if (!tmpDiff.equals("")) {
					separate.accept(output);
					/*
					 * if (output.length() > 0) { output.append(","); }
					 * 
					 */
					output.append(tmpDiff);
				}
			}
			if (output.length() > 0) {
				output.insert(0, "\"" + rootName + "\":{").append("}");//parent level, and grand parent
			}
			return output.toString();
		} else {//LEVEL 4: one child, Single Node

			if (!isActualNull && rootLevelActual.isArray() || !isExpectedNull && rootLevelExpected.isArray()) {//loop as List
				StringBuilder output = new StringBuilder();
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

				String[] keys = getListPK(rootName);

				for (JsonNode itemE : expectedAsList) {
					List<JsonNode> sA = getItemById(actualAsList, keys, itemE);

					if (sA.size() > 1l) {
						if (keys == null) {//no unique key, all fiel are the key, and equal
							JsonNode itemA = sA.get(0);
							actualAsList.remove(itemA);
						} else {
							String itemFoundKey = generateItemNotFoundStr(keys, itemE);
							separate.accept(output);
							output.append(writeAsJson(null, nodeCompare, new String[] { noUniqueItem, String.format(itemFound, rootName, expectedAsListCopy.indexOf(itemE), itemFoundKey) }));
							continue;
						}
					} else

					if (sA.size() == 1) {
						JsonNode itemA = sA.get(0);
						String tmpDiff = checkDiff(itemA, itemE, rootName + ".Item[" + expectedAsListCopy.indexOf(itemE) + "]", breakOnNull);
						if (!tmpDiff.equals("")) {
							/*
							 * if (output.length() > 0) { output.append(","); }
							 */
							separate.accept(output);
							output.append("{" + tmpDiff + "}");
						}
						actualAsList.remove(itemA);
					} else {//size 0
						String itemFoundKey = generateItemNotFoundStr(keys, itemE);
						/*
						 * if (output.length() > 0) { output.append(","); }
						 */
						separate.accept(output);
						output.append(writeAsJson(null, nodeCompare, new String[] { itemNotFound, String.format(itemFound, rootName, expectedAsListCopy.indexOf(itemE), itemFoundKey) }));
					}
				}
				if (actualAsList.size() != 0) {
					for (JsonNode n : actualAsList) {
						String itemFoundKey = generateItemNotFoundStr(keys, n);
						/*
						 * if (output.length() > 0) { output.append(","); }
						 */
						separate.accept(output);
						output.append(writeAsJson(null, nodeCompare, new String[] { String.format(itemFound, rootName, actualAsListCopy.indexOf(n), itemFoundKey), itemNotFound }));
					}
				}

				if (output.length() > 0) {
					output.insert(0, "\"" + rootName + "\":[").append("]");//array level
				}
				return output.toString();
			} else {//not an array
				boolean equal = skipNodes.contains(rootName) ? true : compare(rootName, rootLevelActual, rootLevelExpected, breakOnNull);
				if (equal) {
					return "";
				} else {
					return writeAsJson(rootName, nodeCompare, new String[] { isActualNull ? rootLevelActual + "" : rootLevelActual.toString(), isExpectedNull ? rootLevelExpected + "" : rootLevelExpected.toString() });//last child
				}

			}
		}

	}

	private List<JsonNode> getItemById(List<JsonNode> list, String[] keys, JsonNode item) throws IOException {
		List<JsonNode> matches = new ArrayList<>();
		for (JsonNode s : list) {
			if (keys != null) {//filter on key
				for (int whereI = 0; whereI < keys.length; whereI++) {
					if (!compare("", item.get(keys[whereI]), s.get(keys[whereI]), false)) {
						continue;
					}
				}
				matches.add(s);
			} else {//filter on equal all fields
				String tmpDiff = checkDiff(item, s, "", false);
				if (tmpDiff.equals("")) {
					matches.add(s);
				}
			}
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

	private String generateItemNotFoundStr(String keys[], JsonNode n) {
		String itemNotFound = "";
		if (keys != null) {
			for (String keyI : keys) {
				itemNotFound += itemNotFound.length() > 0 ? " - " : "";
				itemNotFound += keyI;
				itemNotFound += "[";
				itemNotFound += n.get(keyI).toString();
				itemNotFound += "]";

			}
		} else {
			itemNotFound = n.toString();
		}
		return itemNotFound;
	}

	private String[] getListPK(String rootName) {
		String[] keys = null;
		/*if (rootName.equals("listServiceItem")) {
			keys = new String[] { "serviceCode", "rownbr" };
		} else if (rootName.equals("listDiagnosis")) {
			keys = new String[] { "orderId" };
		} 
			  else if (rootName.equals("listBenefitLink")) { keys = new
			  String[] { "fobId", "benefitId", "other" }; }
			 else if (rootName.equals("listBenefitServiceItem")) {
			keys = new String[] { "serviceCode", "rowNbr" };
		} else if (rootName.equals("listBenefitLinkType")) {
			keys = new String[] { "id" };
		} else if (rootName.equals("listBenefitConstraintTOB2")) {
			keys = new String[] { "orderId" };
		} else if (rootName.equals("listApplicableRule")) {
			keys = new String[] { "ruleId" };
		} else if (rootName.equals("approvalItems")) {
			keys = new String[] { "approvalItemId" };
		} 
			  else if (rootName.equals("claimsEditAlerts")) { keys = new
			  String[] { "moduleId", "serviceCode", "rowNbr", "errorRecord" };
			 }
			  else if (rootName.equals("listClaimValidation")) {
			keys = new String[] { "id" };
		} else if (rootName.equals("listBenefitConstraintTOB1")) {
			keys = new String[] { "orderId" };//TODO CHECK WITH SISI
		} else {
			keys = null;
		}*/
		return keys;
	}
}
