package com.comparator.model;

import java.io.IOException;
import java.util.Comparator;
import java.util.function.Consumer;

import org.jooq.lambda.tuple.Tuple2;
import static com.comparator.utils.CompareUtils.writeJsonField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class JsonDiff implements Comparable<JsonDiff> {

	@NonNull
	public StringBuilder	diff;
	@NonNull
	public Integer			diffCounter;
	@NonNull
	public Integer			equalCounter;

	/**
	 * diff1 +<b>,</b>+ diff2 <br>
	 * counter ++
	 * 
	 * @param diff
	 */
	public void appendDiff(JsonDiff diff) {
		stringAppender.accept(this.diff);
		this.diff.append(diff.diff);
		diffCounter += diff.diffCounter;
		equalCounter += diff.equalCounter;
	}

	/**
	 * add equal to equalCounter
	 * 
	 * @param diff
	 */
	public void incrementEqual(JsonDiff diff) {
		equalCounter += diff.equalCounter;
	}

	/**
	 * encapsulate diff in <b>{}</b>, => {diff}, if the length of diff is > 0
	 * 
	 * @return
	 */
	public JsonDiff addJsonBorder() {
		if (diff.length() > 0) {
			diff.insert(0, "{").append("}");
		}
		return this;
	}

	/**
	 * encapsulate diff in <b>[]</b>, => [diff], if the length of diff is > 0
	 * 
	 * @return
	 */
	public JsonDiff addArrayBorder() {
		if (diff.length() > 0) {
			diff.insert(0, "[").append("]");
		}
		return this;
	}

	/**
	 * add <b>NodeName:</b> @ the begin of diff, if the length of diff is > 0
	 * 
	 * @param rootName
	 * @return
	 * @throws IOException
	 */
	public JsonDiff setNodeName(String rootName) throws IOException {
		if (diff.length() > 0) {
			diff.insert(0, writeJsonField(rootName) + ":");//parent level, and grand parent 
		}
		return this;
	}

	/**
	 * 
	 * @param rootName
	 */
	/*
	 * public void setArrayName(String rootName) { if (diff.length() > 0) {
	 * diff.insert(0, "\"" + rootName + "\":[").append("]");//array level } }
	 */

	/**
	 * diff: ""<br>
	 * equal: 0<br>
	 * notEqual: 0
	 * 
	 * @return
	 */
	public static JsonDiff init() {
		return new JsonDiff(new StringBuilder(""), 0, 0);
	}

	/**
	 * diff: ""<br>
	 * equal: 1<br>
	 * notEqual: 0
	 * 
	 * @return
	 */
	public static JsonDiff noDiff() {
		return new JsonDiff(new StringBuilder(""), 0, 1);
	}

	/**
	 * diff: ""<br>
	 * equal: 0<br>
	 * notEqual: 1
	 * 
	 * @return
	 */
	public static JsonDiff diff(String diff) {
		return new JsonDiff(new StringBuilder(diff), 1, 0);
	}

	/**
	 * true if diffCounter is = to 0
	 * 
	 * @return
	 */
	public boolean isNoDiff() {
		return diffCounter == 0;
	}

	/**
	 * if length > 0 add "<b>,</b>" at the end of string
	 */
	@JsonIgnore
	private Consumer<StringBuilder> stringAppender = (sb) -> {
		if (sb.length() > 0) {
			sb.append(",");
		}
	};

	public String toString() {
		return "diff: '" + this.diff + "', equalCounter: '" + this.equalCounter + "', diffCounter: '" + this.diffCounter + "'";
	}

	@Override
	public int compareTo(JsonDiff o) {
		int i = compareEqualAndDiff(this, o);
		return i;
	}

	public static Comparator<Tuple2<JsonNode, JsonDiff>> EQUAL_DESC_DIFF_ASC = new Comparator<Tuple2<JsonNode, JsonDiff>>() {
		@Override
		public int compare(Tuple2<JsonNode, JsonDiff> o1, Tuple2<JsonNode, JsonDiff> o2) {
			int i = compareEqualAndDiff(o1.v2, o2.v2);
			return i;
		}
	};

	public static int compareEqualAndDiff(JsonDiff o1, JsonDiff o2) {
		Integer e1 = o1.equalCounter;
		Integer e2 = o2.equalCounter;
		if (e1 > e2) {
			return -1;
		} else if (e1 < e2) {
			return 1;
		} else {
			Integer d1 = o1.diffCounter;
			Integer d2 = o2.diffCounter;
			if (d1 > d2) {
				return 1;
			} else if (d1 < d2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
