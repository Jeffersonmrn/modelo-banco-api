package com.transferencia.conta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transferencia.conta.enums.TipoTransferencia;
import com.transferencia.conta.model.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {

	List<Transferencia> findByTipo(TipoTransferencia tipo);

	List<Transferencia> findByOrigemIdOrDestinoId(Long origemId, Long destinoId);

	List<Transferencia> findByOrigemIdOrDestinoIdOrderByDataHoraDesc(Long origemId, Long destinoId);

}
