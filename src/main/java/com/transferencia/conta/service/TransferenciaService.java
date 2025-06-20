package com.transferencia.conta.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.transferencia.conta.dto.DepositoResponseDTO;
import com.transferencia.conta.dto.PixDTO;
import com.transferencia.conta.dto.SaqueResponseDTO;
import com.transferencia.conta.dto.TransferenciaRequestDTO;
import com.transferencia.conta.enums.TipoTransferencia;
import com.transferencia.conta.exceptions.BadRequestException;
import com.transferencia.conta.exceptions.ForbiddenException;
import com.transferencia.conta.exceptions.NotFoundException;
import com.transferencia.conta.exceptions.UnauthorizedException;
import com.transferencia.conta.infra.security.TokenService;
import com.transferencia.conta.model.Conta;
import com.transferencia.conta.model.Transferencia;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.ContaRepository;
import com.transferencia.conta.repository.TransferenciaRepository;
import com.transferencia.conta.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class TransferenciaService {

	private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private TransferenciaRepository transferenciaRepo;

	@Autowired
	private ContaRepository contaRepo;

	@Autowired
	private TokenService tokenService;

	public Transferencia criarTransferencia(TransferenciaRequestDTO dto, String cpf) {

		if (dto.valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("O valor da transferência deve ser maior que zero");
		}

		Usuario remetente = usuarioRepository.findByCpf(cpf)
				.orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

		Conta origem = contaRepo.findById(dto.origemId)
				.orElseThrow(() -> new NotFoundException("Conta origem não encontrada"));

		if (!origem.getUsuario().equals(remetente)) {
			throw new ForbiddenException("Usuário não autorizado para essa conta");
		}

		if (origem.getSaldo().compareTo(dto.valor) < 0) {
			throw new BadRequestException("Saldo insuficiente");
		}

		Conta destino = contaRepo.findById(dto.destinoId)
				.orElseThrow(() -> new NotFoundException("Conta destino não encontrada"));

		Transferencia t = new Transferencia();
		t.setOrigem(origem);
		t.setDestino(destino);
		t.setValor(dto.valor);
		t.setTipo(dto.tipo);
		t.setDataHora(LocalDateTime.now());

		if (dto.tipo == TipoTransferencia.PIX) {
			t.setChavePix(dto.chavePix);

			PixDTO pixDTO = new PixDTO();
			pixDTO.setChave(dto.chavePix);
			pixDTO.setValor(dto.valor);

			ResponseEntity<String> resposta = restTemplate.postForEntity("http://localhost:8081/pix/enviar", pixDTO,
					String.class);

			logger.info("Resposta do Banco Central Simulado: {}", resposta.getBody());
		} else {
			t.setChavePix(null);
		}

		origem.setSaldo(origem.getSaldo().subtract(dto.valor));
		destino.setSaldo(destino.getSaldo().add(dto.valor));
		contaRepo.save(origem);
		contaRepo.save(destino);

		return transferenciaRepo.save(t);
	}

	public Optional<Transferencia> buscarPorId(Long id) {
		return transferenciaRepo.findById(id);
	}

	public List<Transferencia> listarTodas() {
		return transferenciaRepo.findAll();
	}

	@Transactional
	public DepositoResponseDTO depositar(Long contaId, BigDecimal valor, String token) {

		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("O valor do depósito deve ser maior que zero");
		}

		Usuario usuario = autenticarUsuario(token);

		Conta conta = contaRepo.findById(contaId).orElseThrow(() -> new NotFoundException("Conta não encontrada"));

		if (!conta.getUsuario().equals(usuario)) {
			throw new ForbiddenException("Você só pode depositar na sua própria conta");
		}

		conta.setSaldo(conta.getSaldo().add(valor));
		contaRepo.save(conta);

		Transferencia t = new Transferencia();
		t.setOrigem(null);
		t.setDestino(conta);
		t.setValor(valor);
		t.setTipo(TipoTransferencia.DEPOSITO);
		t.setDataHora(LocalDateTime.now());
		transferenciaRepo.save(t);

		return new DepositoResponseDTO(valor, conta.getSaldo());
	}

	@Transactional
	public SaqueResponseDTO sacar(Long contaId, BigDecimal valor, String token) {

		if (valor.compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("O valor do saque deve ser maior que zero");
		}

		Usuario usuario = autenticarUsuario(token);

		Conta conta = contaRepo.findById(contaId).orElseThrow(() -> new NotFoundException("Conta não encontrada"));

		if (!conta.getUsuario().equals(usuario)) {
			throw new ForbiddenException("Você só pode sacar da sua própria conta");
		}

		if (conta.getSaldo().compareTo(valor) < 0) {
			throw new BadRequestException("Saldo insuficiente");
		}

		conta.setSaldo(conta.getSaldo().subtract(valor));
		contaRepo.save(conta);

		Transferencia t = new Transferencia();
		t.setOrigem(conta);
		t.setDestino(null);
		t.setValor(valor);
		t.setTipo(TipoTransferencia.SAQUE);
		t.setDataHora(LocalDateTime.now());
		transferenciaRepo.save(t);

		return new SaqueResponseDTO(valor, conta.getSaldo());
	}

	private Usuario autenticarUsuario(String token) {
		String rawToken = token.replace("Bearer ", "").trim();
		String login = tokenService.validateToken(rawToken);
		if (login == null) {
			throw new UnauthorizedException("Usuário não autenticado");
		}
		return usuarioRepository.findByCpf(login).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
	}
}
