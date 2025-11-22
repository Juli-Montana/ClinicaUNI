package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad LoginAttempt.
 * 
 * @author lmora
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    /**
     * Obtiene los intentos de login de un usuario en un rango de fechas.
     */
    List<LoginAttempt> findByUsernameAndFechaHoraBetween(
            String username,
            LocalDateTime inicio,
            LocalDateTime fin);

    /**
     * Cuenta los intentos fallidos de un usuario desde una fecha.
     */
    long countByUsernameAndExitosoAndFechaHoraAfter(
            String username,
            Boolean exitoso,
            LocalDateTime desde);
}