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

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.mpobjects.munin.activemq.Output;

/**
 * Base code to set up the JMX connection
 */
public class JmxConnection {
	protected String host = "localhost";
	protected int port = 1099;
	protected String user;
	protected String pass;

	protected Output output;
	protected MBeanServerConnection connection;

	public JmxConnection(Output aOutput) {
		super();
		if (aOutput == null) {
			throw new NullPointerException("Output cannot be null");
		}
		output = aOutput;
		init();
	}

	/**
	 * @param aHost
	 *            the host to set
	 */
	public void setHost(String aHost) {
		host = aHost;
	}

	/**
	 * @param aPort
	 *            the port to set
	 */
	public void setPort(int aPort) {
		port = aPort;
	}

	/**
	 * @param aUser
	 *            the user to set
	 */
	public void setUser(String aUser) {
		user = aUser;
	}

	/**
	 * @param aPass
	 *            the pass to set
	 */
	public void setPass(String aPass) {
		pass = aPass;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	protected void init() {
		Map<String, String> env = System.getenv();
		if (env.get("JMX_HOST") != null) {
			host = env.get("JMX_HOST");
		} else {
			host = System.getProperty("JMX_HOST", host);
		}
		if (env.get("JMX_PORT") != null) {
			port = Integer.parseInt(env.get("JMX_PORT"));
		} else {
			port = Integer.getInteger("JMX_PORT", port);
		}
		if (env.get("JMX_USER") != null) {
			user = env.get("JMX_USER");
		} else {
			user = System.getProperty("JMX_USER", user);
		}
		if (env.get("JMX_PASS") != null) {
			pass = env.get("JMX_PASS");
		} else {
			pass = System.getProperty("JMX_PASS", pass);
		}
	}

	/**
	 * Connect to the JMX server (if not already connected)
	 * 
	 * @return
	 */
	public boolean connect() {
		if (connection != null) {
			return true;
		}
		String url = getJmxUrl();
		boolean withAuth = false;
		try {
			JMXServiceURL jmxUrl = new JMXServiceURL(url);
			Map<String, Object> env = new HashMap<String, Object>();
			if (user != null) {
				env.put(JMXConnector.CREDENTIALS, new String[] { user, pass });
				env.put("username", user);
				env.put("password", pass);
				withAuth = true;
			}
			JMXConnector connector = JMXConnectorFactory.connect(jmxUrl, env);
			connection = connector.getMBeanServerConnection();
			return true;
		} catch (Exception e) {
			output.err.println("Unable to connect to (with authentication: " + withAuth + "): " + url);
			if (!withAuth) {
				output.err.println("Authentication might be required. Set JMX_USER and JMX_PASS.");
			}
			e.printStackTrace(output.err);
			return false;
		}
	}

	/**
	 * @return the JMX URI
	 */
	protected String getJmxUrl() {
		return String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port);
	}
}
