package com.projeto2.middleware.remoting;

import com.projeto2.middleware.enums.HttpMethod;
import com.projeto2.middleware.model.MiddlewareRequest;
import com.projeto2.middleware.model.MiddlewareResponse;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implementação da camada de transporte sobre UDP.
 * Simula uma requisição HTTP empacotada em um único datagrama de texto.
 */
public class UdpTransport implements TransportStrategy {
    private final int port;

    public UdpTransport(int port) {
        this.port = port;
    }

    @Override
    public void start(Function<MiddlewareRequest, MiddlewareResponse> handler) throws IOException {
        DatagramSocket socket = new DatagramSocket(this.port);
        System.out.println("[UdpTransport] Servidor HTTP sobre UDP iniciado na porta " + this.port);

        // O servidor UDP roda em uma thread separada para não bloquear a aplicação.
        new Thread(() -> {
            byte[] buffer = new byte[8192]; // Buffer para receber os pacotes.
            while (true) {
                try {
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket); // Espera (bloqueia) até receber um pacote.

                    // 1. Unmarshal do Transporte: Converte o pacote UDP num modelo MiddlewareRequest.
                    MiddlewareRequest request = this.fromDatagramPacket(receivePacket);

                    // 2. Executa a Lógica Principal: Chama a função 'handler' do middleware.
                    MiddlewareResponse response = handler.apply(request);

                    // 3. Marshal do Transporte: Converte a MiddlewareResponse de volta para um pacote UDP.
                    this.toDatagramPacket(response, receivePacket, socket);

                } catch (Exception e) {
                    System.err.println("Erro ao processar pacote UDP: " + e.getMessage());
                }
            }
        }).start();
    }

    // "Desmonta" um pacote UDP.
    private MiddlewareRequest fromDatagramPacket(DatagramPacket packet) {
        String data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

        // Convenção de texto para a requisição UDP. Formato esperado: "GET /soma?a=5&b=10"
        String[] parts = data.split(" ");
        HttpMethod method = HttpMethod.valueOf(parts[0].toUpperCase());

        String fullPath = parts[1];
        String path = fullPath;
        String query = "";

        if (fullPath.contains("?")) {
            path = fullPath.substring(0, fullPath.indexOf('?'));
            query = fullPath.substring(fullPath.indexOf('?') + 1);
        }

        Map<String, String> params = new HashMap<>();
        if (!query.isEmpty()) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    params.put(pair[0], pair[1]);
                }
            }
        }
        return new MiddlewareRequest(method, path, params);
    }

    // "Monta" uma resposta UDP.
    private void toDatagramPacket(MiddlewareResponse response, DatagramPacket originalPacket, DatagramSocket socket) throws IOException {
        InetAddress clientAddress = originalPacket.getAddress();
        int clientPort = originalPacket.getPort();

        byte[] responseBytes = response.body().getBytes(StandardCharsets.UTF_8);

        DatagramPacket sendPacket = new DatagramPacket(responseBytes, responseBytes.length, clientAddress, clientPort);
        socket.send(sendPacket);
    }
}
