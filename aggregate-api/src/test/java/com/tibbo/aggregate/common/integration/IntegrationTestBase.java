package com.tibbo.aggregate.common.integration;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Базовый класс для интеграционных тестов.
 * 
 * <p>Этот класс предоставляет общую инфраструктуру для интеграционных тестов,
 * включая подключение к AggreGate серверу и управление соединением.</p>
 * 
 * <p>Использование:
 * <pre>
 * public class MyIntegrationTest extends IntegrationTestBase {
 *     {@literal @}Test
 *     public void testSomething() throws Exception {
 *         Context context = getContextManager().getRoot();
 *         // Ваш тест
 *     }
 * }
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.1
 */
public abstract class IntegrationTestBase {

    /** Контроллер удаленного сервера */
    protected RemoteServerController controller;
    
    /** Менеджер контекстов */
    protected ContextManager contextManager;
    
    /** Хост сервера (можно переопределить через системное свойство) */
    protected String serverHost = System.getProperty("aggregate.test.host", "localhost");
    
    /** Порт сервера (можно переопределить через системное свойство) */
    protected int serverPort = Integer.parseInt(
        System.getProperty("aggregate.test.port", String.valueOf(RemoteServer.DEFAULT_PORT))
    );
    
    /** Имя пользователя (можно переопределить через системное свойство) */
    protected String username = System.getProperty("aggregate.test.username", "admin");
    
    /** Пароль (можно переопределить через системное свойство) */
    protected String password = System.getProperty("aggregate.test.password", "admin");
    
    /**
     * Настройка перед каждым тестом.
     * 
     * <p>Инициализирует подключение к серверу и менеджер контекстов.
     * Переопределите этот метод, если нужна дополнительная настройка.</p>
     * 
     * @throws Exception если произошла ошибка при настройке
     */
    @BeforeEach
    public void setUp() throws Exception {
        // Инициализация логирования, если еще не инициализировано
        try {
            Log.start();
        } catch (Exception e) {
            // Логирование уже инициализировано, игнорируем
        }
        
        // Создание подключения к серверу
        RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
        controller = new RemoteServerController(server, true);
        
        // Подключение
        controller.connect();
        controller.login();
        
        // Получение менеджера контекстов
        contextManager = controller.getContextManager();
    }
    
    /**
     * Очистка после каждого теста.
     * 
     * <p>Отключается от сервера. Переопределите этот метод,
     * если нужна дополнительная очистка.</p>
     * 
     * @throws Exception если произошла ошибка при очистке
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (controller != null) {
            try {
                controller.disconnect();
            } catch (Exception e) {
                // Игнорируем ошибки при отключении
            }
            controller = null;
        }
        contextManager = null;
    }
    
    /**
     * Получить менеджер контекстов.
     * 
     * @return менеджер контекстов
     */
    protected ContextManager getContextManager() {
        return contextManager;
    }
    
    /**
     * Получить контроллер сервера.
     * 
     * @return контроллер сервера
     */
    protected RemoteServerController getController() {
        return controller;
    }
    
    /**
     * Получить контекст по пути.
     * 
     * @param path путь к контексту
     * @return контекст или null, если не найден
     */
    protected Context getContext(String path) {
        if (contextManager == null) {
            return null;
        }
        return contextManager.get(path);
    }
    
    /**
     * Проверить, подключен ли тест к серверу.
     * 
     * @return true, если подключен
     */
    protected boolean isConnected() {
        return controller != null && controller.isConnected();
    }
    
    /**
     * Переподключиться к серверу.
     * 
     * @throws Exception если произошла ошибка при переподключении
     */
    protected void reconnect() throws Exception {
        if (controller != null) {
            controller.disconnect();
        }
        
        RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
        controller = new RemoteServerController(server, true);
        controller.connect();
        controller.login();
        contextManager = controller.getContextManager();
    }
}

