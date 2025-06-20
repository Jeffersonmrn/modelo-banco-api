package com.transferencia.conta.dto;

public class ResponseDTO<T> {

	private String status;
	private String mensagem;
	private T dados;

	public ResponseDTO(String status, String mensagem, T dados) {

		this.status = status;
		this.mensagem = mensagem;
		this.dados = dados;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public T getDados() {
		return dados;
	}

	public void setDados(T dados) {
		this.dados = dados;
	}
}
