package com.uniminuto.clinica.service;

import com.uniminuto.clinica.model.AutenticatorRs;
import com.uniminuto.clinica.model.AuthenticatorRq;
import org.apache.coyote.BadRequestException;

public interface AutenticarService {

    AutenticatorRs autenticar(AuthenticatorRq request, String ipAddress) throws BadRequestException;
}
