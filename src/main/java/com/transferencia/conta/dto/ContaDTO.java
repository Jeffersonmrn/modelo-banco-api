package com.transferencia.conta.dto;

import java.math.BigDecimal;

import com.transferencia.conta.model.Conta;

public class ContaDTO {

	public Long contaId;
	public String agencia;
	public String numeroConta;
	public BigDecimal saldo;
	public Long usuarioId;

	public ContaDTO() {
	}

	public ContaDTO(Long contaId, String agencia, String numeroConta, BigDecimal saldo, Long usuarioId) {

		this.contaId = contaId;
		this.agencia = agencia;
		this.numeroConta = numeroConta;
		this.saldo = saldo;
		this.usuarioId = usuarioId;
	}

	public ContaDTO(Conta conta) {
		
		this.contaId = conta.getId();
		this.agencia = conta.getAgencia();
		this.numeroConta = conta.getNumeroConta();
		this.saldo = conta.getSaldo();
		this.usuarioId = conta.getUsuario() != null ? conta.getUsuario().getId() : null;
	}

	public Long getContaId() {
		return contaId;
	}

	public void setContaId(Long contaId) {
		this.contaId = contaId;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getNumeroConta() {
		return numeroConta;
	}

	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}
}
