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
public class SelectedNodes extends ANodeInfo<NodeInfo> {
	private boolean include;

	public SelectedNodes(List<NodeInfo> nodeInfos, boolean include) {
		super(nodeInfos);
		this.include = include;
	}

	public boolean isSkip(String path, boolean caseSensitive) {
		boolean nodeFound = isContainsNodeInfo(path, caseSensitive);
		boolean skipNode = nodeFound && !include || include && !nodeFound;
		return skipNode;
	}
}
