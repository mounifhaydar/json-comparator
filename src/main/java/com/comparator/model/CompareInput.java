package com.comparator.model;

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
	private SelectedNodes	selectedNodes = new SelectedNodes();
	private Keys			keys = new Keys();
	boolean					nodeSensitiveName;
	boolean					caseSensitiveValue;
	private Precisions		precisions = new Precisions();
	private DirtyCleans		dirtyCleans = new DirtyCleans();

}
