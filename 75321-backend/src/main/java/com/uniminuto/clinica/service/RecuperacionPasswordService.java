package com.uniminuto.clinica.service;

import com.uniminuto.clinica.model.RecuperarPasswordRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import javax.mail.MessagingException;

/**
 * Servicio para recuperación de contraseña.
 * 
 * @author lmora
 */
public interface RecuperacionPasswordService {

    /**
     * Procesa la solicitud de recuperación de contraseña.
     * 
     * @param request   Datos de la solicitud
     * @param ipAddress IP del cliente
     * @return Respuesta genérica (sin revelar si el usuario existe)
     * @throws BadRequestException Error en la validación
     * @throws MessagingException  Error al enviar correo
     */
    RespuestaRs recuperarPassword(RecuperarPasswordRq request, String ipAddress)
            throws BadRequestException, MessagingException;
}