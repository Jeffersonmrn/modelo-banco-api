package com.transferencia.conta.dto;

public class UsuarioResponseDTO {

	private String mensagem;
	private UsuarioDTO usuario;

	public UsuarioResponseDTO(String mensagem, UsuarioDTO usuario) {
		this.mensagem = mensagem;
		this.usuario = usuario;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public UsuarioDTO getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioDTO usuario) {
		this.usuario = usuario;
	}
}
