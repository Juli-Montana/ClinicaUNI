package com.uniminuto.clinica.model;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Response paginada para logs de auditoría.
 * 
 * @author lmora
 */
@Data
public class AuditoriaLogPageRs implements Serializable {

    private List<AuditoriaLogRs> contenido;
    private int paginaActual;
    private int totalPaginas;
    private long totalElementos;
    private int elementosPorPagina;
    private boolean esUltimaPagina;
    private boolean esPrimeraPagina;
}