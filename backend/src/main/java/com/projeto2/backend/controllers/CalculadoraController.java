package com.projeto2.backend.controllers;

import com.projeto2.middleware.annotations.Controller;
import com.projeto2.middleware.annotations.Param;
import com.projeto2.middleware.annotations.RequestMapping;
import com.projeto2.middleware.enums.HttpMethod;
import com.projeto2.backend.interceptors.LogInterceptor;
import com.projeto2.middleware.annotations.InterceptAfter;
import com.projeto2.middleware.annotations.InterceptBefore;

/**
 * Classe que representa nossa "aplicação".
 * Seus métodos serão expostos como endpoints HTTP pelo middleware.
 */
@Controller // 1. Marca a classe como um Controller.
public class CalculadoraController {

    /**
     * Este método será exposto na rota GET /soma.
     * A plataforma de middleware deverá ser capaz de extrair os parâmetros 'a' e 'b'
     * da URL, convertê-los para int e chamar este método.
     */
    @RequestMapping(path = "/soma", method = HttpMethod.GET) // 2. Mapeia o método para uma rota.
    @InterceptBefore({LogInterceptor.class})
    @InterceptAfter({LogInterceptor.class})
    public int soma(@Param(name = "a") int num1, @Param(name = "b") int num2) {
        System.out.println("[CalculadoraController] Executando o metodo 'soma'...");
        return num1 + num2;
    }
    /**
     * Um método POST que ecoa uma mensagem.
     */
    @RequestMapping(path = "/echo", method = HttpMethod.POST)
    public String echo(@Param(name = "mensagem") String msg) {
        return "Você disse: " + msg;
    }
}