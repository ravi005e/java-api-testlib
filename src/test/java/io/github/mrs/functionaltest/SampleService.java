package io.github.mrs.functionaltest;

import io.restassured.response.ValidatableResponse;

import org.testng.internal.collections.Pair;

import io.github.mrs.functionaltest.utils.HttpCaller;

public class SampleService extends ServiceArea<SampleSerializer> {

	final String CONTEXT_PATH = "/api/users";

	public Pair<SampleSerializer, ValidatableResponse> fetchUsers() throws Exception {
		HttpCaller http = new HttpCaller();
		ValidatableResponse response = http.doGet(CONTEXT_PATH + "?page=2", 200);
		SampleSerializer ss = fromJson(response, SampleSerializer.class);
		return Pair.of(ss, response);
	}

	@Override
	public ValidatableResponse remove(Pair<HttpCaller, SampleSerializer> item) throws Exception {
		throw new UnsupportedOperationException("Deletion not supported: "+ this.getClass().getName());
	}

}
