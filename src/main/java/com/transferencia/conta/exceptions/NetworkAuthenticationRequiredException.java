package com.transferencia.conta.exceptions;

public class NetworkAuthenticationRequiredException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NetworkAuthenticationRequiredException(String message) {
		super(message);
	}
}
