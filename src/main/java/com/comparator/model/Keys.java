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
public class Keys {
	private List<Key> keys;

	public Key findPKList(String parentName, String listName, boolean caseSensitive) {
		if (keys == null) {
			return null;
		}
		for (Key key : keys) {
			if (caseSensitive) {
				if (parentName.equals(key.getParentNodeName()) && listName.equals(key.getNodeName())) {
					return key;
				}
			} else {
				if (parentName.equalsIgnoreCase(key.getParentNodeName()) && listName.equalsIgnoreCase(key.getNodeName())) {
					return key;
				}
			}

		}
		return null;
	}

	public boolean isDenyDuplication(String parentName, String listName, boolean caseSensitive) {
		Key r = findPKList(parentName, listName, caseSensitive);
		return r != null && r.isUnique();
	}

	public boolean isKeyFound(String parentName, String listName, boolean caseSensitive) {
		Key r = findPKList(parentName, listName, caseSensitive);
		return r != null;
	}
}
