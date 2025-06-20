package com.transferencia.conta.dto;

import java.math.BigDecimal;

public class PixDTO {

	private String chave;
	private BigDecimal valor;

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
}
