package com.transferencia.conta.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transferencia.conta.dto.AdminLoginResponse;
import com.transferencia.conta.dto.ContaDTO;
import com.transferencia.conta.dto.LoginRequest;
import com.transferencia.conta.dto.ResponseLogDTO;
import com.transferencia.conta.dto.UsuarioCreateDTO;
import com.transferencia.conta.dto.UsuarioDTO;
import com.transferencia.conta.dto.UsuarioResponseDTO;
import com.transferencia.conta.exceptions.BadRequestException;
import com.transferencia.conta.infra.security.TokenService;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.UsuarioRepository;
import com.transferencia.conta.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final UsuarioService usuarioService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest body) {

		if (body.email() != null) {
			var usuarioOpt = usuarioRepository.findByEmail(body.email());

			if (usuarioOpt.isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
			}

			Usuario usuario = usuarioOpt.get();

			if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
				String token = tokenService.generateToken(usuario);
				return ResponseEntity.ok(new AdminLoginResponse(usuario.getId(), token));
			}

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
		}

		var usuarioOpt = usuarioRepository.buscarPorAgenciaEConta(body.agencia(), body.numeroConta());

		if (usuarioOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado");
		}

		Usuario usuario = usuarioOpt.get();

		if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
			String token = tokenService.generateToken(usuario);
			return ResponseEntity.ok(new ResponseLogDTO(usuario.getId(), usuario.getConta().getAgencia(),
					usuario.getConta().getNumeroConta(), token));
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
	}

	@PostMapping("/register")
	public ResponseEntity<?> registrar(@Valid @RequestBody UsuarioCreateDTO dto) {
		try {
			Usuario novoUsuario = usuarioService.cadastrar(dto);

			ContaDTO contaDTO = novoUsuario.getConta() != null ? new ContaDTO(novoUsuario.getConta().getId(),
					novoUsuario.getConta().getAgencia(), novoUsuario.getConta().getNumeroConta(),
					novoUsuario.getConta().getSaldo(), novoUsuario.getId()) : null;

			UsuarioDTO responseDTO = new UsuarioDTO(novoUsuario.getId(), novoUsuario.getNome(), novoUsuario.getEmail(),
					null, novoUsuario.getCpf(), contaDTO);

			UsuarioResponseDTO usuarioResponse = new UsuarioResponseDTO("usuario criado com sucesso", responseDTO);
			return ResponseEntity.status(201).body(usuarioResponse);

		} catch (BadRequestException e) {
			return ResponseEntity.badRequest()
					.body(new UsuarioResponseDTO("erro ao criar usuário: " + e.getMessage(), null));
		}
	}
}
