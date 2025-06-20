package com.transferencia.conta.pixconfig;

import com.transferencia.conta.dto.TransferenciaRequestDTO;
import com.transferencia.conta.enums.TipoTransferencia;
import com.transferencia.conta.utils.CpfUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ChavePixValidator implements ConstraintValidator<ValidChavePix, TransferenciaRequestDTO> {

	@Override
	public boolean isValid(TransferenciaRequestDTO dto, ConstraintValidatorContext context) {
		if (dto == null)
			return true;

		if (dto.tipo != TipoTransferencia.PIX) {
			return true;
		}

		String chave = dto.chavePix;
		if (chave == null || chave.isBlank())
			return false;

		return chave.matches("^\\d{11}$") || chave.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
				|| CpfUtils.isCpfValido(chave);
	}
}
