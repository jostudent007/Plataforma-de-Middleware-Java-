package com.projeto2.middleware.model;

import com.projeto2.middleware.enums.HttpMethod;
import java.util.Map;

/**
 * Representa uma requisição de forma genérica, abstraindo os detalhes do protocolo (TCP ou UDP).
 * Funciona como um "Data Transfer Object" (DTO) interno para o middleware.
 *
 * @param method O método HTTP da requisição (GET, POST, etc.).
 * @param path O caminho da rota (ex: "/soma").
 * @param params Um mapa com os parâmetros da requisição (ex: {"a"="5", "b"="10"}).
 */
public record MiddlewareRequest(
        HttpMethod method,
        String path,
        Map<String, String> params
) {}