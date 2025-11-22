package com.uniminuto.clinica.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author lmora
 */
@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    /**
     * Id serializable.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "rol")
    private String rol;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "activo")
    private boolean activo;

    @Column(name = "email")
    private String email;

    /**
     * Contador de intentos fallidos de login.
     */
    @Column(name = "intentos_fallidos")
    private Integer intentosFallidos = 0;

    /**
     * Fecha del último intento de login.
     */
    @Column(name = "fecha_ultimo_intento")
    private LocalDateTime fechaUltimoIntento;

    /**
     * Fecha hasta la cual el usuario está bloqueado.
     */
    @Column(name = "bloqueado_hasta")
    private LocalDateTime bloqueadoHasta;

    /**
     * Verifica si el usuario está actualmente bloqueado.
     * 
     * @return true si está bloqueado, false en caso contrario
     */
    public boolean estaBloqueado() {
        if (bloqueadoHasta == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(bloqueadoHasta);
    }
}