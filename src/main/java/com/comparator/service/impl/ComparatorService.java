package com.comparator.service.impl;

import static com.comparator.utils.CompaireUtils.writePairValues;
import static com.comparator.utils.CompaireUtils.writeSingleValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.comparator.service.IComparatorService;
import com.comparator.utils.CompaireUtils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ComparatorService implements IComparatorService {

	@Autowired
	@Qualifier("mapperIndent")
	private ObjectMapper mapperIndent;

	@Override
	public String compareJson(JsonNode actualJson, JsonNode expectedJson) throws IOException, ParseException {
		String diff = "";

		JsonNode actual = actualJson;// mapperIndent.readTree(actualJson);
		JsonNode expected = expectedJson;//mapperIndent.readTree(expectedJson);
		if (actual == null || expected == null) {
			diff = "actual/expected input null";
		} else {
			diff = checkDiff(actual, expected, "root");
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

	private String checkDiff(JsonNode actual, JsonNode expected, String rootName) throws IOException {
		String itemKey = "%s.Item[%s]";
		String itemValue = "%s not found in input %s";
		String result = "";

		if (actual == null) {
			return writePairValues(rootName, "missing node", expected + "");//last child
		} else if (expected == null) {
			return writePairValues(rootName, actual + "", "missing node");//last child
		} else if (actual.isNull() && actual.isNull()) {
			return "";
		} else if (actual.isNull() || expected.isNull()) {
			return writePairValues(rootName, actual + "", expected + "");//last child
		} else {
			Iterator<String> itrE = expected.fieldNames();
			Iterator<String> itrA = actual.fieldNames();
			HashSet<String> listAll = new HashSet<String>();
			// Merge
			while (itrE.hasNext())
				listAll.add(itrE.next());
			while (itrA.hasNext())
				listAll.add(itrA.next());
			// New iterator of unique objects
			Iterator<String> itr = listAll.iterator();

			if (!itr.hasNext()) {
				if (actual.isArray() || expected.isArray()) {//loop as List
					//actual.
					List<JsonNode> actualAsList = new ArrayList<>();
					List<JsonNode> expectedAsList = new ArrayList<>();
					for (JsonNode a : actual) {
						actualAsList.add(a);
					}
					for (JsonNode e : expected) {
						expectedAsList.add(e);
					}

					int itemIndexE = -1;
					int itemIndexA = -1;
					boolean firstItem = true;
					boolean notEqualFound = false;
					String[] keys = getListPK(rootName);

					for (JsonNode itemE : expectedAsList) {
						itemIndexE++;
						Optional<JsonNode> a = actualAsList.stream().filter(s -> {
							if (keys != null) {//filter on key
								for (int whereI = 0; whereI < keys.length; whereI++) {
									if (!s.get(keys[whereI]).equals(itemE.get(keys[whereI]))) {
										return false;
									}
								}
								return true;
							} else {//filter on equal all fields
								return s.equals(itemE);
							}
						}).findFirst();
						if (a.isPresent()) {
							JsonNode itemA = a.get();
							String tmpDiff = checkDiff(itemA, itemE, rootName + ".Item[" + itemIndexE + "]");
							if (!tmpDiff.equals("")) {
								notEqualFound = true;
								//item level
								if (firstItem) {
									firstItem = false;
									result = "{" + tmpDiff + "}";//{}	
								} else {
									result = result + "," + "{" + tmpDiff + "}";//{},{},{}...
								}
							}
							itemIndexA++;
							actualAsList.remove(itemA);
						} else {
							notEqualFound = true;
							if (firstItem) {
								firstItem = false;
								result = writeSingleValue(String.format(itemKey, rootName, itemIndexE), String.format(itemValue, itemE.toString(), "actual"));//{}
							} else {
								result = result + "," + writeSingleValue(String.format(itemKey, rootName, itemIndexE), String.format(itemValue, itemE.toString(), "actual"));//{},{},{}...
							}
						}
					}
					if (actualAsList.size() != 0) {
						notEqualFound = true;
						for (JsonNode n : actualAsList) {
							itemIndexA++;
							if (firstItem) {
								firstItem = false;
								result = writeSingleValue(String.format(itemKey, rootName, itemIndexA), String.format(itemValue, n.toString(), "expected"));//{}
							} else {
								result = result + "," + writeSingleValue(String.format(itemKey, rootName, itemIndexA), String.format(itemValue, n.toString(), "expected"));//{},{},{}...
							}
						}
					}

					if (notEqualFound) {
						return result = "\"" + rootName + "\":[" + result + "]";//array level
					} else {
						return "";
					}
				} else {//not  a json array
					boolean equal = CompaireUtils.compare(actual, expected);
					if (equal) {
						return "";
					} else {
						return writePairValues(rootName, actual.asText(), expected.asText());//last child
					}
				}
			} else {//loop
				boolean firstItem = true;
				boolean notEqualFound = false;
				while (itr.hasNext()) {
					String fieldName = itr.next();
					JsonNode childExpect = expected.get(fieldName);
					JsonNode childActual = actual.get(fieldName);

					String tmpDiff = checkDiff(childActual, childExpect, fieldName);
					if (!tmpDiff.equals("")) {
						notEqualFound = true;
						//child level
						if (firstItem) {
							firstItem = false;
							result = tmpDiff;//{}	
						} else {
							result = result + "," + tmpDiff;//{},{},{}...
						}

					}

				}
				if (notEqualFound) {
					result = "\"" + rootName + "\":{" + result + "}";//parent level, and grand parent
				}
			}
		}
		return result;
	}

	private String[] getListPK(String rootName) {
		String[] keys = null;
		if (rootName.equals("listServiceItem")) {
			keys = new String[] { "serviceCode", "rownbr" };
		} else if (rootName.equals("listDiagnosis")) {
			keys = new String[] { "orderId" };
		} else if (rootName.equals("listBenefitLink")) {
			keys = new String[] { "fobId", "benefitId", "other" };
		} else if (rootName.equals("listBenefitServiceItem")) {
			keys = new String[] { "serviceCode", "rowNbr" };
		} else if (rootName.equals("listBenefitLinkType")) {
			keys = new String[] { "id" };
		} else if (rootName.equals("listBenefitConstraintTOB2")) {
			keys = new String[] { "orderId" };
		} else if (rootName.equals("listApplicableRule")) {
			//keys = new String[] { "orderId" };TODO
		} else if (rootName.equals("approvalItems")) {
			//keys = new String[] { "orderId" };TODO
		} else if (rootName.equals("claimsEditAlerts")) {
			//keys = new String[] { "orderId" };TODO
		} else if (rootName.equals("listClaimValidation")) {
			//keys = new String[] { "orderId" };TODO
		} else if (rootName.equals("approvalItems")) {
			//keys = new String[] { "orderId" };TODO
		} else {
			keys = null;
		}
		return keys;
	}
}
