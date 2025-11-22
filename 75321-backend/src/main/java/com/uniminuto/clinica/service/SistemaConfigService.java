package com.uniminuto.clinica.service;

/**
 * Servicio para gestionar configuraciones del sistema.
 * 
 * @author lmora
 */
public interface SistemaConfigService {

    /**
     * Obtiene un valor de configuración.
     * 
     * @param clave        Clave de configuración
     * @param valorDefecto Valor por defecto si no existe
     * @return Valor de configuración
     */
    String obtenerValor(String clave, String valorDefecto);

    /**
     * Obtiene un valor de configuración como entero.
     * 
     * @param clave        Clave de configuración
     * @param valorDefecto Valor por defecto si no existe
     * @return Valor de configuración
     */
    int obtenerValorInt(String clave, int valorDefecto);
}