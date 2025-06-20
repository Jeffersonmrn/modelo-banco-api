package com.transferencia.conta.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.transferencia.conta.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByCpf(String cpf);

	Optional<Usuario> findByEmail(String email);

	@Query("SELECT u FROM Usuario u JOIN u.conta c WHERE c.agencia = :agencia AND c.numeroConta = :numeroConta")
	Optional<Usuario> buscarPorAgenciaEConta(@Param("agencia") String agencia,
			@Param("numeroConta") String numeroConta);

	boolean existsByCpf(String cpf);

	boolean existsByEmail(String email);

}
