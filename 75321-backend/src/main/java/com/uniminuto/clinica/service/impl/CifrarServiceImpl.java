package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.service.CifrarService;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CifrarServiceImpl implements CifrarService {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String encriptarPassword(String passAEncriptar) {
        return passwordEncoder.encode(passAEncriptar);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
