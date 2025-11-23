package com.projeto2.middleware.remoting;

import com.projeto2.middleware.enums.HttpMethod;
import com.projeto2.middleware.model.MiddlewareRequest;
import com.projeto2.middleware.model.MiddlewareResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementação da camada de transporte sobre TCP, usando o servidor HTTP nativo do Java.
 * Sua única responsabilidade é "traduzir" de uma requisição HTTP real para o modelo interno
 * e vice-versa.
 */
public class TcpTransport implements TransportStrategy {
    private final int port;

    public TcpTransport(int port) {
        this.port = port;
    }

    @Override
    public void start(Function<MiddlewareRequest, MiddlewareResponse> handler) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(this.port), 0);
        server.createContext("/", httpExchange -> {
            try (httpExchange) {
                // 1. Unmarshal do Transporte: Converte a requisição HTTP (HttpExchange)
                // num modelo genérico (O MiddlewareRequest).
                MiddlewareRequest request = this.fromHttpExchange(httpExchange);

                // 2. Executa a Lógica Principal: Chama a função 'handler' que foi passada,
                // que contém toda a lógica do middleware (lookup, invoke, etc.).
                MiddlewareResponse response = handler.apply(request);

                // 3. Marshal do Transporte: Converte a resposta genérica (MiddlewareResponse)
                // de volta para uma resposta HTTP específica.
                this.toHttpExchange(response, httpExchange);

            } catch (Exception e) {
                System.err.println("Erro critico no transporte TCP: " + e.getMessage());
            }
        });

        // Teste para correção de performance: um pool de threads fixo. Não surtiu efeito
        //server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(20));

        server.start();
        System.out.println("[TcpTransport] Servidor HTTP sobre TCP iniciado na porta " + this.port);
    }

    // ... (métodos auxiliares fromHttpExchange, toHttpExchange e parseQuery) ...
    private MiddlewareRequest fromHttpExchange(HttpExchange exchange) throws IOException {
        HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod().toUpperCase());
        String path = exchange.getRequestURI().getPath();

        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            parseQuery(query, params);
        }

        if (method == HttpMethod.POST) {
            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                parseQuery(body, params);
            }
        }
        return new MiddlewareRequest(method, path, params);
    }

    private void toHttpExchange(MiddlewareResponse response, HttpExchange exchange) throws IOException {
        byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(response.statusCode(), responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private void parseQuery(String query, Map<String, String> params) {
        if (query != null && !query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    params.put(pair[0], java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                }
            }
        }
    }
}