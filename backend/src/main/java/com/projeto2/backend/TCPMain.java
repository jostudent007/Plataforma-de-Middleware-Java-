package com.projeto2.backend;

import com.projeto2.backend.controllers.CalculadoraController;
import com.projeto2.middleware.MiddlewareFramework;

/**
 * Ponto de entrada para iniciar a aplicação usando o transporte TCP.
 */
public class TCPMain {
    public static void main(String[] args) {
        System.out.println("Iniciando a aplicacao backend sobre TCP...");
        MiddlewareFramework framework = new MiddlewareFramework();
        try {
            framework.addController(CalculadoraController.class);
            framework.start(8080, "tcp"); // Inicia com o protocolo TCP.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}