package com.uniminuto.clinica.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Request para recuperación de contraseña.
 * 
 * @author lmora
 */
@Data
public class RecuperarPasswordRq implements Serializable {

    /**
     * Correo electrónico para recuperar contraseña.
     */
    @NotBlank(message = "El correo electrónico es obligatorio")
    private String email;
}