package examples;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример обработки ошибок при работе с AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Правильную обработку исключений</li>
 *   <li>Обработку ошибок подключения</li>
 *   <li>Обработку ошибок аутентификации</li>
 *   <li>Обработку ошибок операций</li>
 *   <li>Гарантированное освобождение ресурсов</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.1
 */
public class ErrorHandlingExample {
    
    /**
     * Точка входа в приложение.
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        Log.start();
        
        RemoteServerController controller = null;
        
        try {
            // Настройка параметров подключения
            String serverHost = "localhost";
            int serverPort = RemoteServer.DEFAULT_PORT;
            String username = "admin";
            String password = "admin";
            
            // Создание подключения
            RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
            controller = new RemoteServerController(server, true);
            
            System.out.println("Попытка подключения к серверу...");
            
            try {
                controller.connect();
                System.out.println("Подключение установлено!");
            } catch (Exception e) {
                System.err.println("Ошибка подключения: " + e.getMessage());
                System.err.println("Проверьте:");
                System.err.println("  - Запущен ли AggreGate сервер");
                System.err.println("  - Правильность адреса и порта");
                System.err.println("  - Доступность сети");
                return; // Выход, так как без подключения дальше работать нельзя
            }
            
            try {
                controller.login();
                System.out.println("Аутентификация успешна!");
            } catch (AggreGateException e) {
                System.err.println("Ошибка аутентификации: " + e.getMessage());
                System.err.println("Код ошибки: " + e.getCode());
                System.err.println("Проверьте правильность имени пользователя и пароля");
                return; // Выход, так как без аутентификации дальше работать нельзя
            } catch (Exception e) {
                System.err.println("Неожиданная ошибка при аутентификации: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // Выполнение операций с обработкой ошибок
            try {
                // Здесь выполняются операции с сервером
                System.out.println("Выполнение операций...");
                
                // Пример: попытка получить несуществующий контекст
                try {
                    // controller.getContextManager().get("nonexistent.context");
                    System.out.println("Операции выполнены успешно");
                } catch (Exception e) {
                    System.err.println("Ошибка при выполнении операции: " + e.getMessage());
                    // Продолжаем работу, так как это не критическая ошибка
                }
                
            } catch (Exception e) {
                System.err.println("Критическая ошибка при выполнении операций: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("Общая ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Гарантированное освобождение ресурсов
            if (controller != null) {
                try {
                    if (controller.isConnected()) {
                        controller.disconnect();
                        System.out.println("Отключено от сервера.");
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка при отключении (игнорируется): " + e.getMessage());
                    // Игнорируем ошибки при отключении, так как мы уже в finally
                }
            }
        }
    }
    
    /**
     * Пример обработки специфических ошибок AggreGate.
     * 
     * @param e исключение AggreGate
     */
    private static void handleAggreGateException(AggreGateException e) {
        System.err.println("AggreGate ошибка:");
        System.err.println("  Сообщение: " + e.getMessage());
        System.err.println("  Код: " + e.getCode());
        
        // Обработка специфических кодов ошибок
        if (e.getCode() != null) {
            switch (e.getCode()) {
                case "AUTH_FAILED":
                    System.err.println("  Действие: Проверьте учетные данные");
                    break;
                case "CONTEXT_NOT_FOUND":
                    System.err.println("  Действие: Проверьте путь к контексту");
                    break;
                case "PERMISSION_DENIED":
                    System.err.println("  Действие: Проверьте права доступа");
                    break;
                default:
                    System.err.println("  Действие: Обратитесь к документации");
            }
        }
    }
}

