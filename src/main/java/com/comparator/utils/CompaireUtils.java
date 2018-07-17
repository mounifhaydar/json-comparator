package com.comparator.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;

public class CompaireUtils {

	public static Function<String, String> escape = (x) -> x.replace("\"", "\\\"");

	public static boolean compare(JsonNode actual, JsonNode expected) {
		boolean equal = false;
		if (actual.isNumber() && expected.isNumber()) {
			double a = actual.asDouble();
			double e = expected.asDouble();
			if (a != e) {
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
			String a = actual.asText();
			String e = expected.asText();
			if (!a.equals(e)) {
				equal = false;
			} else {
				equal = true;
			}
		} else {//default equal
			if (!actual.equals(expected)) {
				equal = false;
			} else {
				equal = true;
			}
		}
		return equal;
	}

	/**
	 * return "rootName":{"actual": "f1", "expected":"f2"}
	 * if rootName is empty => {"actual": "f1", "expected":"f2"}
	 * @param rootName
	 * @param f1
	 * @param f2
	 * @return
	 * @throws IOException
	 */
	public static String writePairValues(String rootName, String f1, String f2) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonFactory jfactory = new JsonFactory();
		JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
		jGenerator.writeStartObject();
		jGenerator.writeStringField("actual", f1);
		jGenerator.writeStringField("expected", f2);
		jGenerator.writeEndObject();
		jGenerator.close();
		//
		String json = new String(stream.toByteArray(), "UTF-8");
		if (StringUtils.isEmpty(rootName)) {
			return json;
		} else {
			return "\"" + rootName + "\":" + json;
		}

	}

	/**
	 * return {"fieldName": "value"}
	 * 
	 * @param rootName
	 * @param f1
	 * @param f2
	 * @return
	 * @throws IOException
	 */
	public static String writeSingleValue(String fieldName, String value) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		JsonFactory jfactory = new JsonFactory();
		JsonGenerator jGenerator = jfactory.createGenerator(stream, JsonEncoding.UTF8);
		jGenerator.writeStartObject();
		jGenerator.writeStringField(fieldName, value);
		jGenerator.writeEndObject();
		jGenerator.close();
		String json = new String(stream.toByteArray(), "UTF-8");
		return json;
	}
}
