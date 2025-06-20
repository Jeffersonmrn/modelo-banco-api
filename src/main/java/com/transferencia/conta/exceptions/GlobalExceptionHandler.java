package com.transferencia.conta.exceptions;

import javax.naming.ServiceUnavailableException;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handle400(BadRequestException ex) {
		return ResponseEntity.status(400).body(new ErrorResponse(400, ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handle401(UnauthorizedException ex) {
		return ResponseEntity.status(401).body(new ErrorResponse(401, ex.getMessage()));
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ErrorResponse> handle403(ForbiddenException ex) {
		return ResponseEntity.status(403).body(new ErrorResponse(403, ex.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handle404(NotFoundException ex) {
		return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
	}

	@ExceptionHandler(MethodNotAllowedException.class)
	public ResponseEntity<ErrorResponse> handle405(MethodNotAllowedException ex) {
		return ResponseEntity.status(405).body(new ErrorResponse(405, ex.getMessage()));
	}

	@ExceptionHandler(InternalServerErrorException.class)
	public ResponseEntity<ErrorResponse> handle500(InternalServerErrorException ex) {
		return ResponseEntity.status(500).body(new ErrorResponse(500, ex.getMessage()));
	}

	@ExceptionHandler(ServiceUnavailableException.class)
	public ResponseEntity<ErrorResponse> handle503(ServiceUnavailableException ex) {
		return ResponseEntity.status(503).body(new ErrorResponse(503, ex.getMessage()));
	}

	@ExceptionHandler(NetworkAuthenticationRequiredException.class)
	public ResponseEntity<ErrorResponse> handle511(NetworkAuthenticationRequiredException ex) {
		return ResponseEntity.status(511).body(new ErrorResponse(511, ex.getMessage()));
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormat(InvalidFormatException ex) {
		return ResponseEntity.status(400)
				.body(new ErrorResponse(400, "Valor inválido para o campo: " + ex.getPath().get(0).getFieldName()));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
		String mensagem = ex.getConstraintViolations().stream()
				.map(v -> "Valor inválido para o campo: " + v.getPropertyPath().toString()).findFirst()
				.orElse("Dados inválidos");

		return ResponseEntity.status(400).body(new ErrorResponse(400, mensagem));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
		if (ex.getCause() instanceof InvalidFormatException) {
			InvalidFormatException cause = (InvalidFormatException) ex.getCause();
			String field = cause.getPath().get(0).getFieldName();
			return ResponseEntity.status(400)
					.body(new ErrorResponse(400, "Tipo de valor inválido para o campo: " + field));
		}
		return ResponseEntity.status(400)
				.body(new ErrorResponse(400, "Erro de leitura no corpo da requisição. Verifique os dados enviados."));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
		String mensagem = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage())
				.findFirst().orElse("Dados inválidos");

		return ResponseEntity.status(400).body(new ErrorResponse(400, mensagem));
	}
}
