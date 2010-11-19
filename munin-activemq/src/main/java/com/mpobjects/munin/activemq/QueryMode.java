/*
 * Copyright 2010, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.munin.activemq;

import java.util.EnumSet;

/**
 * Defines what results to return.
 */
public enum QueryMode {
	/**
	 * The current number of message on the queue or topic. Uses the QueueSize attribute.
	 */
	SIZE,
	/**
	 * Number of producers/consumers. Based on the ConsumerCount and ProducerCount attributes.
	 */
	SUBSCRIBERS,
	/**
	 * Number of messages that pass through the destination. This is based on the EnqueueCount and DequeueCount.
	 */
	TRAFFIC;

	/**
	 * Parse a string to a {@link QueryMode} value.
	 * 
	 * @param string
	 * @return
	 */
	public static final QueryMode parse(String string) {
		for (QueryMode mode : EnumSet.allOf(QueryMode.class)) {
			if (mode.toString().equalsIgnoreCase(string)) {
				return mode;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown query mode: %s", string));
	}
}
