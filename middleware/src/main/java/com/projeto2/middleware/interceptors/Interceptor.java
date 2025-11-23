package com.projeto2.middleware.interceptors;

/**
 * Interface que define o contrato para um Interceptor.
 * Cada interceptor pode executar lógicas antes e depois da invocação do método principal.
 */
public interface Interceptor {

    /**
     * Método executado ANTES da invocação do método do controller.
     */
    void before();

    /**
     * Método executado DEPOIS da invocação do método do controller.
     */
    void after();
}