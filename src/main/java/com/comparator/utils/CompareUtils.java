package com.comparator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CompareUtils {
	private static final String										CLAIM_ETERNAL_REF	= "(EA\\d+/\\d+)" + "|" + "(C\\d+/\\d+)" + "|" + "(ECLAIM\\d+/\\d+)";
	private static final String[]									ALERTS				= { "Unable to Load Conditions", "Contraindication between Drug", "may represent duplicate therapy",
			"has to managed or treated before the drug" };
	/*
	 * private static final String[] DERTY_CLEAN = { "?", "'", "\u2019", "'",
	 * "&quot;", "'", "&apos;", "'", "null", "", " 0.0 ", " 0 ", ".0\"", "\"",
	 * "false", "NO", "true", "YES", "may represent a duplication in therapy",
	 * "may represent duplicate therapy", "#", "", ".", "" };
	 */

	public static BiFunction<Integer, Integer, Integer>				nvlInteger			= (x, def) -> x == null ? def : x;
	public static BiFunction<Long, Long, Long>						nvlLong				= (x, def) -> x == null ? def : x;
	public static BiFunction<Double, Double, Double>				nvlDouble			= (x, def) -> x == null ? def : x;
	public static BiFunction<Boolean, Boolean, Boolean>				nvlBoolean			= (x, def) -> x == null ? def : x;
	public static BiFunction<BigDecimal, BigDecimal, BigDecimal>	nvlBigDecimal		= (x, def) -> x == null ? def : x;
	public static BiFunction<String, String, String>				nvlVarchar			= (x, def) -> x == null ? def : x;
	//public static Function<String, String> escape = (x) -> x.replace("\"", "\\\"");
	//private static double											allowedDiffPrecision		= 0.01;

	public static boolean isEqual(String rootName, JsonNode actual, JsonNode expected, boolean breakOnNullNode, boolean breakOnNullValue, int allowedDiffPrecision, boolean caseSensitive, String[] dertyClean) {
		boolean equal = false;
		//actual = actual == null ? JsonNodeFactory.instance.nullNode() : actual;
		//expected = expected == null ? JsonNodeFactory.instance.nullNode() : expected;

		BiPredicate<Boolean, Boolean> checkType = (a, e) -> a && e || (!breakOnNullValue) && (a || e);
		if (actual.isNull() && expected.isNull()) {
			return true;
		} else if (checkType.test(actual.isNumber(), expected.isNumber())) {
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
			String[] a = cleanNode(actual, caseSensitive, dertyClean);
			String[] e = cleanNode(expected, caseSensitive, dertyClean);
			equal = Arrays.equals(a, e);

		} else {//default equal
			String[] a = cleanNode(actual, caseSensitive, dertyClean);
			String[] e = cleanNode(expected, caseSensitive, dertyClean);
			equal = Arrays.equals(a, e);
		}
		return equal;
	}

	public static String[] cleanNode(JsonNode n, boolean caseSensitive, String[] dertyClean) {
		String in = n.asText().trim();
		String[] out = null;

		if (dertyClean != null) {
			for (int i = 0; i < dertyClean.length - 1; i += 2) {
				in = in.replace(dertyClean[i], dertyClean[i + 1]);
			}
		}

		if (in.equals("0") || in.equals("0.0")) {//@Now Zero is same as empty string
			in = "";
		}
		in = in.replaceAll(CLAIM_ETERNAL_REF, "CLAIM_ETERNAL_REF");
		String in2 = in;
		String in3 = in;
		if (Arrays.asList(ALERTS).stream().filter(p -> in2.contains(p)).count() > 0l) {
			in3 = Arrays.asList(ALERTS).stream().filter(p -> in2.contains(p)).findFirst().get();//ret.substring(0,  ret.indexOf("Unable to Load Conditions"));//moreDetails, alertMessage, "T78.40XA" != "W57.XXXA" => skip comparison, both not found
		}
		out = (caseSensitive ? in3 : in3.toLowerCase()).split(",");
		Arrays.sort(out);
		return out;
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
	public static String writeAsJson(/* String rootName, */ String[] keys, String[] values) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonFactory jfactory = new JsonFactory();
		JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
		jGenerator.writeStartObject();
		for (int i = 0; i < keys.length; i++) {
			jGenerator.writeStringField(keys[i], values[i]);
		}
		jGenerator.writeEndObject();
		jGenerator.close();

		String json = new String(stream.toByteArray(), "UTF-8");
		/*
		 * if (StringUtils.isEmpty(rootName)) { return json; } else { return
		 * "\"" + rootName + "\":" + json; }
		 */
		return json;
	}

	public static String writeJsonField(String field) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonFactory jfactory = new JsonFactory();
		JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
		jGenerator.writeString(field);
		jGenerator.close();
		String json = new String(stream.toByteArray(), "UTF-8");
		/*
		 * if (StringUtils.isEmpty(rootName)) { return json; } else { return
		 * "\"" + rootName + "\":" + json; }
		 */
		return json;
	}

	public static String jsonBeautify(String jsonStr, ObjectMapper mapperIndent) throws JsonParseException, IOException {
		if (!jsonStr.equals("")) {
			JsonFactory factory = mapperIndent.getFactory();
			JsonParser parser = factory.createParser(jsonStr);
			JsonNode compareResult = mapperIndent.readTree(parser);
			jsonStr = mapperIndent.writeValueAsString(compareResult);
		}
		return jsonStr;
	}
}
