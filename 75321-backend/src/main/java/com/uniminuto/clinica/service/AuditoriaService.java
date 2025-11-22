package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AuditoriaLog;
import com.uniminuto.clinica.model.AuditoriaLogRs;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * Servicio para gestión de logs de auditoría.
 * 
 * @author lmora
 */
public interface AuditoriaService {

    /**
     * Registra un evento de auditoría.
     * 
     * @param username    Usuario relacionado
     * @param tipoEvento  Tipo de evento
     * @param descripcion Descripción del evento
     * @param ipAddress   Dirección IP
     * @param exitoso     Si fue exitoso o no
     */
    void registrarEvento(String username, String tipoEvento,
            String descripcion, String ipAddress, Boolean exitoso);

    /**
     * Consulta logs con filtros y paginación.
     * 
     * @param username    Filtro por usuario (opcional)
     * @param tipoEvento  Filtro por tipo de evento (opcional)
     * @param fechaInicio Fecha inicio (opcional)
     * @param fechaFin    Fecha fin (opcional)
     * @param page        Número de página
     * @param size        Tamaño de página
     * @return Página de logs
     */
    Page<AuditoriaLogRs> consultarLogs(String username, String tipoEvento,
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            int page, int size);
}