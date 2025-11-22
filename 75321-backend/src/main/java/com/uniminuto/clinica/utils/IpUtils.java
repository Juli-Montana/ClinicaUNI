package com.uniminuto.clinica.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Utilidades para obtener la dirección IP real del cliente.
 * Considera proxies y load balancers.
 * 
 * @author lmora
 */
public class IpUtils {

    /**
     * Obtiene la dirección IP real del cliente.
     * Considera headers de proxies y load balancers.
     * 
     * @param request HttpServletRequest
     * @return Dirección IP del cliente
     */
    public static String obtenerIpCliente(HttpServletRequest request) {
        if (request == null) {
            return "UNKNOWN";
        }

        // Intentar obtener IP de headers comunes de proxies
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Si hay múltiples IPs (por múltiples proxies), tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        // Si es localhost en IPv6, convertir a IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip != null ? ip : "UNKNOWN";
    }

    /**
     * Valida si una IP es válida (IPv4 o IPv6).
     * 
     * @param ip Dirección IP a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean esIpValida(String ip) {
        if (ip == null || ip.isEmpty() || "UNKNOWN".equals(ip)) {
            return false;
        }

        // Validar IPv4
        String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        // Validar IPv6 (simplificado)
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
    }
}