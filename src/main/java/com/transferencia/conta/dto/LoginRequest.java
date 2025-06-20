package com.transferencia.conta.dto;

public record LoginRequest(String email, String agencia, String numeroConta, String senha) {

}
