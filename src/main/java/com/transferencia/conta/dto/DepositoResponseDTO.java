package com.transferencia.conta.dto;

import java.math.BigDecimal;

public class DepositoResponseDTO {

	public BigDecimal valorDepositado;
	public BigDecimal saldoAtual;

	public DepositoResponseDTO(BigDecimal valorDepositado, BigDecimal saldoAtual) {
		this.valorDepositado = valorDepositado;
		this.saldoAtual = saldoAtual;
	}
}
