package com.projeto2.middleware.annotations;

import com.projeto2.middleware.enums.HttpMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de nível de método para mapear um método a uma rota HTTP.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // Só pode ser usada em métodos.
public @interface RequestMapping {
    /**
     * O caminho da rota (ex: "/soma").
     */
    String path();

    /**
     * O método HTTP (GET, POST, etc.).
     */
    HttpMethod method();
}