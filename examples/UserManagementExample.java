package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

/**
 * Пример управления пользователями в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Создание нового пользователя</li>
 *   <li>Получение контекста пользователя</li>
 *   <li>Изменение информации о пользователе</li>
 *   <li>Удаление пользователя</li>
 * </ul>
 * </p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные параметры подключения</li>
 *   <li>У пользователя есть права администратора</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class UserManagementExample {
    
    /**
     * Точка входа в приложение.
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация системы логирования
        Log.start();
        
        // Объявляем controller для гарантированного освобождения ресурсов
        RemoteServerController controller = null;
        
        try {
            // ============================================
            // ШАГ 1: Настройка и подключение к серверу
            // ============================================
            System.out.println("=== Настройка подключения ===");
            
            // Параметры подключения
            // ВАЖНО: Замените на реальные значения вашего сервера
            // ВАЖНО: Пользователь должен иметь права администратора для управления пользователями
            String serverHost = "localhost";
            int serverPort = RemoteServer.DEFAULT_PORT; // По умолчанию: 6460
            String username = "admin";
            String password = "admin";
            
            // Создание подключения
            RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
            controller = new RemoteServerController(server, true);
            
            // Подключение с обработкой ошибок
            try {
                System.out.println("Подключение к серверу " + serverHost + ":" + serverPort + "...");
                controller.connect();
                System.out.println("✓ Подключение установлено");
            } catch (Exception e) {
                System.err.println("✗ Ошибка подключения: " + e.getMessage());
                System.err.println("  Проверьте, что сервер запущен и доступен");
                return;
            }
            
            // Аутентификация с обработкой ошибок
            try {
                System.out.println("Аутентификация пользователя '" + username + "'...");
                controller.login();
                System.out.println("✓ Аутентификация успешна");
            } catch (Exception e) {
                System.err.println("✗ Ошибка аутентификации: " + e.getMessage());
                System.err.println("  Проверьте правильность имени пользователя и пароля");
                return;
            }
            
            // ============================================
            // ШАГ 2: Получение менеджера контекстов
            // ============================================
            System.out.println("\n=== Работа с пользователями ===");
            
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            Context rootContext = cm.getRoot();
            if (rootContext == null) {
                System.err.println("✗ Ошибка: Не удалось получить корневой контекст");
                return;
            }
            
            // ============================================
            // ШАГ 3: Создание нового пользователя
            // ============================================
            System.out.println("\n--- Создание нового пользователя ---");
            
            // Генерация уникального имени пользователя для теста
            // В реальном приложении используйте осмысленные имена
            String newUsername = "testUser_" + System.currentTimeMillis();
            String newPassword = "testPassword123";
            
            // Проверка параметров (edge case)
            if (newUsername == null || newUsername.trim().isEmpty()) {
                System.err.println("✗ ОШИБКА: Имя пользователя не может быть пустым!");
                return;
            }
            if (newPassword == null || newPassword.length() < 3) {
                System.err.println("✗ ОШИБКА: Пароль должен содержать минимум 3 символа!");
                return;
            }
            
            System.out.println("Создание пользователя:");
            System.out.println("  Имя: " + newUsername);
            System.out.println("  Пароль: " + (newPassword.length() > 0 ? "***" : "не задан"));
            
            try {
                // Вызов функции register для создания пользователя
                // Параметры: имя пользователя, пароль, повтор пароля
                // ВАЖНО: Пароль нужно указать дважды для подтверждения
                rootContext.callFunction(
                    RootContextConstants.F_REGISTER, 
                    newUsername, 
                    newPassword, 
                    newPassword
                );
                System.out.println("✓ Пользователь создан успешно!");
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при создании пользователя:");
                System.err.println("  " + e.getMessage());
                System.err.println("  Возможные причины:");
                System.err.println("    - Пользователь с таким именем уже существует");
                System.err.println("    - Недостаточно прав для создания пользователей");
                System.err.println("    - Некорректное имя пользователя или пароль");
                return;
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при создании пользователя:");
                System.err.println("  " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            // ============================================
            // ШАГ 4: Получение контекста пользователя
            // ============================================
            System.out.println("\n--- Работа с контекстом пользователя ---");
            
            // Получение пути к контексту пользователя
            // Формат пути: "users.{username}"
            String userContextPath = ContextUtils.userContextPath(newUsername);
            System.out.println("Поиск контекста пользователя: " + userContextPath);
            
            Context userContext = cm.get(userContextPath);
            
            // Обработка случая, когда контекст не найден
            if (userContext == null) {
                System.err.println("✗ Контекст пользователя не найден: " + userContextPath);
                System.err.println("  Возможные причины:");
                System.err.println("    - Пользователь не был создан");
                System.err.println("    - Нет прав на доступ к контексту пользователя");
                System.err.println("    - Неправильный путь к контексту");
                return;
            }
            
            System.out.println("✓ Контекст пользователя найден: " + userContext.getPath());
            
            // ============================================
            // ШАГ 5: Изменение информации о пользователе
            // ============================================
            System.out.println("\n--- Изменение информации о пользователе ---");
            
            try {
                // Изменение email пользователя
                // setVariableField обновляет значение поля в переменной
                // Параметры: имя переменной, имя поля, новое значение, CallerController
                String newEmail = "testuser@example.com";
                System.out.println("Изменение email на: " + newEmail);
                
                userContext.setVariableField("childInfo", "email", newEmail, null);
                System.out.println("✓ Email изменен успешно!");
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при изменении email:");
                System.err.println("  " + e.getMessage());
                System.err.println("  Возможные причины:");
                System.err.println("    - Переменная 'childInfo' не существует");
                System.err.println("    - Поле 'email' недоступно для записи");
                System.err.println("    - Недостаточно прав на изменение");
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при изменении email:");
                System.err.println("  " + e.getMessage());
            }
            
            // ============================================
            // ШАГ 6: Чтение информации о пользователе
            // ============================================
            System.out.println("\n--- Чтение информации о пользователе ---");
            
            try {
                // Чтение переменной childInfo с информацией о пользователе
                DataTable userInfo = userContext.getVariable("childInfo");
                
                if (userInfo == null) {
                    System.out.println("⚠ Переменная 'childInfo' не найдена");
                } else if (userInfo.getRecordCount() == 0) {
                    System.out.println("⚠ Переменная 'childInfo' пуста");
                } else {
                    System.out.println("✓ Информация о пользователе:");
                    
                    // Чтение email
                    try {
                        String email = userInfo.rec().getString("email");
                        System.out.println("  Email: " + (email != null ? email : "не задан"));
                    } catch (Exception e) {
                        System.out.println("  Email: ошибка чтения (" + e.getMessage() + ")");
                    }
                    
                    // Можно прочитать другие поля:
                    // String firstname = userInfo.rec().getString("firstname");
                    // String lastname = userInfo.rec().getString("lastname");
                    // String phone = userInfo.rec().getString("phone");
                }
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при чтении информации о пользователе:");
                System.err.println("  " + e.getMessage());
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при чтении информации:");
                System.err.println("  " + e.getMessage());
            }
            
            // ============================================
            // ШАГ 7: Удаление пользователя (опционально)
            // ============================================
            System.out.println("\n--- Удаление пользователя (опционально) ---");
            System.out.println("ВАЖНО: Удаление пользователя закомментировано для безопасности");
            System.out.println("       Раскомментируйте код ниже для удаления тестового пользователя");
            
            /*
            try {
                System.out.println("Удаление пользователя: " + newUsername);
                // Вызов функции delete из контекста пользователя
                userContext.callFunction("delete");
                System.out.println("✓ Пользователь удален успешно!");
            } catch (ContextException e) {
                System.err.println("✗ Ошибка при удалении пользователя:");
                System.err.println("  " + e.getMessage());
            }
            */
            
            System.out.println("\n✓ Пример управления пользователями завершен!");
            System.out.println("  Созданный тестовый пользователь: " + newUsername);
            System.out.println("  (Для удаления раскомментируйте код в примере)");
            
        } catch (com.tibbo.aggregate.common.context.ContextException e) {
            // Специфическая обработка ошибок контекста
            System.err.println("\n✗ Ошибка работы с контекстом:");
            System.err.println("  Тип: " + e.getClass().getSimpleName());
            System.err.println("  Сообщение: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Причина: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            // Общая обработка ошибок
            System.err.println("\n✗ Неожиданная ошибка:");
            System.err.println("  Тип: " + e.getClass().getSimpleName());
            System.err.println("  Сообщение: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // ============================================
            // ШАГ 8: Гарантированное освобождение ресурсов
            // ============================================
            if (controller != null) {
                try {
                    if (controller.isConnected()) {
                        System.out.println("\nОтключение от сервера...");
                        controller.disconnect();
                        System.out.println("✓ Отключено успешно");
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки при отключении
                    System.err.println("⚠ Предупреждение при отключении: " + e.getMessage());
                }
            }
        }
    }
}

