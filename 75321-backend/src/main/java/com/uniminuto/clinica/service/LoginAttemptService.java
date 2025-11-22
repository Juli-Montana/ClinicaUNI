package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Usuario;

/**
 * Servicio para gestionar intentos de login.
 * 
 * @author lmora
 */
public interface LoginAttemptService {

    /**
     * Registra un intento de login fallido.
     * 
     * @param username  Usuario que intentó autenticarse
     * @param ipAddress IP desde donde se intentó
     * @param mensaje   Mensaje descriptivo del fallo
     */
    void registrarIntentoFallido(String username, String ipAddress, String mensaje);

    /**
     * Registra un intento de login exitoso.
     * 
     * @param username  Usuario que se autenticó
     * @param ipAddress IP desde donde se autenticó
     */
    void registrarIntentoExitoso(String username, String ipAddress);

    /**
     * Verifica si un usuario está bloqueado y actualiza su estado si es necesario.
     * 
     * @param usuario Usuario a verificar
     * @return true si está bloqueado, false si puede intentar login
     */
    boolean verificarYActualizarBloqueo(Usuario usuario);

    /**
     * Incrementa el contador de intentos fallidos de un usuario.
     * Si alcanza el máximo, lo bloquea temporalmente.
     * 
     * @param usuario Usuario a actualizar
     */
    void incrementarIntentosFallidos(Usuario usuario);

    /**
     * Resetea el contador de intentos fallidos de un usuario.
     * 
     * @param usuario Usuario a resetear
     */
    void resetearIntentos(Usuario usuario);
}