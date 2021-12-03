package com.dove.mwd;

@SuppressWarnings("serial")
public class NotDefinedException extends RuntimeException {
	NotDefinedException() {
		super();
	}
	
	NotDefinedException(String msg) {
		super(msg);
	}
}
