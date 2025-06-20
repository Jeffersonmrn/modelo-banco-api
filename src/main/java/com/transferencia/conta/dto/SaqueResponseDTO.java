package com.transferencia.conta.dto;

import java.math.BigDecimal;

public class SaqueResponseDTO {

	private BigDecimal valorSacado;
	private BigDecimal saldoRestante;

	public SaqueResponseDTO(BigDecimal valorSacado, BigDecimal saldoRestante) {
		this.valorSacado = valorSacado;
		this.saldoRestante = saldoRestante;
	}

	public BigDecimal getValorSacado() {
		return valorSacado;
	}

	public void setValorSacado(BigDecimal valorSacado) {
		this.valorSacado = valorSacado;
	}

	public BigDecimal getSaldoRestante() {
		return saldoRestante;
	}

	public void setSaldoRestante(BigDecimal saldoRestante) {
		this.saldoRestante = saldoRestante;
	}
}
