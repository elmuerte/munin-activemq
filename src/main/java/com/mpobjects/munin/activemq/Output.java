/*
 * Copyright 2010, MP Objects, http://www.mp-objects.com
 */
package com.mpobjects.munin.activemq;

import java.io.PrintStream;

/**
 * A helper class for the output of this utility. The only reason that this exists is because we don't want to hard link
 * to System.out and System.err. In the future we might want to use different output mechanisms.
 */
public class Output {
	public PrintStream out;
	public PrintStream err;

	/**
	 * Used to set the exit code which will be used at the end of the program.
	 */
	protected int exitCode;

	/**
	 * Standard output using the system out and err.
	 */
	public Output() {
		this(System.out, System.err);
	}

	/**
	 * @param aOut
	 * @param aErr
	 */
	public Output(PrintStream aOut, PrintStream aErr) {
		if (aOut == null) {
			throw new NullPointerException("Out cannot be null");
		}
		if (aErr == null) {
			throw new NullPointerException("Err cannot be null");
		}
		out = aOut;
		err = aErr;
	}

	/**
	 * @param aExitCode
	 *            the exitCode to set
	 */
	public void setExitCode(int aExitCode) {
		exitCode = aExitCode;
	}

	/**
	 * @return the exitCode
	 */
	public int getExitCode() {
		return exitCode;
	}
}
