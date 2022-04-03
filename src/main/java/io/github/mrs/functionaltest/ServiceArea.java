package io.github.mrs.functionaltest;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.Collection;
import java.util.stream.Collectors;

import io.github.mrs.functionaltest.utils.HttpCaller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.restassured.response.ValidatableResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.internal.collections.Pair;

public abstract class ServiceArea<T extends Serializer> {
	protected static final Logger LOGGER = LogManager.getLogger(ServiceArea.class);

	/**
	 * Subclass must implement to cleanup data without an exception.
	 *
	 * @param item item to remove
	 * @throws Exception
	 */
	public abstract ValidatableResponse remove(Pair<HttpCaller,T> item) throws Exception;

	/**
	 * @param response response with JSON body
	 * @param clazz class to turn the JSON into
	 * @return instance
	 */
	protected <U> U fromJson(ValidatableResponse response, Class<U> clazz) {
		return new Gson().fromJson(response.extract().body().asString(), clazz);
	}

	/**
	 * @param buildObject generic object
	 * @param clazz class to turn the JSON into
	 * @return instance
	 */
	public <U> String toJson(Object buildObject, Class<U> clazz) {
		return new Gson().toJson(buildObject, clazz);
	}

	public <U> String toJsonWithNulls(Object buildObject, Class<U> clazz) {
		Gson serializeNulls = new GsonBuilder().serializeNulls()
            .create();
		return serializeNulls.toJson(buildObject, clazz);
	}

	/**
	 * @param jsonString JSON string
	 * @return JsonObject after parsing jsonString
	 */
	public JsonObject toJson(String jsonString) {
		return JsonParser.parseString(jsonString).getAsJsonObject();
	}

	/**
	 * @param collection of objects
	 * @param delimiter to join
	 * @return string
	 */
	public String join(Collection<?> collection, String delimiter) {
		return collection.stream()
				.map( Object::toString )
				.collect(Collectors.joining(delimiter));
	}

	private boolean hasResource(String resource) {
		try {
			getClass().getClassLoader().getResource(resource);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	protected void validateResponseSchema(ValidatableResponse response, String file) {
		if(null == file) {
			LOGGER.info("skipping schema validation");
		}
		else if((null != file) && hasResource(file)) {
			response.body(matchesJsonSchemaInClasspath(file));
		} else {
			throw new RuntimeException("schema file [" +file+ "] not found under resources");
		}
	}

}
