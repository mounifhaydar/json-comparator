package com.comparator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.BiFunction;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class CompareUtils {
	public static BiFunction<Integer, Integer, Integer>				nvlInteger				= (x, def) -> x == null ? def : x;
	public static BiFunction<Long, Long, Long>						nvlLong					= (x, def) -> x == null ? def : x;
	public static BiFunction<Double, Double, Double>				nvlDouble				= (x, def) -> x == null ? def : x;
	public static BiFunction<Boolean, Boolean, Boolean>				nvlBoolean				= (x, def) -> x == null ? def : x;
	public static BiFunction<BigDecimal, BigDecimal, BigDecimal>	nvlBigDecimal			= (x, def) -> x == null ? def : x;
	public static BiFunction<String, String, String>				nvlVarchar				= (x, def) -> x == null ? def : x;
	//public static Function<String, String> escape = (x) -> x.replace("\"", "\\\"");
	private static double											allowedDiffPrecision	= 0.0001;

	public static boolean compare(String rootName, JsonNode actual, JsonNode expected, boolean breakOnNull) {
		boolean equal = false;
		actual = actual == null ? JsonNodeFactory.instance.nullNode() : actual;
		expected = expected == null ? JsonNodeFactory.instance.nullNode() : expected;

		if (actual.isNull() && expected.isNull()) {
			return true;
		} else if (actual.isNumber() && expected.isNumber()) {
			double a = actual.asDouble();
			double e = expected.asDouble();
			if (Math.abs(a - e) > allowedDiffPrecision) {
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
			String a = cleanNode(actual);
			String e = cleanNode(expected);
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
			if (!a.equals(e)) {
				equal = false;
			} else {
				equal = true;
			}
		} else {//default equal
			String a = cleanNode(actual);
			String e = cleanNode(expected);
			if (a.equals(e)) {
				equal = true;
			} else {
				equal = false;
			}
		}
		return equal;
	}

	private static String cleanNode(JsonNode n) {
		String ret = n.asText().trim().replace("&quot;", "'").replace("&apos;", "'").replace("null", "").replace(".0\"", "\"");
		if (ret.equals("0")) {//@Now Zero is same as empty string
			ret = "";
		} else if (ret.indexOf("Unable to Load Conditions") > -1) {
			ret = ret.substring(0, ret.indexOf("Unable to Load Conditions") > -1 ? ret.indexOf("Unable to Load Conditions") : 0);//moreDetails, alertMessage, "T78.40XA" != "W57.XXXA" => skip comparison, both not found
		}
		return ret;
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
	public static String writeAsJson(String rootName, String[] keys, String[] values) throws IOException {
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
		if (StringUtils.isEmpty(rootName)) {
			return json;
		} else {
			return "\"" + rootName + "\":" + json;
		}
	}
}
