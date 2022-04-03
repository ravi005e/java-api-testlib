package io.github.mrs.functionaltest.utils;

import static io.restassured.RestAssured.given;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.core.AnyOf;

@NoArgsConstructor
public class HttpCaller {

	private static final Logger LOGGER = LogManager.getLogger(HttpCaller.class);

	// define a default no-op spec
	private static RequestSpecification REQUEST_SPEC = new RequestSpecBuilder().build();
	private static ResponseSpecification RESPONSE_SPEC = new ResponseSpecBuilder().build();

	// TODO: use Map implementation that allows for case-insenitive keys on get operations
	private Map<String, String> headers = new HashMap<>();

	public HttpCaller(Map<String, String> additionalHeaders) {
	    this.headers.putAll(additionalHeaders);
	}

	/**
	 * Create a new HttpCaller object with its original headers augmented by those given.
	 *
	 * @param moreHeaders headers to use in addition to the ones already in the current HttpCaller object;
	 *   headers given will override existing
	 * @return new HttpCaller object
	 */
	public HttpCaller withHeaders(Map<String, String> moreHeaders) {
		Map<String, String> newHeaders = new HashMap<>(this.headers);
		newHeaders.putAll(moreHeaders);
		return new HttpCaller(newHeaders);
	}

	/**
	 * Get the value of an individual header this caller object was seeded with.
	 *
	 * @param key case-sensitive key for the header
	 * @return value associated with the header, will return empty string instead
	 * of null if header does not exist
	 */
	public String getHeader(String key) {
		String val = this.headers.get(key);
		return null == val ? "" : val;
	}

	private Map<String, String> getHeaders() {
		this.headers.put("Accept", "application/json");
		return this.headers;
	}

	private Map<String,String> postHeaders() {
		Map<String, String> headers = getHeaders();
		headers.put("Content-Type", "application/json");
		return headers;
	}

	public String resolveUrl(String path) {
		return Config.url(path);
	}

	public static void setup(int port) {
		if (Config.DEBUG) {
			try {
				PrintStream trafficStream = new PrintStream(new File("test-output/traffic.log"));
				RestAssured.config = RestAssured
						.config()
						.logConfig(new LogConfig().defaultStream(trafficStream));
				REQUEST_SPEC = new RequestSpecBuilder().setPort(port).log(LogDetail.ALL).build();
				RESPONSE_SPEC = new ResponseSpecBuilder().log(LogDetail.ALL).build();
			} catch (IOException e) {
				LOGGER.warn("Failed to open output file for request/response logging: " + e.getMessage());
			}
		}
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
	/**
	 * Run a remove without status code verification.
	 *
	 * @param path path within area
	 * @return ValidatableResponse
	 */
	public ValidatableResponse doDelete(String path, String payload, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
				.body(payload)
			.when()
				.delete(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus);
	}

	public ValidatableResponse doDelete(String path, String payload, AnyOf<Integer> anyOf) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
				.body(payload)
			.when()
				.delete(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(anyOf);
	}

	/**
	 * Run a remove without status code verification.
	 *
	 * @param path path within area
	 * @return ValidatableResponse
	 */
	public ValidatableResponse doDelete(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
			.when()
				.delete(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus);
	}

	public ValidatableResponse doDelete(String path, AnyOf<Integer> anyOf) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
			.when()
				.delete(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(anyOf);
	}

	public ValidatableResponse fetch(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(this.headers)
			.when()
				.get(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus);
	}

	public ValidatableResponse doGet(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
			.when()
				.get(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	@Deprecated
	public ValidatableResponse doGet(String path, String payload, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
				.body(payload)
			.when()
				.get(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPost(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(this.headers)
			.when()
				.post(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPost(String path, String payload, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(postHeaders())
				.body(payload)
			.when()
				.post(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPut(String path, String payload, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(postHeaders())
				.body(payload)
			.when()
				.put(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPut(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(postHeaders())
			.when()
				.put(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPatch(String path, String payload, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(postHeaders())
				.body(payload)
			.when()
				.patch(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public ValidatableResponse doPatch(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(postHeaders())
			.when()
				.patch(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus)
				.contentType(ContentType.JSON);
	}

	public JsonObject toJson(ValidatableResponse response) {
		return JsonParser.parseString(response.extract().body().asString()).getAsJsonObject();
	}


	public ValidatableResponse fetchHead(String path, int expectedStatus) throws Exception {
		return given().spec(REQUEST_SPEC)
				.headers(getHeaders())
			.when()
				.head(resolveUrl(path))
			.then().spec(RESPONSE_SPEC)
				.statusCode(expectedStatus);
	}

}
