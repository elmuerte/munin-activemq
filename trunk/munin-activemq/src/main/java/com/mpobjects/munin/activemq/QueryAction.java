/*
 * Copyright 2010 MP Objects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
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
