package com.transferencia.conta.service;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.transferencia.conta.dto.UsuarioCreateDTO;
import com.transferencia.conta.enums.Role;
import com.transferencia.conta.exceptions.BadRequestException;
import com.transferencia.conta.model.Conta;
import com.transferencia.conta.model.Usuario;
import com.transferencia.conta.repository.ContaRepository;
import com.transferencia.conta.repository.UsuarioRepository;
import com.transferencia.conta.utils.CpfUtils;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepo;

	@Autowired
	private ContaRepository contaRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Usuario cadastrar(UsuarioCreateDTO dto) {
	    if (!CpfUtils.isCpfValido(dto.getCpf())) {
	        throw new BadRequestException("CPF inválido");
	    }

	    validarUsuarioExistente(dto.getCpf(), dto.getEmail());

	    Usuario usuario = new Usuario();
	    usuario.setNome(dto.getNome());
	    usuario.setEmail(dto.getEmail());
	    usuario.setCpf(dto.getCpf());
	    usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
	    usuario.setRole(Role.ROLE_USER); 

	    Conta conta = criarContaParaUsuario(usuario);

	    usuario.setConta(conta);
	    conta.setUsuario(usuario);

	    return usuarioRepo.save(usuario);
	}

	private void validarUsuarioExistente(String cpf, String email) {
		if (usuarioRepo.existsByCpf(cpf)) {
			throw new BadRequestException("CPF já cadastrado");
		}
		if (usuarioRepo.existsByEmail(email)) {
			throw new BadRequestException("Email já cadastrado");
		}
	}

	private Conta criarContaParaUsuario(Usuario usuario) {
		Conta conta = new Conta();
		conta.setAgencia("0002");
		conta.setNumeroConta(gerarNumeroContaUnico());
		conta.setSaldo(BigDecimal.ZERO);

		conta = contaRepo.save(conta);
		return conta;
	}

	private String gerarNumeroContaUnico() {
		Random random = new Random();
		String numeroConta;
		do {
			int parteNumerica = 10000000 + random.nextInt(90000000);
			int digito = random.nextInt(10);
			numeroConta = parteNumerica + "-" + digito;
		} while (contaRepo.existsByNumeroConta(numeroConta));
		return numeroConta;
	}
}
