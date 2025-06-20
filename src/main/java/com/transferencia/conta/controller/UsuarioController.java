package com.transferencia.conta.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transferencia.conta.dto.ContaDTO;
import com.transferencia.conta.dto.ExtratoDTO;
import com.transferencia.conta.dto.TransferenciaDTO;
import com.transferencia.conta.dto.UsuarioAdminDTO;
import com.transferencia.conta.dto.UsuarioDTO;
import com.transferencia.conta.dto.UsuarioUpdateDTO;
import com.transferencia.conta.enums.Role;
import com.transferencia.conta.exceptions.ForbiddenException;
import com.transferencia.conta.exceptions.NotFoundException;
import com.transferencia.conta.exceptions.UnauthorizedException;
import com.transferencia.conta.infra.security.TokenService;
import com.transferencia.conta.model.Conta;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.ContaRepository;
import com.transferencia.conta.repository.TransferenciaRepository;
import com.transferencia.conta.repository.UsuarioRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ContaRepository contaRepo;

	@Autowired
	private TransferenciaRepository transferenciaRepo;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping
	public ResponseEntity<List<Object>> listarTodos(@RequestHeader("Authorization") String token) {
		Usuario usuarioLogado = getUsuarioAutenticado(token);

		if (usuarioLogado.getRole() != Role.ROLE_ADMIN) {
			throw new ForbiddenException("Apenas administradores podem listar todos os usuários");
		}

		List<Object> lista = usuarioRepo.findAll().stream().map(u -> {
			if (u.getRole() == Role.ROLE_ADMIN) {
				return new UsuarioAdminDTO(u.getNome(), u.getEmail());
			} else {
				Conta conta = u.getConta();
				ContaDTO contaDTO = (conta != null)
						? new ContaDTO(conta.getId(), conta.getAgencia(), conta.getNumeroConta(), conta.getSaldo(),
								u.getId())
						: null;
				return new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), null, u.getCpf(), contaDTO);
			}
		}).collect(Collectors.toList());

		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		Usuario usuarioLogado = getUsuarioAutenticado(token);

		if (usuarioLogado.getRole() != Role.ROLE_ADMIN && !usuarioLogado.getId().equals(id)) {
			throw new ForbiddenException("Você só pode acessar seus próprios dados");
		}

		return usuarioRepo.findById(id).map(u -> {
			Conta conta = u.getConta();
			ContaDTO contaDTO = (conta != null)
					? new ContaDTO(conta.getId(), conta.getAgencia(), conta.getNumeroConta(), conta.getSaldo(),
							u.getId())
					: null;
			return ResponseEntity.ok(new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), null, u.getCpf(), contaDTO));
		}).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/extrato")
	public ResponseEntity<ExtratoDTO> buscarExtrato(@PathVariable Long id,
			@RequestHeader("Authorization") String token) {

		Usuario usuarioLogado = getUsuarioAutenticado(token);

		if (usuarioLogado.getRole() != Role.ROLE_ADMIN && !usuarioLogado.getId().equals(id)) {
			throw new ForbiddenException("Você só pode consultar seu próprio extrato");
		}

		Conta conta = contaRepo.findByUsuarioId(id).orElseThrow(() -> new NotFoundException("Conta não encontrada"));

		List<TransferenciaDTO> transferenciasDTO = transferenciaRepo
				.findByOrigemIdOrDestinoIdOrderByDataHoraDesc(conta.getId(), conta.getId()).stream()
				.map(TransferenciaDTO::new).toList();

		ExtratoDTO extrato = new ExtratoDTO(usuarioLogado.getId(), usuarioLogado.getNome(), conta.getSaldo(),
				transferenciasDTO);

		return ResponseEntity.ok(extrato);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO dto,
			@RequestHeader("Authorization") String token) {

		Usuario usuarioLogado = getUsuarioAutenticado(token);

		if (usuarioLogado.getRole() != Role.ROLE_ADMIN && !usuarioLogado.getId().equals(id)) {
			throw new ForbiddenException("Você só pode atualizar seus próprios dados");
		}

		return usuarioRepo.findById(id).map(usuario -> {
			if (usuarioLogado.getRole() == Role.ROLE_ADMIN) {
				if (dto.nome != null)
					usuario.setNome(dto.nome);
				if (dto.cpf != null)
					usuario.setCpf(dto.cpf);
			}

			usuario.setEmail(dto.email);
			usuario.setSenha(passwordEncoder.encode(dto.senha));

			Usuario atualizado = usuarioRepo.save(usuario);

			Conta conta = contaRepo.findByUsuarioId(atualizado.getId()).orElse(null);
			ContaDTO contaDTO = (conta != null)
					? new ContaDTO(conta.getId(), conta.getAgencia(), conta.getNumeroConta(), conta.getSaldo(),
							atualizado.getId())
					: null;

			return ResponseEntity.ok(new UsuarioDTO(atualizado.getId(), atualizado.getNome(), atualizado.getEmail(),
					null, atualizado.getCpf(), contaDTO));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletar(@PathVariable Long id, @RequestHeader("Authorization") String token) {
		Usuario usuarioLogado = getUsuarioAutenticado(token);

		if (usuarioLogado.getRole() != Role.ROLE_ADMIN) {
			throw new ForbiddenException("Apenas administradores podem deletar usuários");
		}

		if (usuarioLogado.getId().equals(id)) {
			throw new ForbiddenException("Administradores não podem deletar a própria conta");
		}

		return usuarioRepo.findById(id).map(usuario -> {
			usuarioRepo.delete(usuario);
			return ResponseEntity.noContent().build();
		}).orElse(ResponseEntity.notFound().build());
	}

	private Usuario getUsuarioAutenticado(String token) {
		String rawToken = token.replace("Bearer ", "").trim();
		String cpf = tokenService.validateToken(rawToken);
		return usuarioRepo.findByCpf(cpf).orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));
	}
}
