package com.comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Combination extends NodeInfo {
	private String[] spliter;

	public Combination(String parentNodeName, String nodeName, String path, String[] spliter) {
		super(parentNodeName, nodeName, path);
		this.spliter = spliter;
	}
}
