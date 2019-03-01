package com.comparator.model;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompareInput {
	private JsonNode		actual;
	private JsonNode		expected;
	private List<NodeInfo>	primaryNodes;
	private boolean			primaryIncluded;
	private List<Key>		keys;
	private int				precision;
	boolean					nodeSensitiveName;
	boolean					caseSensitiveValue;
}
