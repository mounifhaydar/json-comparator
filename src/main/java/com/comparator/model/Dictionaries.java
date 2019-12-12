package com.comparator.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Dictionaries extends ANodeInfo<Dictionary> {

	public Dictionaries(List<Dictionary> nodeInfos) {
		super(nodeInfos);
	}
	
	public String[] getDictionary(String path, boolean caseSensitive) {
		Dictionary r = getNodeInfo(path, caseSensitive);
		return r != null ? r.getValues() : null;
	}
}
