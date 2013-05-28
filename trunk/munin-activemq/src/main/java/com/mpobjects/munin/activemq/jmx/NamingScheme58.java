/*
 * Copyright 2013 MP Objects
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
package com.mpobjects.munin.activemq.jmx;

/**
 * JMX naming scheme used by ActiveMQ since 5.8
 */
public class NamingScheme58 extends NamingScheme {

	public static final NamingScheme INSTANCE = new NamingScheme58();

	public NamingScheme58() {
	}

	@Override
	public String brokerBean(String aBrokerName) {
		return String.format("org.apache.activemq:type=Broker,brokerName=%s", aBrokerName);
	}

	@Override
	public String destinationBean(String aBrokerName, String aType, String aName) {
		return String.format("org.apache.activemq:type=Broker,brokerName=%s,destinationType=%s,destinationName=%s", aBrokerName, aType, aName);
	}

	@Override
	public String destinationName() {
		return "destinationName";
	}

	@Override
	public String destinationType() {
		return "destinationType";
	}
}
