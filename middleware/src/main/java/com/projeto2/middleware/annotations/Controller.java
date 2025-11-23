package com.projeto2.middleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação de nível de classe para marcar uma classe como um "Controller".
 * O middleware irá escanear classes com esta anotação para encontrar métodos remotos.
 */
@Retention(RetentionPolicy.RUNTIME) // Garante que a anotação esteja disponível em tempo de execução para o Reflection.
@Target(ElementType.TYPE)           // Especifica que esta anotação só pode ser usada em classes.
public @interface Controller {
}