package com.projeto2.backend.interceptors;

import com.projeto2.middleware.interceptors.Interceptor;

/**
 * Um interceptor de exemplo que apenas imprime mensagens de log no console.
 */
public class LogInterceptor implements Interceptor {

    @Override
    public void before() {
        System.out.println("[LogInterceptor] >>> Interceptando ANTES da chamada do metodo...");
    }

    @Override
    public void after() {
        System.out.println("[LogInterceptor] <<< Interceptando DEPOIS da chamada do metodo...");
    }
}