package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.AuditoriaLog;
import com.uniminuto.clinica.model.AuditoriaLogRs;
import com.uniminuto.clinica.repository.AuditoriaLogRepository;
import com.uniminuto.clinica.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de auditoría.
 * 
 * @author lmora
 */
@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    @Autowired
    private AuditoriaLogRepository auditoriaLogRepository;

    /**
     * Registra un evento en el log de auditoría.
     */
    @Override
    @Transactional
    public void registrarEvento(String username, String tipoEvento,
            String descripcion, String ipAddress, Boolean exitoso) {
        AuditoriaLog log = new AuditoriaLog();
        log.setUsername(username);
        log.setTipoEvento(tipoEvento);
        log.setDescripcion(descripcion);
        log.setIpAddress(ipAddress);
        log.setExitoso(exitoso);
        log.setFechaHora(LocalDateTime.now());

        auditoriaLogRepository.save(log);

        System.out.println(String.format(
                "[AUDITORIA] %s | Usuario: %s | IP: %s | Exitoso: %s | %s",
                tipoEvento, username, ipAddress, exitoso, descripcion));
    }

    /**
     * Consulta logs con filtros y paginación.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AuditoriaLogRs> consultarLogs(String username, String tipoEvento,
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            int page, int size) {
        // Crear paginación ordenada por fecha descendente
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "fechaHora"));

        // Consultar con filtros
        Page<AuditoriaLog> logsPage = auditoriaLogRepository.findByFiltros(
                username, tipoEvento, fechaInicio, fechaFin, pageable);

        // Convertir a DTO
        return logsPage.map(this::convertirADto);
    }

    /**
     * Convierte una entidad AuditoriaLog a DTO.
     */
    private AuditoriaLogRs convertirADto(AuditoriaLog log) {
        AuditoriaLogRs dto = new AuditoriaLogRs();
        dto.setId(log.getId());
        dto.setUsername(log.getUsername());
        dto.setTipoEvento(log.getTipoEvento());
        dto.setDescripcion(log.getDescripcion());
        dto.setIpAddress(log.getIpAddress());
        dto.setExitoso(log.getExitoso());

        // Formatear fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dto.setFechaHora(log.getFechaHora().format(formatter));

        return dto;
    }
}