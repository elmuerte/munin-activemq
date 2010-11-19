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
