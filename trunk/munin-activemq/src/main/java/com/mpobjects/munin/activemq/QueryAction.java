/*
 * Copyright 2010, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.munin.activemq;

import java.util.EnumSet;

/**
 * Main actions for this tool.
 */
public enum QueryAction {
	/**
	 * Return the values for the given query mode.
	 */
	FETCH,
	/**
	 * Return the munin configuration for the query mode.
	 */
	CONFIG,
	/**
	 * Perform the autconf action. This is not dependent on the query mode. It also returns all known queues and topic.
	 */
	AUTOCONF,
	/**
	 * Returns all available query modes.
	 */
	SUGGEST,
	/**
	 * List all known destinations
	 */
	LIST;

	public static final QueryAction parse(String string) {
		for (QueryAction action : EnumSet.allOf(QueryAction.class)) {
			if (action.toString().equalsIgnoreCase(string)) {
				return action;
			}
		}
		throw new IllegalArgumentException(String.format("Unknown query action: %s", string));
	}
}
