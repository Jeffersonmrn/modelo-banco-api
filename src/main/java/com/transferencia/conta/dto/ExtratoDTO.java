package com.transferencia.conta.dto;

import java.math.BigDecimal;
import java.util.List;

public class ExtratoDTO {

	public Long id;
	public String nome;
	public BigDecimal saldo;
	public List<TransferenciaDTO> transferencias;

	public ExtratoDTO() {
	}

	public ExtratoDTO(Long id, String nome, BigDecimal saldo, List<TransferenciaDTO> transferencias) {
		this.id = id;
		this.nome = nome;
		this.saldo = saldo;
		this.transferencias = transferencias;
	}
}
