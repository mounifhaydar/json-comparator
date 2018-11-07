package com.comparator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.function.BiFunction;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CompareUtils {
	private static final String[]									CLAIM_NUMBER_SLASH_ASOAP	= { "([A-Za-z0-9_]+)/([0-9]+),", "Y/X,", "([A-Za-z0-9_]+)/([0-9]+)\\)", "Y/X)" };	//EA0002965877/1
	private static final String										UNABLE_TO_LOAD_CONDITIONS	= "Unable to Load Conditions";
	private static final String[]									DERTY_CLEAN					= { "?", "'",
			"\u2019"/* "’" */, "'", "&quot;", "'", "&apos;", "'", "null", "", " 0.0 ", " 0 ", ".0\"", "\"" ,"false","NO","true","YES"};

	//Dupication Terapy
	private static final String[]									DUPLICATE_THERAPY			= { "may represent a duplication in therapy", "may represent duplicate therapy" };
	//private static final String										START_OF_CLAIM_AND_DRUG		= "of Claim #";
	//private static final String										START_OF_CLAIM_AND_DRUGS	= "of Current Claim and Drug(s) (";
	//private static final String										END_OF_CLAIM_AND_DRUG		= ") are members of the same class description and may represent duplicate therapy";

	public static BiFunction<Integer, Integer, Integer>				nvlInteger					= (x, def) -> x == null ? def : x;
	public static BiFunction<Long, Long, Long>						nvlLong						= (x, def) -> x == null ? def : x;
	public static BiFunction<Double, Double, Double>				nvlDouble					= (x, def) -> x == null ? def : x;
	public static BiFunction<Boolean, Boolean, Boolean>				nvlBoolean					= (x, def) -> x == null ? def : x;
	public static BiFunction<BigDecimal, BigDecimal, BigDecimal>	nvlBigDecimal				= (x, def) -> x == null ? def : x;
	public static BiFunction<String, String, String>				nvlVarchar					= (x, def) -> x == null ? def : x;
	//public static Function<String, String> escape = (x) -> x.replace("\"", "\\\"");
	//private static double											allowedDiffPrecision		= 0.01;

	public static boolean compare(String rootName, JsonNode actual, JsonNode expected, boolean breakOnNull, int allowedDiffPrecision) {
		boolean equal = false;
		actual = actual == null ? JsonNodeFactory.instance.nullNode() : actual;
		expected = expected == null ? JsonNodeFactory.instance.nullNode() : expected;

		if (actual.isNull() && expected.isNull()) {
			return true;
		} else if (actual.isNumber() && expected.isNumber()) {
			double a = actual.asDouble();
			double e = expected.asDouble();

			if (Math.abs(a - e) != 0 && Math.abs(a - e) >= Math.pow(10, -allowedDiffPrecision)) {
				equal = false;
			} else {
				equal = true;
			}
		} else if (actual.isBoolean() && expected.isBoolean()) {
			boolean a = actual.asBoolean();
			boolean e = expected.asBoolean();
			if (a != e) {
				equal = false;
			} else {
				equal = true;
			}
		} else if (actual.isTextual() && expected.isTextual()) {
			String[] a = cleanNode(actual);
			String[] e = cleanNode(expected);
			/*
			 * if (rootName.equals("alertMessage")) { a = a.replace("&quot;",
			 * "'").replace("&apos;", "'"); e = e.replace("&quot;",
			 * "'").replace("&apos;", "'"); a = a.substring(0,
			 * a.indexOf("Unable to Load Conditions") > -1 ?
			 * a.indexOf("Unable to Load Conditions") : 0); //"T78.40XA" !=
			 * "W57.XXXA" => skip comparison, both not found e = e.substring(0,
			 * e.indexOf("Unable to Load Conditions") > -1 ?
			 * e.indexOf("Unable to Load Conditions") : 0); //"T78.40XA" !=
			 * "W57.XXXA" => skip comparison, both not found } else if
			 * (rootName.equals("moreDetails")) { a = a.replace("&quot;",
			 * "'").replace("&apos;", "'"); e = e.replace("&quot;",
			 * "'").replace("&apos;", "'"); }
			 */
			equal = Arrays.equals(a, e);

		} else {//default equal
			String[] a = cleanNode(actual);
			String[] e = cleanNode(expected);
			equal = Arrays.equals(a, e);
		}
		return equal;
	}

	public static String[] cleanNode(JsonNode n) {
		String in = n.asText().trim();
		String[] out = null;

		for (int i = 0; i < DERTY_CLEAN.length - 1; i += 2) {
			in = in.replace(DERTY_CLEAN[i], DERTY_CLEAN[i + 1]);
		}

		if (in.equals("0") || in.equals("0.0")) {//@Now Zero is same as empty string
			in = "";
		} else if (in.indexOf(UNABLE_TO_LOAD_CONDITIONS) > -1) {
			in = UNABLE_TO_LOAD_CONDITIONS;//ret.substring(0,  ret.indexOf("Unable to Load Conditions"));//moreDetails, alertMessage, "T78.40XA" != "W57.XXXA" => skip comparison, both not found
		} else if (in.indexOf(DUPLICATE_THERAPY[0]) > -1 || in.indexOf(DUPLICATE_THERAPY[1]) > -1) {
			/*
			 * for (int i = 0; i < CLAIM_NUMBER_SLASH_ASOAP.length - 1; i += 2)
			 * {//ECLAIM0002965877 and EA0002965877 are the same in =
			 * in.replaceAll(CLAIM_NUMBER_SLASH_ASOAP[i],
			 * CLAIM_NUMBER_SLASH_ASOAP[i + 1]); }
			 * 
			 * if (in.indexOf(START_OF_CLAIM_AND_DRUGS) > -1) {//[drug 1, drug
			 * 2] and [drug 2, drug 1] are the same in =
			 * in.substring(in.indexOf(START_OF_CLAIM_AND_DRUG) +
			 * START_OF_CLAIM_AND_DRUG.length(),
			 * in.lastIndexOf(END_OF_CLAIM_AND_DRUG)); }
			 */
			in = DUPLICATE_THERAPY[0];
		}
		out = in.split(",");
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
