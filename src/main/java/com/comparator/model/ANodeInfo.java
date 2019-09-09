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
public abstract class ANodeInfo<T extends NodeInfo> {
	private List<T> nodeInfos;

	public T getNodeInfo(String path, boolean caseSensitive) {
		if (nodeInfos == null)
			return null;
		for (T a : nodeInfos) {
			if (caseSensitive) {
				if (a.getPath().equals(path)) {
					return a;
				}
			} else {
				if (a.getPath().equalsIgnoreCase(path)) {
					return a;
				}
			}

		}
		return null;
	}

	public boolean isContainsNodeInfo(String path, boolean caseSensitive) {
		/*
		 * if (nodeInfos == null) return false; for (NodeInfo a : nodeInfos) {
		 * if (caseSensitive) { if (a.getPath().equals(nodeInfo.getPath())) {
		 * return true; } } else { if
		 * (a.getPath().equalsIgnoreCase(nodeInfo.getPath())) { return true; } }
		 * 
		 * } return false;
		 */
		return getNodeInfo(path, caseSensitive) != null;
	}

}
