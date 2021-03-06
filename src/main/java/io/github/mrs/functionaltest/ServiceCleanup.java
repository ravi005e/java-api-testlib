package io.github.mrs.functionaltest;

import io.github.mrs.functionaltest.utils.HttpCaller;
import io.restassured.response.ValidatableResponse;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.internal.collections.Pair;

public abstract class ServiceCleanup {
	protected static final Logger LOGGER = LogManager.getLogger(ServiceCleanup.class);

	/**
	 * Every cleanup instance must implement this to actually remove its objects
	 *
	 * The objects it deletes will have been added via one of the <tt>forRemoval()</tt> methods. It's important
	 * that the objects are deleted in a deterministic order so foreign key or other constraints aren't violated.
	 *
	 * Additionally, this method should capture any exceptions thrown by the process and log them, but continue
	 * working through the rest of the objects it's tracked. We can't have a single failure prevent the deletion
	 * of a whole suite full of data.
	 *
	 * It's up to the implementation how these objects are collected.
	 * @throws Exception
	 */
	public abstract void run() throws Exception;

	/**
	 * Shortcut to run through a set of objects to remove and log an OK/FAIL for each.
	 *
	 * @param itemsToRemove list of items to remove
	 * @param service service that actually does the deletion
	 * @throws Exception
	 */
	protected <T extends Serializer> void removeAll(List<Pair<HttpCaller, T>> itemsToRemove, int expectedStatusCode, ServiceArea<T> service) throws Exception {
		for (Pair<HttpCaller, T> item : itemsToRemove) {
			ValidatableResponse response = service.remove(item);
			displayDeleteStatus(response, expectedStatusCode, item.getClass().getSimpleName(), item.second().getDeletionIdentifiers());
		}
	}

	/**
	 * Given a response, object type, and set of identifier values display the status of a deletion.
	 *
	 * @param response response generated by a remove
	 * @param expectedStatus expected HTTP status, operation will fail if status doesn't match, which
	 *   means we'll display a FAIL message but will continue on deleting other objects.
	 * @param objectType display name for type of object being deleted
	 * @param identifiers one or more identifiers to display
	 */
	protected void displayDeleteStatus(ValidatableResponse response, int expectedStatus, String objectType, String ... identifiers) {
		int statusCode = response.extract().statusCode();
		String identifierDisplay = String.join("/", identifiers);
		if ((statusCode == expectedStatus)){
			LOGGER.info("OK: Removed {} {}", objectType, identifierDisplay);
		} else {
			LOGGER.error("FAIL cleanup: {} {} - {}", objectType, identifierDisplay, response.extract().body().asString());
		}
	}
}
