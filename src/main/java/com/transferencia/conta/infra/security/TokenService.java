package com.transferencia.conta.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.transferencia.conta.exceptions.UnauthorizedException;
import com.transferencia.conta.model.Usuario;

@Service
public class TokenService {

	@Value("${api.security.token.secret}")
	private String secret;

	public String generateToken(Usuario authenticatedUser) {

		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);

			String token = JWT.create().withIssuer("login-auth-api").withSubject(authenticatedUser.getCpf())
					.withExpiresAt(this.generateExpirationDate()).sign(algorithm);
			return token;

		} catch (JWTCreationException exception) {
			throw new UnauthorizedException("Token inválido ou expirado");
		}
	}

	public String validateToken(String token) {

		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);

			return JWT.require(algorithm).withIssuer("login-auth-api").build().verify(token).getSubject();
		} catch (JWTVerificationException exception) {
			return null;
		}
	}

	private Instant generateExpirationDate() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}

	public String getCpfByToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secret);
			return JWT.require(algorithm).withIssuer("login-auth-api").build().verify(token.replace("Bearer ", ""))
					.getSubject();
		} catch (JWTVerificationException exception) {
			return null;
		}
	}
}
