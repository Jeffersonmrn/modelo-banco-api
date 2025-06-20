package com.transferencia.conta.pixconfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChavePixValidator.class)
public @interface ValidChavePix {
	String message() default "Chave Pix inválida. Use um CPF, e-mail ou telefone.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
