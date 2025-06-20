package com.transferencia.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.transferencia.conta.enums.TipoTransferencia;
import com.transferencia.conta.model.Transferencia;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferenciaDTO {

	@JsonProperty("transferenciaId")
	public Long id;

	public Long origemId;
	public String origemNome;
	public Long destinoId;
	public String destinoNome;
	public String chavePix;
	public BigDecimal valor;
	public String dataHora;
	public TipoTransferencia tipo;

	public TransferenciaDTO() {
	}

	public TransferenciaDTO(Transferencia t) {
		this.id = t.getId();

		if (t.getOrigem() != null) {
			this.origemId = t.getOrigem().getId();
			this.origemNome = t.getOrigem().getUsuario() != null ? t.getOrigem().getUsuario().getNome() : null;
		} else {
			this.origemId = null;
			this.origemNome = null;
		}

		if (t.getDestino() != null) {
			this.destinoId = t.getDestino().getId();
			this.destinoNome = t.getDestino().getUsuario() != null ? t.getDestino().getUsuario().getNome() : null;
		} else {
			this.destinoId = null;
			this.destinoNome = null;
		}

		this.valor = t.getValor();

		LocalDateTime utcTime = t.getDataHora().atOffset(ZoneOffset.UTC).toLocalDateTime();
		this.dataHora = utcTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		this.tipo = t.getTipo();

		this.chavePix = t.getChavePix();
	}
}
