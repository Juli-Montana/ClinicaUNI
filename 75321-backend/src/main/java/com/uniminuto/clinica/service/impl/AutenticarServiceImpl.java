package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.model.AutenticatorRs;
import com.uniminuto.clinica.model.AuthenticatorRq;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.AutenticarService;
import com.uniminuto.clinica.service.CifrarService;
import com.uniminuto.clinica.service.AuditoriaService;
import com.uniminuto.clinica.service.LoginAttemptService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import com.uniminuto.clinica.entity.Session;
import com.uniminuto.clinica.repository.SessionRepository;
import com.uniminuto.clinica.security.JwtUtil;

import javax.transaction.Transactional;

/**
 * Servicio de autenticación con control de intentos fallidos y bloqueo
 * temporal.
 * 
 * @author lmora
 */
@Service
public class AutenticarServiceImpl implements AutenticarService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CifrarService cifrarService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    /**
     * Autentica un usuario con control de intentos fallidos y bloqueo temporal.
     * 
     * @param request   Credenciales del usuario
     * @param ipAddress Dirección IP del cliente
     * @return Token JWT si la autenticación es exitosa
     * @throws BadRequestException Si las credenciales son inválidas o el usuario
     *                             está bloqueado
     */
    @Override
    @Transactional
    public AutenticatorRs autenticar(AuthenticatorRq request, String ipAddress)
            throws BadRequestException {

        String username = request.getUsername().toLowerCase().trim();

        // PASO 1: Buscar el usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            // Usuario no existe - Registrar intento fallido
            loginAttemptService.registrarIntentoFallido(
                    username,
                    ipAddress,
                    "Usuario no encontrado");

            auditoriaService.registrarEvento(
                    username,
                    "LOGIN_FALLIDO",
                    "Intento de login con usuario inexistente",
                    ipAddress,
                    false);

            throw new BadRequestException("Usuario o contraseña incorrectos");
        }

        Usuario usuario = usuarioOpt.get();

        // PASO 2: Verificar si el usuario está activo
        if (!usuario.isActivo()) {
            loginAttemptService.registrarIntentoFallido(
                    username,
                    ipAddress,
                    "Usuario inactivo");

            auditoriaService.registrarEvento(
                    username,
                    "LOGIN_FALLIDO",
                    "Intento de login con usuario inactivo",
                    ipAddress,
                    false);

            throw new BadRequestException("El usuario está inactivo. Contacte al administrador.");
        }

        // PASO 3: Verificar si el usuario está bloqueado
        boolean estaBloqueado = loginAttemptService.verificarYActualizarBloqueo(usuario);

        if (estaBloqueado) {
            LocalDateTime bloqueadoHasta = usuario.getBloqueadoHasta();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String horaDesbloqueo = bloqueadoHasta.format(formatter);

            loginAttemptService.registrarIntentoFallido(
                    username,
                    ipAddress,
                    "Intento de login con usuario bloqueado hasta " + horaDesbloqueo);

            auditoriaService.registrarEvento(
                    username,
                    "LOGIN_BLOQUEADO",
                    "Intento de login con usuario bloqueado temporalmente hasta " + bloqueadoHasta,
                    ipAddress,
                    false);

            throw new BadRequestException(
                    "Usuario bloqueado temporalmente por múltiples intentos fallidos. " +
                            "Intente nuevamente después de las " + horaDesbloqueo);
        }

        // PASO 4: Validar contraseña
        boolean passwordOk = passwordEncoder.matches(request.getPassword(), usuario.getPassword());

        if (!passwordOk) {
            // Contraseña incorrecta - Incrementar intentos fallidos
            loginAttemptService.incrementarIntentosFallidos(usuario);

            loginAttemptService.registrarIntentoFallido(
                    username,
                    ipAddress,
                    "Contraseña incorrecta");

            auditoriaService.registrarEvento(
                    username,
                    "LOGIN_FALLIDO",
                    String.format("Contraseña incorrecta. Intentos fallidos: %d",
                            usuario.getIntentosFallidos()),
                    ipAddress,
                    false);

            throw new BadRequestException("Usuario o contraseña incorrectos");
        }

        // PASO 5: Login exitoso - Resetear intentos fallidos
        loginAttemptService.resetearIntentos(usuario);

        // Registrar intento exitoso
        loginAttemptService.registrarIntentoExitoso(username, ipAddress);

        auditoriaService.registrarEvento(
                username,
                "LOGIN_EXITOSO",
                "Usuario autenticado correctamente",
                ipAddress,
                true);

        // PASO 6: Generar token JWT
        String token = jwtUtil.generateToken(usuario);

        AutenticatorRs rta = new AutenticatorRs();
        rta.setToken(token);

        // PASO 7: Crear sesión del usuario
        crearSesionUsuario(usuario, token);

        System.out.println(String.format(
                "[AUTENTICACION_EXITOSA] Usuario: %s | IP: %s | Token generado",
                username, ipAddress));

        return rta;
    }

    /**
     * Crea y almacena la sesión del usuario autenticado.
     *
     * @param usuario Usuario autenticado
     * @param token   Token JWT generado
     */
    private void crearSesionUsuario(Usuario usuario, String token) {
        // Elimina cualquier sesión previa del usuario
        sessionRepository.deleteByUserId(usuario.getId().intValue());

        Session session = new Session();
        session.setUserId(usuario.getId().intValue());
        session.setToken(token);
        session.setFechaIniSesion(LocalDateTime.now());

        Date fechaExpiracion = jwtUtil.getExpirationDateFromToken(token);
        session.setFechaExpiracion(fechaExpiracion.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());

        sessionRepository.save(session);
    }
}