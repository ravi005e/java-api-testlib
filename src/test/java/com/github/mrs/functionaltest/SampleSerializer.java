package com.github.mrs.functionaltest;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class SampleSerializer extends Serializer {
	private int page;
	private int per_page;
	private int total;
	private int total_pages;

	private List<Data> data;
	private Map<String, String> support;

	class Data {
		private int id;
		private String email;
		private String first_name;
		private String last_name;
		private String avatar;
	}

	@Override
	public String[] getDeletionIdentifiers() {
		throw new UnsupportedOperationException("Deletion not supported: "+ this.getClass().getName());
	}
}
