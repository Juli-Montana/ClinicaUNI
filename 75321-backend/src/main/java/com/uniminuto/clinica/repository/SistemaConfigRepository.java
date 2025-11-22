package com.uniminuto.clinica.repository;

import com.uniminuto.clinica.entity.SistemaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad SistemaConfig.
 * 
 * @author lmora
 */
@Repository
public interface SistemaConfigRepository extends JpaRepository<SistemaConfig, Long> {

    /**
     * Busca una configuración por su clave.
     */
    Optional<SistemaConfig> findByClave(String clave);
}