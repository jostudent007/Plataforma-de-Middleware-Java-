package com.projeto2.middleware.remoting;

import com.projeto2.middleware.model.MiddlewareRequest;
import com.projeto2.middleware.model.MiddlewareResponse;
import java.io.IOException;
import java.util.function.Function;

/**
 * A interface do Padrão Strategy que define o contrato para uma camada de transporte de rede.
 * Esta é a implementação do padrão "Protocol Plug-in".
 */
public interface TransportStrategy {
    /**
     * Inicia o servidor de transporte.
     * @param handler A função principal do middleware, que recebe um MiddlewareRequest e retorna
     * um MiddlewareResponse. Esta é a lógica de negócio que será executada para cada requisição,
     * independente do protocolo.
     */
    void start(Function<MiddlewareRequest, MiddlewareResponse> handler) throws IOException;
}