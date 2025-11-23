package com.projeto2.middleware.remoting;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação do padrão Lookup (ou Invoker Registry).
 * É um catálogo Singleton que mapeia uma rota (ex: "GET:/soma") ao seu
 * respectivo Invoker.
 */
public class InvokerRegistry {
    // A instância única do Singleton.
    private static final InvokerRegistry INSTANCE = new InvokerRegistry();

    // O mapa que armazena as rotas. A chave é uma String (ex: "GET:/soma") e o valor é o Invoker.
    private final ConcurrentHashMap<String, Invoker> registry = new ConcurrentHashMap<>();

    // Construtor privado.
    private InvokerRegistry() {}

    /**
     * Retorna a instância única do registro.
     */
    public static InvokerRegistry getInstance() {
        return INSTANCE;
    }
    /**
     * Registra um novo Invoker no catálogo.
     * @param routeKey A chave da rota (ex: "GET:/soma").
     * @param invoker O Invoker associado a essa rota.
     */
    public void registerInvoker(String routeKey, Invoker invoker) {
        registry.put(routeKey, invoker);
        System.out.println("[InvokerRegistry] Rota registrada: " + routeKey);
    }
    /**
     * Procura e retorna um Invoker com base na chave da rota.
     * @param routeKey A chave da rota.
     * @return O Invoker correspondente, ou null se a rota não for encontrada.
     */
    public Invoker getInvoker(String routeKey) {
        return registry.get(routeKey);
    }
}