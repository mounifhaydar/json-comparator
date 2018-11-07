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
			if(caseSensitive) {
				if (a.getNodeName().equals(nodeInfo.getNodeName()) && a.getParentNodeName().equals(nodeInfo.getParentNodeName())) {
					return true;
				}
			}else {
				if (a.getNodeName().equalsIgnoreCase(nodeInfo.getNodeName()) && a.getParentNodeName().equalsIgnoreCase(nodeInfo.getParentNodeName())) {
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
