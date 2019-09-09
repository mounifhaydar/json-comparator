package com.comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Precision extends NodeInfo {
	/**
	 * defaultPrecision = 0 : should be equal </br>
	 * defaultPrecision = 1 : 1.01 and 1.09 are consumed as equal </br> 
	 * defaultPrecision = 2 : 1.01 and 1.09 are consumed as not equal </br>
	 */
	private int defaultPrecision;

	public Precision(String parentNodeName, String nodeName, String path, int defaultPrecision) {
		super(parentNodeName, nodeName, path);
		this.defaultPrecision = defaultPrecision;
	}
}
