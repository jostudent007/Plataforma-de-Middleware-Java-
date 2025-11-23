package com.projeto2.backend;

import com.projeto2.backend.controllers.CalculadoraController;
import com.projeto2.middleware.MiddlewareFramework;

/**
 * Ponto de entrada para iniciar a aplicação usando o transporte UDP.
 */
public class UDPMain {
    public static void main(String[] args) {
        System.out.println("Iniciando a aplicacao backend sobre UDP...");
        MiddlewareFramework framework = new MiddlewareFramework();
        try {
            framework.addController(CalculadoraController.class);
            framework.start(8080, "udp"); // Inicia com o protocolo UDP.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}