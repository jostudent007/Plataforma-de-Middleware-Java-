package com.projeto2.middleware;

import com.projeto2.middleware.annotations.Controller;
import com.projeto2.middleware.annotations.InterceptAfter;
import com.projeto2.middleware.annotations.InterceptBefore;
import com.projeto2.middleware.annotations.Param;
import com.projeto2.middleware.annotations.RequestMapping;

import com.projeto2.middleware.interceptors.Interceptor;

import com.projeto2.middleware.model.MiddlewareRequest;
import com.projeto2.middleware.model.MiddlewareResponse;

import com.projeto2.middleware.remoting.Invoker;
import com.projeto2.middleware.remoting.InvokerRegistry;
import com.projeto2.middleware.remoting.LifecycleManager;
import com.projeto2.middleware.remoting.TcpTransport;
import com.projeto2.middleware.remoting.TransportStrategy;
import com.projeto2.middleware.remoting.UdpTransport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * A classe de fachada e "maestro" do framework.
 * Coordena todos os outros componentes para escanear controllers e processar requisições.
 */
public class MiddlewareFramework {
    // Referências para os componentes Singleton do middleware.
    private final InvokerRegistry invokerRegistry = InvokerRegistry.getInstance();
    private final LifecycleManager lifecycleManager = LifecycleManager.getInstance();

    /**
     * Adiciona um controller ao framework. Este método usa Reflection para escanear
     * a classe em busca de métodos anotados e os registra no InvokerRegistry.
     * @param controllerClass A classe do controller (CalculadoraController.class).
     */
    public void addController(Class<?> controllerClass) {
        // Garante que a classe passada é de fato um controller.
        if (!controllerClass.isAnnotationPresent(Controller.class)) {
            System.err.println("Classe " + controllerClass.getName() + " nao e um @Controller. Ignorando.");
            return;
        }

        System.out.println("Escaneando controller: " + controllerClass.getName());

        // Itera sobre todos os métodos da classe.
        for (Method method : controllerClass.getDeclaredMethods()) {
            // Verifica se o método está anotado para ser uma rota.
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                // Cria a chave da rota, ex: "GET:/soma".
                String routeKey = mapping.method().name() + ":" + mapping.path();

                // Extrai as informações dos parâmetros do método.
                List<Invoker.ParameterInfo> parameterInfos = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    if (parameter.isAnnotationPresent(Param.class)) {
                        Param paramAnnotation = parameter.getAnnotation(Param.class);
                        parameterInfos.add(new Invoker.ParameterInfo(paramAnnotation.name(), parameter.getType()));
                    }
                }
                // Cria um Invoker com todas as informações coletadas.
                Invoker invoker = new Invoker(controllerClass, method, parameterInfos);
                // Registra o Invoker no "catálogo" de rotas.
                invokerRegistry.registerInvoker(routeKey, invoker);
            }
        }
    }
    /**
     * Inicia o framework com uma estratégia de transporte específica.
     * @param port A porta em que o servidor irá operar.
     * @param protocol O protocolo a ser usado ("tcp" ou "udp").
     */
    public void start(int port, String protocol) throws IOException {
        System.out.println("Iniciando o MiddlewareFramework com transporte " + protocol.toUpperCase());

        // Padrão Strategy (Protocol Plug-in): Escolhe a implementação de transporte com base no parâmetro.
        TransportStrategy transport;
        if ("udp".equalsIgnoreCase(protocol)) {
            transport = new UdpTransport(port);
        } else {
            // TCP é o transporte padrão.
            transport = new TcpTransport(port);
        }

        // Esta função lambda (handler) contém a LÓGICA CENTRAL do middleware.
        // Ela é passada para a camada de transporte, que irá executá-la para cada requisição.
        transport.start(request -> {
            try {
                // 1. Lookup: Usa a chave da rota (ex: "GET:/soma") para encontrar o Invoker.
                String routeKey = request.method().name() + ":" + request.path();
                Invoker invoker = invokerRegistry.getInvoker(routeKey);

                if (invoker == null) {
                    return new MiddlewareResponse(404, "Rota nao encontrada: " + routeKey);
                }

                // 2. Interceptors (Before): Executa interceptors de pré-processamento.
                executeInterceptorsBefore(invoker.getMethod());

                // 3. Preparação de Argumentos: Converte os parâmetros (String) da requisição
                // para os tipos corretos (int, etc.) esperados pelo método Java.
                Object[] methodArgs = new Object[invoker.getParameters().size()];
                for (int i = 0; i < invoker.getParameters().size(); i++) {
                    Invoker.ParameterInfo paramInfo = invoker.getParameters().get(i);
                    String paramValueStr = request.params().get(paramInfo.name());

                    if (paramValueStr == null) {
                        return new MiddlewareResponse(400, "Parametro obrigatorio nao encontrado: " + paramInfo.name());
                    }
                    if (paramInfo.type() == int.class) {
                        methodArgs[i] = Integer.parseInt(paramValueStr);
                    } else if (paramInfo.type() == String.class) {
                        methodArgs[i] = paramValueStr;
                    }
                }

                // 4. Lifecycle Manager: Obtém uma instância do controller para invocar o método.
                Object controllerInstance = lifecycleManager.getInstance(invoker.getControllerClass());

                // 5. Invoke: Usa Reflection para chamar o método do controller com os argumentos preparados.
                Object result = invoker.getMethod().invoke(controllerInstance, methodArgs);

                // 6. Interceptors (After): Executa interceptors de pós-processamento.
                executeInterceptorsAfter(invoker.getMethod());

                // 7. Resposta: Cria uma resposta de sucesso.
                return new MiddlewareResponse(200, result != null ? result.toString() : "");

            } catch (InvocationTargetException e) {
                // Tratamento de Erros: Se o método do controller lançar uma exceção.
                return new MiddlewareResponse(500, "Erro interno no servidor: " + e.getTargetException().getMessage());
            } catch (Exception e) {
                // Tratamento de Erros: Para outros erros do middleware (ex: parsing).
                return new MiddlewareResponse(400, "Requisicao invalida: " + e.getMessage());
            }
        });
    }
    /**
     * Método auxiliar que encontra e executa os interceptors @InterceptBefore de um método.
     * @param method O método do controller que será invocado.
     */
    private void executeInterceptorsBefore(Method method) {
        if (method.isAnnotationPresent(InterceptBefore.class)) {
            InterceptBefore annotation = method.getAnnotation(InterceptBefore.class);
            for (Class<? extends Interceptor> interceptorClass : annotation.value()) {
                try {
                    Interceptor interceptor = interceptorClass.getDeclaredConstructor().newInstance();
                    interceptor.before();
                } catch (Exception e) {
                    throw new RuntimeException("Falha ao executar interceptor (before): " + interceptorClass.getName(), e);
                }
            }
        }
    }
    /**
     * Método auxiliar que encontra e executa os interceptors @InterceptAfter de um método.
     * @param method O método do controller que foi invocado.
     */
    private void executeInterceptorsAfter(Method method) {
        if (method.isAnnotationPresent(InterceptAfter.class)) {
            InterceptAfter annotation = method.getAnnotation(InterceptAfter.class);
            for (Class<? extends Interceptor> interceptorClass : annotation.value()) {
                try {
                    Interceptor interceptor = interceptorClass.getDeclaredConstructor().newInstance();
                    interceptor.after();
                } catch (Exception e) {
                    throw new RuntimeException("Falha ao executar interceptor (after): " + interceptorClass.getName(), e);
                }
            }
        }
    }
}