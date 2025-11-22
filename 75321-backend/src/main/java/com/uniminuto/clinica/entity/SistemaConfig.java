package com.uniminuto.clinica.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar configuraciones del sistema.
 */
@Data
@Entity
@Table(name = "sistema_config")
public class SistemaConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Clave única de configuración.
     */
    @Column(name = "clave", unique = true, nullable = false, length = 100)
    private String clave;

    /**
     * Valor de la configuración.
     */
    @Column(name = "valor", nullable = false, length = 500)
    private String valor;

    /**
     * Descripción de la configuración.
     */
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /**
     * Fecha de última modificación.
     */
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PreUpdate
    @PrePersist
    public void actualizarFecha() {
        this.fechaModificacion = LocalDateTime.now();
    }
}