package com.comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Regex extends NodeInfo{
	private String regex ;
	
	public Regex(String parentNodeName, String nodeName, String path, String regex) {
		super(parentNodeName, nodeName, path);
		this.regex = regex;
	}
}
