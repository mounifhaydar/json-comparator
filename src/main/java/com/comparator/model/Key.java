package com.comparator.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Key extends NodeInfo {
	private boolean			unique;
	private List<String>	keySet;

	public Key(String parentNodeName, String nodeName, String path, boolean unique, List<String> keySet) {
		super(parentNodeName, nodeName, path);
		this.unique = unique;
		this.keySet = keySet;
	}

}
