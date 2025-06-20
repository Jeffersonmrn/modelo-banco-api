package com.transferencia.conta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioDTO {

	@NotBlank
	public String nome;

	@Email
	@NotBlank
	public String email;

	@NotBlank
	public String senha;

	@NotBlank
	public String cpf;

	public ContaDTO conta;

	public UsuarioDTO() {
	}

	public UsuarioDTO(Long id, String nome, String email, String senha, String cpf, ContaDTO conta) {

		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.cpf = cpf;
		this.conta = conta;
	}
}
