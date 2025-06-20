package com.transferencia.conta.exceptions;

public class MethodNotAllowedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException(String message) {
		super(message);
	}
}
