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

	public Key findPKList(String parentName, String listName, String path, boolean caseSensitive) {
		if (keys == null) {
			return null;
		}
		for (Key key : keys) {
			if (caseSensitive) {
				if (path.equals(key.getPath())) {
					return key;
				}
			} else {
				if (path.equalsIgnoreCase(key.getPath())) {
					return key;
				}
			}
		}
		return null;
	}

	public boolean isDenyDuplication(String parentName, String listName, String path, boolean caseSensitive) {
		Key r = findPKList(parentName, listName, path, caseSensitive);
		return r != null && r.isUnique();
	}

	public boolean isHasKey(String parentName, String listName, String path, boolean caseSensitive) {
		Key r = findPKList(parentName, listName, path, caseSensitive);
		return r != null;
	}
}
