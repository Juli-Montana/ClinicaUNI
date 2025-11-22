package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.model.RecuperarPasswordRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.AuditoriaService;
import com.uniminuto.clinica.service.CifrarService;
import com.uniminuto.clinica.service.EmailService;
import com.uniminuto.clinica.service.RecuperacionPasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import org.apache.coyote.BadRequestException;
import javax.mail.MessagingException;
import java.util.Optional;

/**
 * Implementación del servicio de recuperación de contraseña.
 * 
 * @author lmora
 */
@Service
public class RecuperacionPasswordServiceImpl implements RecuperacionPasswordService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CifrarService cifrarService;

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Procesa la recuperación de contraseña.
     * IMPORTANTE: Por seguridad, siempre devuelve el mismo mensaje,
     * independientemente de si el usuario existe o no.
     */
    @Override
    @Transactional
    public RespuestaRs recuperarPassword(RecuperarPasswordRq request, String ipAddress)
            throws MessagingException {

        // Validar que el email no esté vacío
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo electrónico es requerido");
        }

        String email = request.getEmail().trim().toLowerCase();

        // Buscar el usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            // Usuario NO existe - Registrar log de auditoría
            auditoriaService.registrarEvento(
                    email,
                    "RECUPERACION_PASSWORD_FALLIDO",
                    "Intento de recuperación de contraseña con correo inexistente: " + email,
                    ipAddress,
                    false);

            // Respuesta genérica
            return crearRespuestaGenerica();
        }

        // Usuario SÍ existe - Proceder con la recuperación
        Usuario usuario = usuarioOpt.get();

        // Validar que el usuario tenga email configurado
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            // Log de auditoría
            auditoriaService.registrarEvento(
                    email,
                    "RECUPERACION_PASSWORD_FALLIDO",
                    "Usuario sin correo electrónico configurado",
                    ipAddress,
                    false);

            return crearRespuestaGenerica();
        }

        // Generar contraseña temporal
        String passwordTemporal = generarPasswordTemporal();

        // Actualizar contraseña en la base de datos
        usuario.setPassword(cifrarService.encriptarPassword(passwordTemporal));
        usuarioRepository.save(usuario);

        // Enviar correo con la contraseña temporal
        enviarCorreoRecuperacion(usuario, passwordTemporal);

        // Registrar evento exitoso en auditoría
        auditoriaService.registrarEvento(
                email,
                "RECUPERACION_PASSWORD_EXITOSO",
                "Contraseña temporal enviada al correo: " + ocultarEmail(usuario.getEmail()),
                ipAddress,
                true);

        return crearRespuestaGenerica();
    }

    /**
     * Genera una contraseña temporal aleatoria de 10 caracteres.
     */
    private String generarPasswordTemporal() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder password = new StringBuilder();
        int longitud = 10;

        for (int i = 0; i < longitud; i++) {
            int indice = (int) (Math.random() * caracteres.length());
            password.append(caracteres.charAt(indice));
        }

        return password.toString();
    }

    /**
     * Envía el correo con la contraseña temporal.
     * 
     * @param usuario          Usuario que recibirá el correo
     * @param passwordTemporal Contraseña temporal generada
     * @throws MessagingException Si hay error al enviar el correo
     */
    private void enviarCorreoRecuperacion(Usuario usuario, String passwordTemporal)
            throws MessagingException {

        String htmlBody = String.format("""
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .password-box {
                            background-color: #fff3cd;
                            border: 2px solid #ffc107;
                            padding: 15px;
                            margin: 20px 0;
                            font-size: 18px;
                            font-weight: bold;
                            text-align: center;
                        }
                        .warning { color: #d32f2f; font-weight: bold; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Recuperación de Contraseña</h2>
                        </div>
                        <div class="content">
                            <p>Hola <strong>%s</strong>,</p>
                            <p>Has solicitado recuperar tu contraseña. Tu nueva contraseña temporal es:</p>

                            <div class="password-box">
                                %s
                            </div>

                            <p class="warning">⚠️ IMPORTANTE:</p>
                            <ul>
                                <li>Esta es una contraseña temporal</li>
                                <li>Por tu seguridad, cámbiala inmediatamente después de iniciar sesión</li>
                                <li>No compartas esta contraseña con nadie</li>
                            </ul>

                            <p>Si no solicitaste este cambio, contacta al administrador del sistema inmediatamente.</p>
                        </div>
                        <div class="footer">
                            <p>Este es un mensaje automático, por favor no responder.</p>
                            <p>&copy; 2025 Clínica Uniminuto - Sistema de Gestión</p>
                        </div>
                    </div>
                </body>
                </html>
                """,
                usuario.getUsername(),
                passwordTemporal);

        try {
            emailService.sendHtmlEmail(
                    usuario.getEmail(),
                    "Recuperación de Contraseña - Clínica Uniminuto",
                    htmlBody,
                    emailService.getTo());

            System.out.println("Correo de recuperación enviado a: " + usuario.getEmail());

        } catch (BadRequestException e) {
            // Convertir BadRequestException a MessagingException
            System.err.println("Error al enviar correo de recuperación: " + e.getMessage());
            throw new MessagingException("Error al enviar correo: " + e.getMessage());
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de recuperación: " + e.getMessage());
            throw e; // Re-lanzar la excepción
        }
    }

    /**
     * Crea una respuesta genérica que no revela información sensible.
     */
    private RespuestaRs crearRespuestaGenerica() {
        RespuestaRs respuesta = new RespuestaRs();
        respuesta.setStatus(200);
        respuesta.setMensaje(
                "Si el usuario existe, se ha enviado un correo electrónico " +
                        "con instrucciones para recuperar la contraseña.");
        return respuesta;
    }

    /**
     * Oculta parcialmente un email para los logs.
     */
    private String ocultarEmail(String email) {
        if (email == null || email.length() < 5) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            return email.charAt(0) + "***" + email.substring(atIndex);
        }
        return email.charAt(0) + "****" + email.charAt(atIndex - 1) + email.substring(atIndex);
    }
}
