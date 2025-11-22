package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.AuditoriaLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad AuditoriaLog.
 * 
 * @author lmora
 */
@Repository
public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Long> {

    /**
     * Busca logs por username.
     */
    List<AuditoriaLog> findByUsername(String username);

    /**
     * Busca logs por tipo de evento.
     */
    List<AuditoriaLog> findByTipoEvento(String tipoEvento);

    /**
     * Busca logs por rango de fechas.
     */
    List<AuditoriaLog> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Búsqueda con filtros múltiples y paginación.
     */
    @Query("SELECT a FROM AuditoriaLog a WHERE " +
            "(:username IS NULL OR a.username = :username) AND " +
            "(:tipoEvento IS NULL OR a.tipoEvento = :tipoEvento) AND " +
            "(:fechaInicio IS NULL OR a.fechaHora >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR a.fechaHora <= :fechaFin)")
    Page<AuditoriaLog> findByFiltros(
            @Param("username") String username,
            @Param("tipoEvento") String tipoEvento,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable);
}