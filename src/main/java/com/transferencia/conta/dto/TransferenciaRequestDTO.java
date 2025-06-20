package com.transferencia.conta.dto;

import java.math.BigDecimal;

import com.transferencia.conta.enums.TipoTransferencia;
import com.transferencia.conta.pixconfig.ValidChavePix;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@ValidChavePix
public class TransferenciaRequestDTO {

	@NotNull(message = "origemId é obrigatório")
	public Long origemId;

	@NotNull(message = "destinoId é obrigatório")
	public Long destinoId;

	@NotNull(message = "valor é obrigatório")
	@Positive
	public BigDecimal valor;

	@NotNull(message = "tipo é obrigatório")
	public TipoTransferencia tipo;

	public String chavePix;

	@AssertTrue(message = "Chave Pix inválida ou desnecessária para esse tipo de transferência")
	public boolean isChavePixValidaOuDesnecessaria() {
		if (tipo == null)
			return true;

		if (tipo == TipoTransferencia.PIX) {
			return chavePix != null && !chavePix.isBlank();
		}

		return chavePix == null || chavePix.isBlank();
	}

	public TransferenciaRequestDTO() {
	}
}
