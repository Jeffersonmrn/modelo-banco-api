package com.transferencia.conta.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UsuarioUpdateDTO {

	@NotBlank(message = "Email não pode estar em branco")
	@Email(message = "Email inválido")
	public String email;

	@NotBlank(message = "Senha não pode estar em branco")
	public String senha;

	public String nome;
	public String cpf;

	public UsuarioUpdateDTO() {
	}

	public UsuarioUpdateDTO(String email, String senha) {
		this.email = email;
		this.senha = senha;
	}
}
