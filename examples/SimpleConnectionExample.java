package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Простейший пример подключения к AggreGate серверу.
 * 
 * <p>Этот пример демонстрирует базовое подключение к серверу,
 * аутентификацию и отключение.</p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные адрес, порт, имя пользователя и пароль</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.0
 */
public class SimpleConnectionExample {
    
    /**
     * Точка входа в приложение.
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация системы логирования
        // Это необходимо для корректной работы SDK
        Log.start();
        
        // Объявляем controller как null, чтобы гарантировать освобождение ресурсов в finally
        RemoteServerController controller = null;
        
        try {
            // ============================================
            // ШАГ 1: Настройка параметров подключения
            // ============================================
            // ВАЖНО: Замените эти значения на реальные параметры вашего сервера
            String serverHost = "localhost";
            // Порт подключения:
            // - DEFAULT_PORT (6460) - безопасное SSL соединение
            // - DEFAULT_NON_SECURE_PORT (6461) - небезопасное соединение (без SSL)
            // SDK автоматически определяет тип соединения по номеру порта
            int serverPort = RemoteServer.DEFAULT_PORT; // По умолчанию: 6460 (SSL)
            String username = "admin";
            String password = "admin";
            
            // Проверка параметров подключения (edge case)
            if (serverHost == null || serverHost.trim().isEmpty()) {
                System.err.println("ОШИБКА: Адрес сервера не может быть пустым!");
                return;
            }
            if (username == null || username.trim().isEmpty()) {
                System.err.println("ОШИБКА: Имя пользователя не может быть пустым!");
                return;
            }
            
            // ============================================
            // ШАГ 2: Создание объекта сервера
            // ============================================
            // RemoteServer представляет удаленный AggreGate сервер
            // Параметры: хост, порт, имя пользователя, пароль
            RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
            
            // ============================================
            // ШАГ 3: Создание контроллера сервера
            // ============================================
            // RemoteServerController управляет соединением с сервером
            // Второй параметр (true) включает автоматическое переподключение при разрыве связи
            // Это полезно для долгоживущих приложений
            controller = new RemoteServerController(server, true);
            
            // ============================================
            // ШАГ 4: Подключение к серверу
            // ============================================
            System.out.println("Подключение к серверу " + serverHost + ":" + serverPort + "...");
            
            // Обработка ошибок подключения
            try {
                controller.connect();
                System.out.println("✓ Подключение установлено!");
            } catch (java.net.ConnectException e) {
                System.err.println("✗ ОШИБКА: Не удалось подключиться к серверу!");
                System.err.println("  Причина: " + e.getMessage());
                System.err.println("  Проверьте:");
                System.err.println("    - Запущен ли AggreGate сервер");
                System.err.println("    - Правильность адреса: " + serverHost);
                System.err.println("    - Правильность порта: " + serverPort);
                System.err.println("    - Доступность сети и файрвола");
                return; // Выход, так как без подключения дальше работать нельзя
            } catch (java.net.SocketTimeoutException e) {
                System.err.println("✗ ОШИБКА: Таймаут при подключении!");
                System.err.println("  Сервер не отвечает в течение установленного времени");
                return;
            } catch (Exception e) {
                System.err.println("✗ ОШИБКА подключения: " + e.getClass().getSimpleName());
                System.err.println("  Сообщение: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // ============================================
            // ШАГ 5: Аутентификация
            // ============================================
            System.out.println("Аутентификация пользователя '" + username + "'...");
            
            try {
                controller.login();
                System.out.println("✓ Аутентификация успешна!");
            } catch (com.tibbo.aggregate.common.AggreGateException e) {
                System.err.println("✗ ОШИБКА аутентификации!");
                System.err.println("  Сообщение: " + e.getMessage());
                if (e.getCode() != null) {
                    System.err.println("  Код ошибки: " + e.getCode());
                }
                System.err.println("  Проверьте правильность имени пользователя и пароля");
                return; // Выход, так как без аутентификации дальше работать нельзя
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при аутентификации: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // ============================================
            // ШАГ 6: Работа с сервером
            // ============================================
            // Теперь соединение установлено и аутентификация пройдена
            // Можно выполнять операции с сервером:
            // - Получать контексты: controller.getContextManager().get("path")
            // - Читать переменные: context.getVariable("variableName")
            // - Вызывать функции: context.callFunction("functionName", params)
            // - Подписываться на события: context.addEventListener("eventName", listener)
            
            System.out.println("✓ Соединение активно. Можно выполнять операции.");
            System.out.println("  Пример получения менеджера контекстов:");
            System.out.println("    ContextManager cm = controller.getContextManager();");
            System.out.println("    Context root = cm.getRoot();");
            
            // Небольшая пауза для демонстрации (в реальном приложении здесь будет работа)
            Thread.sleep(1000);
            
        } catch (InterruptedException e) {
            // Обработка прерывания потока
            Thread.currentThread().interrupt();
            System.err.println("⚠ Прервано ожидание: " + e.getMessage());
        } catch (Exception e) {
            // Обработка любых других неожиданных ошибок
            System.err.println("✗ Неожиданная ошибка: " + e.getClass().getSimpleName());
            System.err.println("  Сообщение: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ============================================
            // ШАГ 7: Гарантированное освобождение ресурсов
            // ============================================
            // ВАЖНО: Всегда отключайтесь от сервера, даже при ошибках
            // Это освобождает сетевые ресурсы и соединения
            if (controller != null) {
                try {
                    // Проверяем, что соединение действительно установлено
                    if (controller.isConnected()) {
                        System.out.println("\nОтключение от сервера...");
                        controller.disconnect();
                        System.out.println("✓ Отключено успешно!");
                    } else {
                        System.out.println("\nСоединение уже закрыто.");
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки при отключении, так как мы уже в finally
                    // и главное - освободить ресурсы
                    System.err.println("⚠ Предупреждение при отключении (игнорируется): " + e.getMessage());
                }
            }
        }
    }
}

