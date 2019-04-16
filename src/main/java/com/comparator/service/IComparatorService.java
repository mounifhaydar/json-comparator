package com.comparator.service;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.comparator.model.CompareInput;
import com.comparator.model.CompareOutput;
import com.comparator.model.JsonDiff;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IComparatorService {
	public JsonDiff compareJson(CompareInput compare) throws IOException, ParseException;

}
