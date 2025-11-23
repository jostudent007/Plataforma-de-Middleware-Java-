package com.projeto2.middleware.remoting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação do padrão Lifecycle Manager.
 * Responsável por gerenciar o ciclo de vida dos objetos Controller.
 * Nesta versão, implementei o padrão "Static Instance" (Singleton por classe).
 */
public class LifecycleManager {
    private static final LifecycleManager INSTANCE = new LifecycleManager();

    // Cache para armazenar as instâncias únicas de cada controller.
    private final Map<Class<?>, Object> controllers = new ConcurrentHashMap<>();

    private LifecycleManager() {}

    public static LifecycleManager getInstance() {
        return INSTANCE;
    }
    /**
     * Obtém uma instância de um controller. Se já existir no cache, retorna.
     * Senão, cria uma nova, armazena no cache e retorna. (Lazy Acquisition)
     * @param controllerClass A classe do controller a ser instanciada.
     * @return Uma instância do controller.
     */
    public Object getInstance(Class<?> controllerClass) {
        // computeIfAbsent garante que a criação seja atômica e segura em ambiente multi-thread.
        return controllers.computeIfAbsent(controllerClass, clazz -> {
            try {
                System.out.println("[LifecycleManager] Criando nova instancia para: " + clazz.getName());
                // Usa Reflection para criar uma nova instância da classe.
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Falha ao criar instancia do controller: " + clazz.getName(), e);
            }
        });
    }
}