package com.uniminuto.clinica.model;

import lombok.Data;
import java.io.Serializable;

/**
 * Response para logs de auditoría.
 * 
 * @author lmora
 */
@Data
public class AuditoriaLogRs implements Serializable {

    private Long id;
    private String fechaHora;
    private String username;
    private String tipoEvento;
    private String descripcion;
    private String ipAddress;
    private Boolean exitoso;
}