package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.model.AuditoriaLogPageRs;
import com.uniminuto.clinica.model.AuditoriaLogRs;
import com.uniminuto.clinica.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controlador para consultar logs de auditoría.
 * 
 * @author lmora
 */
@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Consulta logs de auditoría con filtros y paginación.
     * GET /auditoria/logs
     * 
     * Parámetros opcionales:
     * - username: Filtrar por usuario
     * - tipoEvento: Filtrar por tipo de evento
     * - fechaInicio: Fecha inicio (formato: yyyy-MM-dd'T'HH:mm:ss)
     * - fechaFin: Fecha fin (formato: yyyy-MM-dd'T'HH:mm:ss)
     * - page: Número de página (default: 0)
     * - size: Elementos por página (default: 20)
     * 
     * @return Logs paginados
     */
    @GetMapping("/logs")
    public ResponseEntity<AuditoriaLogPageRs> consultarLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String tipoEvento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        System.out.println(String.format(
                "[CONSULTA_LOGS] username=%s, tipoEvento=%s, fechaInicio=%s, fechaFin=%s, page=%d, size=%d",
                username, tipoEvento, fechaInicio, fechaFin, page, size));

        // Consultar servicio
        Page<AuditoriaLogRs> logsPage = auditoriaService.consultarLogs(
                username, tipoEvento, fechaInicio, fechaFin, page, size);

        // Convertir a response paginado
        AuditoriaLogPageRs response = new AuditoriaLogPageRs();
        response.setContenido(logsPage.getContent());
        response.setPaginaActual(logsPage.getNumber());
        response.setTotalPaginas(logsPage.getTotalPages());
        response.setTotalElementos(logsPage.getTotalElements());
        response.setElementosPorPagina(logsPage.getSize());
        response.setEsUltimaPagina(logsPage.isLast());
        response.setEsPrimeraPagina(logsPage.isFirst());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint simple para verificar que el módulo de auditoría funciona.
     * GET /auditoria/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Módulo de auditoría funcionando correctamente");
    }
}