package com.transferencia.conta.enums;

public enum Role {

	ROLE_ADMIN("HIGH"), ROLE_USER("LOW");

	private final String priority;

	Role(String priority) {
		this.priority = priority;
	}

	public String getPriority() {
		return priority;
	}
	
	public boolean isAdmin() {
        return this == ROLE_ADMIN;
    }
}
