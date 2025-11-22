package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.SistemaConfig;
import com.uniminuto.clinica.repository.SistemaConfigRepository;
import com.uniminuto.clinica.service.SistemaConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementación del servicio de configuración del sistema.
 * 
 * @author lmora
 */
@Service
public class SistemaConfigServiceImpl implements SistemaConfigService {

    @Autowired
    private SistemaConfigRepository sistemaConfigRepository;

    @Override
    public String obtenerValor(String clave, String valorDefecto) {
        Optional<SistemaConfig> config = sistemaConfigRepository.findByClave(clave);
        return config.map(SistemaConfig::getValor).orElse(valorDefecto);
    }

    @Override
    public int obtenerValorInt(String clave, int valorDefecto) {
        try {
            String valor = obtenerValor(clave, String.valueOf(valorDefecto));
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear configuración " + clave + ": " + e.getMessage());
            return valorDefecto;
        }
    }
}