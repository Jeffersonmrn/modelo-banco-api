package com.transferencia.conta.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transferencia.conta.dto.ContaDTO;
import com.transferencia.conta.dto.DepositoResponseDTO;
import com.transferencia.conta.dto.DepositoSaqueDTO;
import com.transferencia.conta.dto.SaqueResponseDTO;
import com.transferencia.conta.infra.security.TokenService;
import com.transferencia.conta.model.Conta;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.ContaRepository;
import com.transferencia.conta.repository.UsuarioRepository;
import com.transferencia.conta.service.TransferenciaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

	@Autowired
	private ContaRepository contaRepo;

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private TransferenciaService service;

	@Autowired
	private TokenService tokenService;

	@GetMapping
	public ResponseEntity<?> listarContas(@RequestHeader("Authorization") String token) {
		Usuario usuario = recuperarUsuarioPorToken(token);
		if (usuario == null) {
			return ResponseEntity.status(401).body("Usuário não autorizado");
		}

		if (usuario.getRole().isAdmin()) {
			List<ContaDTO> contas = contaRepo.findAll().stream().map(ContaDTO::new).collect(Collectors.toList());
			return ResponseEntity.ok(contas);
		} else {
			List<Conta> contasDoUsuario = contaRepo.findAllByUsuarioId(usuario.getId());
			List<ContaDTO> dtos = contasDoUsuario.stream().map(ContaDTO::new).collect(Collectors.toList());
			return ResponseEntity.ok(dtos);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscarPorId(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		Usuario usuario = recuperarUsuarioPorToken(token);
		if (usuario == null) {
			return ResponseEntity.status(401).body("Usuário não autorizado");
		}

		return contaRepo.findById(id).map(conta -> {
			boolean isAdmin = usuario.getRole().isAdmin();
			boolean isDono = conta.getUsuario() != null && conta.getUsuario().getId().equals(usuario.getId());

			if (isAdmin || isDono) {
				return ResponseEntity.ok(new ContaDTO(conta));
			} else {
				return ResponseEntity.status(403).body("Acesso negado");
			}
		}).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<ContaDTO> criarConta(@Valid @RequestBody ContaDTO dto) {
		Usuario usuario = null;
		if (dto.usuarioId != null) {
			usuario = usuarioRepo.findById(dto.usuarioId).orElse(null);
		}

		Conta conta = new Conta();
		conta.setAgencia(dto.agencia);
		conta.setNumeroConta(dto.numeroConta);
		conta.setSaldo(dto.saldo != null ? dto.saldo : BigDecimal.ZERO);
		conta.setUsuario(usuario);

		Conta salva = contaRepo.save(conta);
		return ResponseEntity.ok(new ContaDTO(salva));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizarConta(@PathVariable Long id, @Valid @RequestBody ContaDTO dto,
			@RequestHeader("Authorization") String token) {
		Usuario usuario = recuperarUsuarioPorToken(token);
		if (usuario == null) {
			return ResponseEntity.status(401).body("Usuário não autorizado");
		}

		return contaRepo.findById(id).map(conta -> {
			boolean isAdmin = usuario.getRole().isAdmin();
			boolean isDono = conta.getUsuario() != null && conta.getUsuario().getId().equals(usuario.getId());

			if (!isAdmin && !isDono) {
				return ResponseEntity.status(403).body("Acesso negado");
			}

			conta.setAgencia(dto.agencia);
			conta.setNumeroConta(dto.numeroConta);
			if (dto.saldo != null) {
				conta.setSaldo(dto.saldo);
			}
			if (dto.usuarioId != null) {
				Usuario novoUsuario = usuarioRepo.findById(dto.usuarioId).orElse(null);
				conta.setUsuario(novoUsuario);
			}

			Conta atualizada = contaRepo.save(conta);
			return ResponseEntity.ok(new ContaDTO(atualizada));
		}).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/depositar")
	public ResponseEntity<?> depositar(@RequestBody DepositoSaqueDTO dto,
			@RequestHeader("Authorization") String token) {
		try {
			DepositoResponseDTO response = service.depositar(dto.getContaId(), dto.getValor(), token);
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/sacar")
	public ResponseEntity<?> sacar(@RequestBody DepositoSaqueDTO dto, @RequestHeader("Authorization") String token) {
		try {
			SaqueResponseDTO resposta = service.sacar(dto.getContaId(), dto.getValor(), token);
			return ResponseEntity.ok(resposta);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletarConta(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		Usuario usuario = recuperarUsuarioPorToken(token);
		if (usuario == null) {
			return ResponseEntity.status(401).body("Usuário não autorizado");
		}

		return contaRepo.findById(id).map(conta -> {
			boolean isAdmin = usuario.getRole().isAdmin();
			boolean isDono = conta.getUsuario() != null && conta.getUsuario().getId().equals(usuario.getId());

			if (!isAdmin && !isDono) {
				return ResponseEntity.status(403).body("Acesso negado");
			}

			contaRepo.delete(conta);
			return ResponseEntity.noContent().build();
		}).orElse(ResponseEntity.notFound().build());
	}

	private Usuario recuperarUsuarioPorToken(String token) {
		String cpf = tokenService.getCpfByToken(token);
		return usuarioRepo.findByCpf(cpf).orElse(null);
	}
}
