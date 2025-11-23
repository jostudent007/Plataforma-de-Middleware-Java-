package com.projeto2.middleware.model;

/**
 * Representa uma resposta de forma genérica.
 * A camada de transporte saberá como converter este objeto em uma resposta HTTP (TCP)
 * ou em um pacote de dados (UDP).
 *
 * @param statusCode O código de status da resposta (ex: 200 para sucesso, 404 para não encontrado).
 * @param body O corpo da resposta como uma String.
 */
public record MiddlewareResponse(
        int statusCode,
        String body
) {}