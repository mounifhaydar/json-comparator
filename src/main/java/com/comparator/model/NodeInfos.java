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
public class NodeInfos {
	private boolean			include;
	private List<NodeInfo>	nodeInfos;

	public boolean isContainsNodeInfo(NodeInfo nodeInfo, boolean caseSensitive) {
		if (nodeInfos == null)
			return false;
		for (NodeInfo a : nodeInfos) {
			if (caseSensitive) {
				if (a.getPath().equals(nodeInfo.getPath())) {
					return true;
				}
			} else {
				if (a.getPath().equalsIgnoreCase(nodeInfo.getPath())) {
					return true;
				}
			}

		}
		return false;
	}

	public boolean isSkip(NodeInfo nodeInfo, boolean caseSensitive) {
		boolean nodeFound = this.isContainsNodeInfo(nodeInfo, caseSensitive);
		boolean skipNode = nodeFound && !include || include && !nodeFound;
		return skipNode;
	}
}
