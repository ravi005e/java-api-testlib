package com.github.mrs.functionaltest;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cleanup {
	protected static final Logger LOGGER = LogManager.getLogger(Cleanup.class);

	private static List<ServiceCleanup> cleanupImplementations = new ArrayList<>();

	/**
	 * Register a service-specific cleanup instance to invoke once we're all done
	 *
	 * @param serviceCleanup cleanup instance specific to a redwood service; it's responsible
	 *     for removing all the objects under its management
	 */
	public static void register(ServiceCleanup serviceCleanup) {
		cleanupImplementations.add(serviceCleanup);
	}

	/**
	 * Run through all cleanup activities, one per Redwood service.
	 */
	public static void run() {
		// Order here shouldn't matter since every service manages its own tables and FKs, so it winds up being FIFO
		for (ServiceCleanup sc : cleanupImplementations) {
			try {
				sc.run();
			} catch (Exception e) {
				LOGGER.error("Failed to complete cleanup activities for class: " + sc.getClass().getSimpleName(), e);
			}
		}
	}
}
