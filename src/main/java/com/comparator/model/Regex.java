package com.comparator.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Regex extends NodeInfo{
	private Map<String, String> regex;

	public Regex(String parentNodeName, String nodeName, String path, Map<String, String> regex) {
		super(parentNodeName, nodeName, path);
		this.regex = regex;
	}
}
