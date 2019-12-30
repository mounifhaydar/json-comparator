package com.comparator.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Regexs extends ANodeInfo<Regex> {

	public Regexs(List<Regex> nodeInfos) {
		super(nodeInfos);
	}

	public Map<String, String> itemRegex(String path, boolean caseSensitive) {
		Regex r = getNodeInfo(path, caseSensitive);
		return r != null ? r.getRegex() : null;
	}
}
