/*
 * Copyright 2010, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.munin.activemq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.mpobjects.munin.activemq.jmx.AmqJmxQuery;

/**
 * Entry point for the activemq plugin
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Output output = new Output();
		QueryAction action = null;
		try {
			if (args.length < 1) {
				printUsage(output, args);
				return;
			}
			action = QueryAction.parse(args[0]);

			List<String> dests = new ArrayList<String>();
			for (int i = 2; i < args.length; ++i) {
				dests.add(args[i]);
			}

			if (action == QueryAction.SUGGEST) {
				for (QueryMode aMode : EnumSet.allOf(QueryMode.class)) {
					output.out.println(aMode.toString().toLowerCase());
				}
				return;
			} else if (action == QueryAction.AUTOCONF) {
				AmqJmxQuery query = new AmqJmxQuery(output);
				query.printAutoConf(dests);
			} else if (action == QueryAction.LIST) {
				AmqJmxQuery query = new AmqJmxQuery(output);
				query.printDestinations();
			} else {
				if (args.length <= 2) {
					printUsage(output, args);
					return;
				}
				QueryMode mode = QueryMode.parse(args[1]);

				AmqJmxQuery query = new AmqJmxQuery(output, mode);
				switch (action) {
					case FETCH:
						query.printValues(dests);
						break;
					case CONFIG:
						query.printConfig(dests);
						break;
				}
			}

		} catch (Exception e) {
			if (action == QueryAction.AUTOCONF) {
				output.out.println(String.format("no (Exception raised: %s)", e.getMessage()));
			}
			e.printStackTrace(output.err);
			output.setExitCode(1);
		} finally {
			if (action == QueryAction.AUTOCONF) {
				// always exit 0 with autoconf
			} else {
				System.exit(output.getExitCode());
			}
		}
	}

	/**
	 * @param output
	 */
	protected static void printUsage(Output output, String[] args) {
		output.err.println("Usage: java -jar munin-activemq.jar <values|config|autoconf|suggest|list> [<size|subscribers|traffic>] [destination ...]");
		output.err.println("Received arguments: " + Arrays.toString(args));
		output.setExitCode(1);
	}
}
