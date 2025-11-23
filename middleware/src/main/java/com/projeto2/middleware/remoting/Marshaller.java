package com.projeto2.middleware.remoting;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;

/**
 * Implementação do padrão Marshaller.
 * Responsável por "desmontar" requisições HTTP (unmarshalling) em dados que o sistema entende,
 * e "montar" respostas HTTP (marshalling) a partir dos resultados da execução.
 */
public class Marshaller {

    /**
     * Extrai informações da requisição HTTP.
     * @param exchange O objeto da requisição HTTP.
     * @return Um objeto RequestData contendo a chave da rota e os parâmetros.
     */
    public RequestData unmarshalRequest(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String routeKey = method.toUpperCase() + ":" + path;

        Map<String, String> params = new HashMap<>();

        // Lógica para extrair parâmetros da URL (para GET)
        String query = exchange.getRequestURI().getQuery();
        if (query != null && !query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    params.put(pair[0], pair[1]);
                }
            }
        }
        // Se a requisição for POST, lê o corpo e trata como parâmetros.
        if ("POST".equalsIgnoreCase(method)) {
            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                // Assume um formato simples de corpo, como "chave1=valor1&chave2=valor2"
                for (String param : body.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1) {
                        // Decodifica o valor para lidar com espaços e caracteres especiais (ex: %20)
                        params.put(pair[0], java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                    }
                }
            }
        }
        return new RequestData(routeKey, params);
    }

    /**
     * Envia uma resposta de sucesso para o cliente.
     * @param result O objeto retornado pelo método invocado.
     * @param exchange O objeto da requisição HTTP para enviar a resposta.
     */
    public void marshalResponse(Object result, HttpExchange exchange) throws IOException {
        String responseBody = (result != null) ? result.toString() : "";
        byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(200, responseBytes.length); // 200 = OK

        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
    }

    /**
     * Envia uma resposta de erro para o cliente.
     * @param statusCode O código de status HTTP (ex: 404, 500).
     * @param message A mensagem de erro.
     * @param exchange O objeto da requisição HTTP.
     */
    public void marshalErrorResponse(int statusCode, String message, HttpExchange exchange) throws IOException {
        byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
    }
    /**
     * Record para agrupar os dados extraídos da requisição.
     */
    public record RequestData(String routeKey, Map<String, String> params) {
    }
}