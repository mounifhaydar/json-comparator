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
public class Precisions extends ANodeInfo<Precision> {
	/**
	 * defaultPrecision = 0 : should be equal </br>
	 * defaultPrecision = 1 : 1.01 and 1.09 are consumed as equal </br> 
	 * defaultPrecision = 2 : 1.01 and 1.09 are consumed as not equal </br>
	 */
	private int defaultPrecision;

	public Precisions(List<Precision> nodeInfos, int defaultPrecision) {
		super(nodeInfos);
		this.defaultPrecision = defaultPrecision;
	}

	public int allowedDiff(String path, boolean caseSensitive) {
		Precision r = getNodeInfo(path, caseSensitive);//findPKList(parentName, listName, path, caseSensitive);
		return r != null ? r.getDefaultPrecision() : defaultPrecision;
	}
}
