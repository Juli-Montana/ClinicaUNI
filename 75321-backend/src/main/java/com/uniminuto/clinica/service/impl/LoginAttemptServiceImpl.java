package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.LoginAttempt;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.repository.LoginAttemptRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.AuditoriaService;
import com.uniminuto.clinica.service.LoginAttemptService;
import com.uniminuto.clinica.service.SistemaConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de intentos de login.
 * 
 * @author lmora
 */
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private SistemaConfigService sistemaConfigService;

    // Constantes de configuración
    private static final String CONFIG_MAX_INTENTOS = "MAX_INTENTOS_LOGIN";
    private static final String CONFIG_TIEMPO_BLOQUEO = "TIEMPO_BLOQUEO_MINUTOS";
    private static final int DEFAULT_MAX_INTENTOS = 3;
    private static final int DEFAULT_TIEMPO_BLOQUEO = 5;

    /**
     * Registra un intento de login fallido en la BD.
     */
    @Override
    @Transactional
    public void registrarIntentoFallido(String username, String ipAddress, String mensaje) {
        LoginAttempt intento = new LoginAttempt();
        intento.setUsername(username);
        intento.setIpAddress(ipAddress);
        intento.setExitoso(false);
        intento.setMensaje(mensaje);
        intento.setFechaHora(LocalDateTime.now());

        loginAttemptRepository.save(intento);

        System.out.println(String.format(
                "[LOGIN_FALLIDO] Usuario: %s | IP: %s | Mensaje: %s",
                username, ipAddress, mensaje));
    }

    /**
     * Registra un intento de login exitoso en la BD.
     */
    @Override
    @Transactional
    public void registrarIntentoExitoso(String username, String ipAddress) {
        LoginAttempt intento = new LoginAttempt();
        intento.setUsername(username);
        intento.setIpAddress(ipAddress);
        intento.setExitoso(true);
        intento.setMensaje("Login exitoso");
        intento.setFechaHora(LocalDateTime.now());

        loginAttemptRepository.save(intento);

        System.out.println(String.format(
                "[LOGIN_EXITOSO] Usuario: %s | IP: %s",
                username, ipAddress));
    }

    /**
     * Verifica si el usuario está bloqueado y actualiza su estado si el bloqueo
     * expiró.
     */
    @Override
    @Transactional
    public boolean verificarYActualizarBloqueo(Usuario usuario) {
        if (usuario.getBloqueadoHasta() == null) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();

        // Si el bloqueo ya expiró, resetear el usuario
        if (ahora.isAfter(usuario.getBloqueadoHasta())) {
            usuario.setBloqueadoHasta(null);
            usuario.setIntentosFallidos(0);
            usuario.setFechaUltimoIntento(null);
            usuarioRepository.save(usuario);

            System.out.println(String.format(
                    "[BLOQUEO_EXPIRADO] Usuario %s desbloqueado automáticamente",
                    usuario.getUsername()));

            return false;
        }

        // El usuario sigue bloqueado
        return true;
    }

    /**
     * Incrementa los intentos fallidos y bloquea si alcanza el máximo.
     */
    @Override
    @Transactional
    public void incrementarIntentosFallidos(Usuario usuario) {
        int maxIntentos = sistemaConfigService.obtenerValorInt(
                CONFIG_MAX_INTENTOS, DEFAULT_MAX_INTENTOS);
        int tiempoBloqueoMinutos = sistemaConfigService.obtenerValorInt(
                CONFIG_TIEMPO_BLOQUEO, DEFAULT_TIEMPO_BLOQUEO);

        // Incrementar contador
        int intentosActuales = (usuario.getIntentosFallidos() != null)
                ? usuario.getIntentosFallidos()
                : 0;
        intentosActuales++;

        usuario.setIntentosFallidos(intentosActuales);
        usuario.setFechaUltimoIntento(LocalDateTime.now());

        // Si alcanzó el máximo de intentos, bloquear
        if (intentosActuales >= maxIntentos) {
            LocalDateTime bloqueadoHasta = LocalDateTime.now().plusMinutes(tiempoBloqueoMinutos);
            usuario.setBloqueadoHasta(bloqueadoHasta);

            System.out.println(String.format(
                    "[USUARIO_BLOQUEADO] Usuario: %s | Intentos: %d | Bloqueado hasta: %s",
                    usuario.getUsername(), intentosActuales, bloqueadoHasta));

            // Registrar en auditoría
            auditoriaService.registrarEvento(
                    usuario.getUsername(),
                    "USUARIO_BLOQUEADO",
                    String.format("Usuario bloqueado por %d minutos después de %d intentos fallidos",
                            tiempoBloqueoMinutos, maxIntentos),
                    null,
                    false);
        } else {
            System.out.println(String.format(
                    "[INTENTO_FALLIDO] Usuario: %s | Intentos: %d/%d",
                    usuario.getUsername(), intentosActuales, maxIntentos));
        }

        usuarioRepository.save(usuario);
    }

    /**
     * Resetea el contador de intentos fallidos después de un login exitoso.
     */
    @Override
    @Transactional
    public void resetearIntentos(Usuario usuario) {
        if (usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0) {
            System.out.println(String.format(
                    "[RESET_INTENTOS] Usuario: %s tenía %d intentos fallidos. Reseteando...",
                    usuario.getUsername(), usuario.getIntentosFallidos()));
        }

        usuario.setIntentosFallidos(0);
        usuario.setFechaUltimoIntento(null);
        usuario.setBloqueadoHasta(null);
        usuarioRepository.save(usuario);
    }
}