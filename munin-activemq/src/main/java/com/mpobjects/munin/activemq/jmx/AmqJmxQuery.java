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
package com.mpobjects.munin.activemq.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.mpobjects.munin.activemq.Output;
import com.mpobjects.munin.activemq.QueryMode;

/**
 * ActiveMQ JMX queries
 */
public class AmqJmxQuery extends JmxConnection {
	/**
	 * The pattern for a destination: type:name. Broker defauls to localhost. Type defaults to Queue. Only name is
	 * required.
	 */
	protected static final Pattern DEST_PATTERN = Pattern.compile("((\\w+):)?(.*)");

	private static final String KEY_TYPE = "Type";
	private static final String KEY_DESTINATION = "Destination";

	protected QueryMode mode = null;

	public AmqJmxQuery(Output aOutput) {
		super(aOutput);
		if (aOutput == null) {
			throw new NullPointerException("Output cannot be null");
		}
	}

	/**
	 * @param aMode
	 */
	public AmqJmxQuery(Output aOutput, QueryMode aMode) {
		this(aOutput);
		mode = aMode;
	}

	/**
	 * Print the values for the given destinations
	 * 
	 * @param aDests
	 */
	public void printValues(List<String> aDests) {
		if (mode == null) {
			throw new IllegalStateException("No query mode was set");
		}
		if (!connect()) {
			output.setExitCode(1);
			return;
		}
		for (String destStr : aDests) {
			ObjectName dest;
			try {
				dest = getObjectName(destStr);
			} catch (IllegalArgumentException e) {
				output.err.println("Invalid destination: " + destStr);
				e.printStackTrace(output.err);
				continue;
			}
			printDestinationValue(dest);
		}
	}

	/**
	 * Print the values for a givend destination
	 * 
	 * @param aDest
	 */
	protected void printDestinationValue(ObjectName aDest) {
		String[] attributes = getAttributeNames();
		try {
			AttributeList values = connection.getAttributes(aDest, attributes);
			for (Attribute attr : values.asList()) {
				if (attr.getValue() instanceof Number) {
					printValue(aDest, attr.getName(), attr.getValue().toString());
				} else {
					output.err.println("Returned value is not a number: " + attr.toString());
					printValue(aDest, attr.getName(), "U");
				}
			}
		} catch (Exception e) {
			e.printStackTrace(output.err);
			for (String attr : attributes) {
				printValue(aDest, attr, "U");
			}
		}
	}

	/**
	 * Get the attribute names for a given mode
	 * 
	 * @param attributes
	 * @return
	 */
	protected String[] getAttributeNames() {
		switch (mode) {
			case SIZE:
				return new String[] { "QueueSize" };
			case SUBSCRIBERS:
				return new String[] { "ConsumerCount", "ProducerCount" };
			case TRAFFIC:
				return new String[] { "EnqueueCount", "DequeueCount" };
			default:
				throw new IllegalStateException("Unknown mode: " + mode);
		}
	}

	/**
	 * @param aDest
	 * @param aAttr
	 * @param aString
	 */
	protected void printValue(ObjectName aDest, String aAttr, String aValue) {
		output.out.print(formatGraphName(aDest, aAttr));
		output.out.print(".value ");
		output.out.println(aValue);
	}

	/**
	 * @param aDest
	 * @param aAttr
	 * @return
	 */
	protected String formatGraphName(ObjectName aDest, String aAttr) {
		return String.format("%s$%s$%s", aDest.getKeyProperty(KEY_TYPE), aDest.getKeyProperty(KEY_DESTINATION).replaceAll("[^a-zA-Z0-9]+", "_"), aAttr);
	}

	protected String formatDestination(ObjectName aDest) {
		return String.format("%s: %s", aDest.getKeyProperty(KEY_TYPE), aDest.getKeyProperty(KEY_DESTINATION));
	}

	/**
	 * Print the configuration for the given destinations
	 * 
	 * @param aDests
	 */
	public void printConfig(List<String> aDests) {
		if (mode == null) {
			throw new IllegalStateException("No query mode was set");
		}
		List<ObjectName> destinations = new ArrayList<ObjectName>();
		for (String destStr : aDests) {
			try {
				destinations.add(getObjectName(destStr));
			} catch (IllegalArgumentException e) {
				output.err.println("Invalid destination: " + destStr);
				e.printStackTrace(output.err);
				continue;
			}
		}
		switch (mode) {
			case SIZE:
				printConfigSize(destinations);
				break;
			case SUBSCRIBERS:
				printConfigSubscribers(destinations);
				break;
			case TRAFFIC:
				printConfigTraffic(destinations);
				break;
			default:
				throw new IllegalStateException("Unknown mode: " + mode);
		}
	}

	/**
	 * @param aDestinations
	 */
	protected void printConfigSize(List<ObjectName> aDestinations) {
		println("graph_title Queue Size");
		println("graph_category ActiveMQ");
		println("graph_info The number of messages currently waiting on the queue.");
		println("graph_vlabel Messages");

		String[] attrs = getAttributeNames();
		for (ObjectName dest : aDestinations) {
			for (String attr : attrs) {
				println("");
				String name = formatGraphName(dest, attr);
				println(name + ".label " + formatDestination(dest));
				println(name + ".type GAUGE");
				println(name + ".min 0");
				// 1 message on the queue could be a fluke, so from 2 it's a warning
				println(name + ".warning 2");
				println(name + ".critical 5");
			}
		}
	}

	/**
	 * @param aDestinations
	 */
	protected void printConfigSubscribers(List<ObjectName> aDestinations) {
		println("graph_title Subscribers");
		println("graph_category ActiveMQ");
		println("graph_info The number of producers and consumers on a destination.");
		println("graph_vlabel Clients");

		String[] attrs = getAttributeNames();
		for (ObjectName dest : aDestinations) {
			for (String attr : attrs) {
				println("");
				String name = formatGraphName(dest, attr);
				String attrLabel = attr.substring(0, attr.indexOf("Count"));
				println(name + ".label " + formatDestination(dest) + " " + attrLabel);
				println(name + ".type GAUGE");
				println(name + ".min 0");
			}
		}
	}

	/**
	 * @param aDestinations
	 */
	protected void printConfigTraffic(List<ObjectName> aDestinations) {
		println("graph_title Traffic");
		println("graph_category ActiveMQ");
		println("graph_info The number of messages that are written to and read from the destination.");
		println("graph_vlabel Messages");

		String[] attrs = getAttributeNames();
		for (ObjectName dest : aDestinations) {
			for (String attr : attrs) {
				println("");
				String name = formatGraphName(dest, attr);
				String attrLabel = attr.substring(0, attr.indexOf("Count"));
				println(name + ".label " + formatDestination(dest) + " " + attrLabel);
				println(name + ".type DERIVE");
				println(name + ".min 0");
			}
		}
	}

	/**
	 * Print the autoconf output.
	 * 
	 * @param aDests
	 * 
	 * @param listDests
	 */
	public void printAutoConf(List<String> aDests) {
		if (!connect()) {
			output.out.println("no (unable to connect)");
			return;
		}
		if (aDests.isEmpty()) {
			output.out.println("yes (no destinations checked)");
		} else {
			List<String> failed = new ArrayList<String>(aDests);
			for (String destStr : aDests) {
				ObjectName dest;
				try {
					dest = getObjectName(destStr);
				} catch (IllegalArgumentException e) {
					output.err.println("Invalid destination specification: " + destStr);
					continue;
				}
				try {
					if (!connection.isRegistered(dest)) {
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace(output.err);
					continue;
				}
				failed.remove(destStr);
			}
			if (failed.isEmpty()) {
				output.out.println("yes");
			} else {
				output.out.println("no (failed destinations:");
				for (String destStr : failed) {
					output.out.println(destStr);
				}
				output.out.println(")");
			}
		}
	}

	public void printDestinations() {
		if (!connect()) {
			return;
		}

		// no try to find all queue and topics
		try {
			AttributeList values = connection.getAttributes(ObjectName.getInstance("org.apache.activemq:BrokerName=localhost,Type=Broker"), new String[] { "Queues", "Topics" });
			for (Attribute attr : values.asList()) {
				// should return an array of object names
				if (attr.getValue() instanceof ObjectName[]) {
					for (ObjectName objName : (ObjectName[]) attr.getValue()) {
						String type = objName.getKeyProperty(KEY_TYPE);
						String destination = objName.getKeyProperty(KEY_DESTINATION);
						if ("queue".equalsIgnoreCase(type) || "topic".equalsIgnoreCase(type)) {
							output.out.print(type);
							output.out.print(":");
							output.out.println(destination);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(output.err);
		}
	}

	/**
	 * Get the object name for a given destination
	 * 
	 * @param destination
	 *            The destination.
	 * @return
	 */
	protected ObjectName getObjectName(String destination) {
		String broker = "localhost";
		String type = "Queue";
		String name = destination;

		Matcher matcher = DEST_PATTERN.matcher(destination);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(String.format("Invalid destination: %s", destination));
		}

		if (matcher.group(2) != null) {
			type = matcher.group(2);
		}
		name = matcher.group(3).trim();

		if ("queue".equalsIgnoreCase(type)) {
			type = "Queue";
		} else if ("topic".equalsIgnoreCase(type)) {
			type = "Topic";
		} else {
			throw new IllegalArgumentException(String.format("Invalid type '%s' in destination: %s", type, destination));
		}

		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException(String.format("No destination name given: %s", destination));
		}

		try {
			return ObjectName.getInstance(String.format("org.apache.activemq:BrokerName=%s,Type=%s,Destination=%s", broker, type, name));
		} catch (MalformedObjectNameException e) {
			throw new IllegalArgumentException("Unable to create object name for destination: " + destination, e);
		}
	}

	/**
	 * Just a shorthand
	 * 
	 * @param str
	 */
	protected final void println(String str) {
		output.out.println(str);
	}
}
