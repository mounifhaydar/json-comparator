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
public class DirtyCleans extends ANodeInfo<DirtyClean> {
	private String[] dirtyClean;

	public DirtyCleans(List<DirtyClean> nodeInfos, String[] dirtyClean) {
		super(nodeInfos);
		this.dirtyClean = dirtyClean;
	}

	public String[] itemCleaner(String path, boolean caseSensitive) {
		DirtyClean r = getNodeInfo(path, caseSensitive);
		return r != null ? r.getDirtyClean() : dirtyClean;
	}
}
