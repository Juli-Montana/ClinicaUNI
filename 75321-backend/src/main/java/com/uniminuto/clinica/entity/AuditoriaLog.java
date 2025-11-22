package com.uniminuto.clinica.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad para registrar logs de auditoría del sistema.
 */
@Data
@Entity
@Table(name = "auditoria_log")
public class AuditoriaLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Fecha y hora del evento.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Nombre de usuario relacionado con el evento.
     */
    @Column(name = "username")
    private String username;

    /**
     * Tipo de evento (LOGIN_FALLIDO, LOGIN_EXITOSO, RECUPERACION_PASSWORD, etc).
     */
    @Column(name = "tipo_evento", nullable = false, length = 100)
    private String tipoEvento;

    /**
     * Descripción detallada del evento.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Dirección IP desde donde se realizó la acción.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Indica si la operación fue exitosa.
     */
    @Column(name = "exitoso")
    private Boolean exitoso;

    /**
     * Constructor por defecto que inicializa la fecha/hora actual.
     */
    public AuditoriaLog() {
        this.fechaHora = LocalDateTime.now();
    }
}