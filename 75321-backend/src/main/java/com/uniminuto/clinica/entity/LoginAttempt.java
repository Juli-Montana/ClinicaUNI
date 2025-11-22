package com.uniminuto.clinica.entity;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entidad para registrar intentos de inicio de sesión.
 */
@Data
@Entity
@Table(name = "login_attempt")
public class LoginAttempt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Nombre de usuario del intento.
     */
    @Column(name = "username", nullable = false)
    private String username;

    /**
     * Fecha y hora del intento.
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /**
     * Dirección IP del intento.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Indica si el intento fue exitoso.
     */
    @Column(name = "exitoso", nullable = false)
    private Boolean exitoso;

    /**
     * Mensaje descriptivo del resultado.
     */
    @Column(name = "mensaje", length = 500)
    private String mensaje;

    /**
     * Constructor por defecto.
     */
    public LoginAttempt() {
        this.fechaHora = LocalDateTime.now();
    }
}