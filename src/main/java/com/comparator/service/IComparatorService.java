package com.comparator.service;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.JsonNode;

public interface IComparatorService {
	public String compareJson(JsonNode actualJson, JsonNode expectedJson) throws IOException, ParseException;
}
