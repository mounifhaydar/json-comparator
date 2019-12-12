package com.comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dictionary extends NodeInfo{

	private String[] values;
	
	public Dictionary(String parentNodeName, String nodeName, String path, String[] values) {
		super(parentNodeName, nodeName, path);
		this.values = values;
	}
}
