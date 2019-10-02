package com.comparator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DirtyClean extends NodeInfo {

	private String[]		dirtyClean;

	public DirtyClean(String parentNodeName, String nodeName, String path, String[] dirtyClean) {
		super(parentNodeName, nodeName, path);
		this.dirtyClean = dirtyClean;
	}
}
