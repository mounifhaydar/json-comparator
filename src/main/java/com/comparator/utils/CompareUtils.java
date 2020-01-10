package com.comparator.utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class CompareUtils {

	/*
	 * private static final String[] DERTY_CLEAN = { "?", "'", "\u2019", "'",
	 * "&quot;", "'", "&apos;", "'", "null", "", " 0.0 ", " 0 ", ".0\"", "\"",
	 * "false", "NO", "true", "YES", "may represent a duplication in therapy",
	 * "may represent duplicate therapy", "#", "", ".", "" };
	 */

	public static BiFunction<Integer, Integer, Integer>				nvlInteger		= (x, def) -> x == null ? def : x;
	public static BiFunction<Long, Long, Long>						nvlLong			= (x, def) -> x == null ? def : x;
	public static BiFunction<Double, Double, Double>				nvlDouble		= (x, def) -> x == null ? def : x;
	public static BiFunction<Boolean, Boolean, Boolean>				nvlBoolean		= (x, def) -> x == null ? def : x;
	public static BiFunction<BigDecimal, BigDecimal, BigDecimal>	nvlBigDecimal	= (x, def) -> x == null ? def : x;
	public static BiFunction<String, String, String>				nvlVarchar		= (x, def) -> x == null ? def : x;
	//public static Function<String, String> escape = (x) -> x.replace("\"", "\\\"");
	//private static double											allowedDiffPrecision		= 0.01;

	public static boolean isEqual(String rootName, JsonNode actual, JsonNode expected, boolean breakOnNullNode, boolean breakOnNullValue, int allowedDiffPrecision, boolean caseSensitive, String[] dertyClean,
			Map<String, String> regex, String[] dictionary, boolean breakOnTypeMismatch) {
		boolean equal = false;
		BiPredicate<Boolean, Boolean> checkType = (a, e) -> a && e || (!breakOnNullValue) && (a || e);

		if (actual.isNull() && expected.isNull()) {
			return true;
		} else if (breakOnNullValue && (actual.isNull() || expected.isNull())) {
			return false;
		} else if (checkType.test(actual.isNumber(), expected.isNumber()) || !breakOnTypeMismatch && relatedType(actual, expected, Number.class)) {
			double a = actual.asDouble();
			double e = expected.asDouble();

			if (Math.abs(a - e) != 0 && (allowedDiffPrecision == 0 || Math.abs(a - e) >= Math.pow(10, -allowedDiffPrecision))) {
				equal = false;
			} else {
				equal = true;
			}
		} else if (checkType.test(actual.isBoolean(), expected.isBoolean())) {
			boolean a = actual.asBoolean();
			boolean e = expected.asBoolean();
			if (a != e) {
				equal = false;
			} else {
				equal = true;
			}
		} else if (checkType.test(actual.isTextual(), expected.isTextual())) {
			String actualA = Optional.ofNullable(actual.textValue()).orElse("");
			String actualE = Optional.ofNullable(expected.textValue()).orElse("");
			String[] a = cleanNode(actualA, caseSensitive, dertyClean, regex, dictionary);
			String[] e = cleanNode(actualE, caseSensitive, dertyClean, regex, dictionary);
			equal = Arrays.equals(a, e);
		} else {//default equal
			String[] a = cleanNode(actual.asText(), caseSensitive, dertyClean, regex, dictionary);
			String[] e = cleanNode(expected.asText(), caseSensitive, dertyClean, regex, dictionary);
			equal = Arrays.equals(a, e);
		}

		return equal;
	}

	public static String[] cleanNode(String value/* JsonNode n */, boolean caseSensitive, String[] dertyClean, Map<String, String> regex, String[] dictionary) {
		String in = value;//n.asText().trim();
		String[] out = null;

		/*
		 * if (in.equals("0") || in.equals("0.0")) {//@Now Zero is same as empty
		 * string in = ""; }
		 */

		//Priority 1: Dictionary
		if (dictionary != null) {
			String in2 = in;
			if (Arrays.asList(dictionary).stream().filter(p -> in2.contains(p)).count() > 0l) {
				in = Arrays.asList(dictionary).stream().filter(p -> in2.contains(p)).findFirst().get();//ret.substring(0,  ret.indexOf("Unable to Load Conditions"));//moreDetails, alertMessage, "T78.40XA" != "W57.XXXA" => skip comparison, both not found
			}
		}

		//Priority 2: RegEx
		if (regex != null) {
			for (Entry<String, String> e : regex.entrySet()) {
				in = in.replaceAll(e.getKey(), e.getValue());
			}
		}

		//Priority 3: Dirty
		if (dertyClean != null) {
			for (int i = 0; i < dertyClean.length; i += 1) {
				in = in.replace(dertyClean[i], "");
			}
		}

		out = (caseSensitive ? in : in.toLowerCase()).split(",");//TODO ADD SPLITER[] IN INPUT PARAM, THEN FOR EACH SPLIT AND COLLECT
		Arrays.sort(out);
		return out;
	}

	public static <T> boolean relatedType(JsonNode actual, JsonNode expected, Class<T> t) {
		boolean aResult = false;
		boolean bResult = false;
		if (t == Number.class) {
			try {
				Double.parseDouble(actual.textValue());
				aResult = true;
			} catch (NullPointerException ex) {

			} catch (NumberFormatException ex) {

			}

			try {
				Double.parseDouble(expected.textValue());
				bResult = true;
			} catch (NullPointerException ex) {

			} catch (NumberFormatException ex) {

			}
			
		}
		return aResult || bResult;
	}

	/**
	 * return <b>"rootName":{ "key[i]" : value[i] }</b> <br>
	 * if rootName is empty => <b>{ "key[i]" : value[i] }</b>
	 * 
	 * @param rootName
	 * @param keys
	 * @param values
	 * @return
	 * @throws IOException
	 */
	/*
	 * public static String writeAsJson(String[] keys, String[] values) throws
	 * IOException { ByteArrayOutputStream stream = new ByteArrayOutputStream();
	 * JsonFactory jfactory = new JsonFactory(); JsonGenerator jGenerator =
	 * jfactory.createGenerator(stream, JsonEncoding.UTF8);
	 * jGenerator.writeStartObject(); for (int i = 0; i < keys.length; i++) {
	 * jGenerator.writeStringField(keys[i], values[i]); }
	 * jGenerator.writeEndObject(); jGenerator.close();
	 * 
	 * String json = new String(stream.toByteArray(), "UTF-8"); return json; }
	 */

	public static void createJson(String[] keys, String[] values, ObjectNode objectNode) {
		for (int i = 0; i < keys.length; i++) {
			objectNode.set(keys[i], writeStringField(values[i]));
		}
	}

	public static TextNode writeStringField(String value) {
		return new TextNode(value);
	}

	/*
	 * public static String writeJsonField(String field) throws IOException {
	 * ByteArrayOutputStream stream = new ByteArrayOutputStream(); JsonFactory
	 * jfactory = new JsonFactory(); JsonGenerator jGenerator =
	 * jfactory.createGenerator(stream, JsonEncoding.UTF8);
	 * jGenerator.writeString(field); jGenerator.close(); String json = new
	 * String(stream.toByteArray(), "UTF-8");
	 * 
	 * if (StringUtils.isEmpty(rootName)) { return json; } else { return "\"" +
	 * rootName + "\":" + json; }
	 * 
	 * return json; }
	 */

	public static String jsonBeautify(String jsonStr, ObjectMapper mapperIndent) throws JsonParseException, IOException {
		if (!jsonStr.equals("")) {
			JsonFactory factory = mapperIndent.getFactory();
			JsonParser parser = factory.createParser(jsonStr);
			JsonNode compareResult = mapperIndent.readTree(parser);
			jsonStr = mapperIndent.writeValueAsString(compareResult);
		}
		return jsonStr;
	}

	public static JsonNode getIgnoreCase(String nodeName, JsonNode jsonNode) {
		JsonNode result = jsonNode.get(nodeName);
		if (result == null) {
			Iterator<String> fieldNames = jsonNode.fieldNames();
			while (fieldNames.hasNext()) {
				String fieldName = fieldNames.next();
				if (nodeName.equalsIgnoreCase(fieldName)) {
					result = jsonNode.get(fieldName);
					break;
				}
			}
		}
		return result;
	}
}
