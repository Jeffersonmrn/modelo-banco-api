package com.transferencia.conta.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transferencia.conta.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {

	Optional<Conta> findByUsuarioId(Long usuarioId);
	
	List<Conta> findAllByUsuarioId(Long usuarioId);

	boolean existsByNumeroConta(String numeroConta);

}
