package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AutenticarApi;
import com.uniminuto.clinica.model.AutenticatorRs;
import com.uniminuto.clinica.model.AuthenticatorRq;
import com.uniminuto.clinica.service.AutenticarService;
import com.uniminuto.clinica.utils.IpUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador de autenticación con captura de IP.
 * 
 * @author lmora
 */
@RestController
public class AutenticarApiController implements AutenticarApi {

    @Autowired
    private AutenticarService autenticarService;

    @Autowired
    private HttpServletRequest request;

    /**
     * Endpoint de autenticación.
     * Captura la IP del cliente y la envía al servicio.
     */
    @Override
    public ResponseEntity<AutenticatorRs> autenticar(AuthenticatorRq authenticatorRq)
            throws BadRequestException {

        // Obtener la IP del cliente
        String ipAddress = IpUtils.obtenerIpCliente(request);

        System.out.println(String.format(
                "[AUTH_REQUEST] Usuario: %s | IP: %s",
                authenticatorRq.getUsername(), ipAddress));

        // Llamar al servicio con la IP
        AutenticatorRs response = autenticarService.autenticar(authenticatorRq, ipAddress);

        return ResponseEntity.ok(response);
    }
}