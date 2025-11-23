package com.projeto2.middleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de nível de parâmetro para mapear um parâmetro da URL de requisição
 * ao argumento de um método.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER) // Só pode ser usada em parâmetros de métodos.
public @interface Param {
    /**
     * O nome do parâmetro na URL (ex: "a" em ?a=5).
     */
    String name();
}