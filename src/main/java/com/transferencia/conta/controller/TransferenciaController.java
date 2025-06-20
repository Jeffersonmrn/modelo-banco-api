package com.transferencia.conta.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transferencia.conta.dto.TransferenciaDTO;
import com.transferencia.conta.dto.TransferenciaRequestDTO;
import com.transferencia.conta.enums.Role;
import com.transferencia.conta.exceptions.ForbiddenException;
import com.transferencia.conta.exceptions.NotFoundException;
import com.transferencia.conta.exceptions.UnauthorizedException;
import com.transferencia.conta.infra.security.TokenService;
import com.transferencia.conta.model.Transferencia;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.UsuarioRepository;
import com.transferencia.conta.service.TransferenciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transferencias")
public class TransferenciaController {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	private final TransferenciaService transferenciaService;

	public TransferenciaController(TransferenciaService transferenciaService) {
		this.transferenciaService = transferenciaService;
	}

	@PostMapping
	public ResponseEntity<TransferenciaDTO> criarTransferencia(@Valid @RequestBody TransferenciaRequestDTO dto,
			@RequestHeader("Authorization") String token) {

		String cpf = tokenService.validateToken(token.replace("Bearer ", "").trim());
		if (cpf == null) {
			throw new UnauthorizedException("Token inválido ou expirado");
		}

		Transferencia transferencia = transferenciaService.criarTransferencia(dto, cpf);
		return ResponseEntity.status(HttpStatus.CREATED).body(new TransferenciaDTO(transferencia));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransferenciaDTO> buscarPorId(@PathVariable Long id,
			@RequestHeader("Authorization") String token) {

		String cpf = tokenService.validateToken(token.replace("Bearer ", "").trim());
		if (cpf == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Transferencia transferencia = transferenciaService.buscarPorId(id)
				.orElseThrow(() -> new NotFoundException("Transferência não encontrada"));

		Usuario usuario = usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

		if (usuario.getRole() == Role.ROLE_ADMIN) {
			return ResponseEntity.ok(new TransferenciaDTO(transferencia));
		}

		boolean autorizado = (transferencia.getOrigem() != null
				&& transferencia.getOrigem().getUsuario().equals(usuario))
				|| (transferencia.getDestino() != null && transferencia.getDestino().getUsuario().equals(usuario));

		if (!autorizado) {
			throw new ForbiddenException("Usuário não autorizado para visualizar essa transferência");
		}

		return ResponseEntity.ok(new TransferenciaDTO(transferencia));
	}

	@GetMapping
	public ResponseEntity<List<TransferenciaDTO>> listarTodaseMinhasTransferencias(
			@RequestHeader("Authorization") String token) {

		String cpf = tokenService.validateToken(token.replace("Bearer ", "").trim());
		if (cpf == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Usuario usuario = usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

		List<Transferencia> transferencias;

		if (usuario.getRole() == Role.ROLE_ADMIN) {

			transferencias = transferenciaService.listarTodas();
		} else {

			transferencias = transferenciaService.listarTodas().stream()
					.filter(t -> (t.getOrigem() != null && t.getOrigem().getUsuario().equals(usuario))
							|| (t.getDestino() != null && t.getDestino().getUsuario().equals(usuario)))
					.collect(Collectors.toList());
		}

		List<TransferenciaDTO> dtos = transferencias.stream().map(TransferenciaDTO::new).collect(Collectors.toList());

		return ResponseEntity.ok(dtos);
	}
}
