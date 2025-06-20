package com.transferencia.conta.utils;

public class CpfUtils {

	public static boolean isCpfValido(String cpf) {
		if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
			return false;
		}
		try {
			int soma1 = 0, soma2 = 0;
			for (int i = 0; i < 9; i++) {
				int digito = Character.getNumericValue(cpf.charAt(i));
				soma1 += digito * (10 - i);
				soma2 += digito * (11 - i);
			}
			int digito1 = (soma1 * 10) % 11;
			digito1 = (digito1 == 10) ? 0 : digito1;

			soma2 += digito1 * 2;
			int digito2 = (soma2 * 10) % 11;
			digito2 = (digito2 == 10) ? 0 : digito2;

			return digito1 == Character.getNumericValue(cpf.charAt(9))
					&& digito2 == Character.getNumericValue(cpf.charAt(10));
		} catch (Exception e) {
			return false;
		}
	}
}
