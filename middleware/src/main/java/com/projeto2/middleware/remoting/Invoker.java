package com.projeto2.middleware.remoting;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Implementação do padrão Invoker.
 * Este objeto encapsula todas as informações necessárias para invocar um método
 * de um objeto remoto (nosso Controller).
 */
public class Invoker {

    // A classe do Controller onde o método está definido (CalculadoraController.class).
    private final Class<?> controllerClass;

    // O objeto Method do Java Reflection, que é uma referência direta ao método (soma ou echo).
    private final Method method;

    // Uma lista com informações sobre cada parâmetro do método.
    private final List<ParameterInfo> parameters;

    /**
     * Construtor do Invoker.
     * @param controllerClass A classe do controller.
     * @param method A referência do método.
     * @param parameters A lista de informações dos parâmetros.
     */
    public Invoker(Class<?> controllerClass, Method method, List<ParameterInfo> parameters) {
        this.controllerClass = controllerClass;
        this.method = method;
        this.parameters = parameters;
    }
    // Getters para que outras partes do sistema possam acessar essas informações.
    public Class<?> getControllerClass() {
        return controllerClass;
    }
    public Method getMethod() {
        return method;
    }
    public List<ParameterInfo> getParameters() {
        return parameters;
    }
    /**
     * Um 'Record' para guardar informações sobre um único parâmetro de método.
     * @param name O nome do parâmetro definido na anotação @Param (ex: "a").
     * @param type O tipo do parâmetro (ex: int.class).
     */
    public record ParameterInfo(String name, Class<?> type) {
    }
}
