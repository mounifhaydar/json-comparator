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
public class Combinations extends ANodeInfo<Combination> {

	private boolean		combinational;
	private String[]	defaultSpliter;

	public Combinations(List<Combination> nodeInfos, boolean combinational, String[] defaultSpliter) {
		super(nodeInfos);
		this.combinational = combinational;
		this.defaultSpliter = defaultSpliter;
	}

	public String[] itemCombination(String path, boolean caseSensitive) {
		Combination r = getNodeInfo(path, caseSensitive);
		return r != null ? r.getSpliter() : defaultSpliter;
	}

	public boolean isCombinatorics(String path, boolean caseSensitive) {
		boolean nodeFound = isContainsNodeInfo(path, caseSensitive);
		return combinational || nodeFound;
	}
}
