package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.model.RecuperarPasswordRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.RecuperacionPasswordService;
import com.uniminuto.clinica.utils.IpUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controlador para recuperación de contraseña.
 * 
 * @author lmora
 */
@RestController
@RequestMapping("/auth")
public class RecuperacionPasswordController {

    @Autowired
    private RecuperacionPasswordService recuperacionPasswordService;

    @Autowired
    private HttpServletRequest request;

    /**
     * Endpoint para solicitar recuperación de contraseña.
     * POST /auth/recuperar-contrasena
     * 
     * @param recuperarRq Datos de recuperación (username)
     * @return Respuesta genérica (no revela si el usuario existe)
     * @throws BadRequestException Error en validación
     * @throws MessagingException  Error al enviar correo
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<RespuestaRs> recuperarPassword(
            @Valid @RequestBody RecuperarPasswordRq recuperarRq)
            throws BadRequestException, MessagingException {

        // Obtener IP del cliente
        String ipAddress = IpUtils.obtenerIpCliente(request);

        System.out.println(String.format(
                "[RECUPERACION_PASSWORD] Email: %s | IP: %s",
                recuperarRq.getEmail(), ipAddress));

        // Procesar recuperación
        RespuestaRs response = recuperacionPasswordService.recuperarPassword(
                recuperarRq, ipAddress);

        return ResponseEntity.ok(response);
    }
}