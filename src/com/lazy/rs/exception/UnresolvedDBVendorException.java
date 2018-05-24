package com.lazy.rs.exception;

public class UnresolvedDBVendorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 659415276546730748L;

	public UnresolvedDBVendorException() {
		super("Unable to resolve database vendor from the connection.");
	}
	
}
