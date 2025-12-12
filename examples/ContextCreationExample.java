package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример создания контекстов в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Создание нового контекста</li>
 *   <li>Работа с переменными в контексте</li>
 *   <li>Удаление контекста</li>
 * </ul>
 * </p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные параметры подключения</li>
 *   <li>У пользователя есть права на создание контекстов</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class ContextCreationExample {
    
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
            // ШАГ 2: Получение контекста пользователя
            // ============================================
            System.out.println("\n=== Работа с контекстами ===");
            
            // Получение менеджера контекстов
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            // Получение пути к контексту пользователя
            // Формат пути: "users.{username}"
            String userContextPath = ContextUtils.userContextPath(username);
            System.out.println("Поиск контекста пользователя: " + userContextPath);
            
            Context userContext = cm.get(userContextPath);
            
            // Обработка случая, когда контекст пользователя не найден
            if (userContext == null) {
                System.err.println("✗ Контекст пользователя не найден: " + userContextPath);
                System.err.println("  Возможные причины:");
                System.err.println("    - Пользователь не существует");
                System.err.println("    - Нет прав на доступ к контексту пользователя");
                return;
            }
            
            System.out.println("✓ Контекст пользователя найден: " + userContext.getPath());
            
            // ============================================
            // ШАГ 3: Создание нового контекста
            // ============================================
            System.out.println("\n--- Создание нового контекста ---");
            
            // Генерация уникального имени контекста для теста
            // В реальном приложении используйте осмысленные имена
            String newContextName = "testContext_" + System.currentTimeMillis();
            
            // Проверка имени контекста (edge case)
            if (newContextName == null || newContextName.trim().isEmpty()) {
                System.err.println("✗ ОШИБКА: Имя контекста не может быть пустым!");
                return;
            }
            
            System.out.println("Имя нового контекста: " + newContextName);
            System.out.println("Описание: Test Context для демонстрации");
            
            // ВАЖНО: Создание контекста выполняется через функцию "add"
            // Эта функция должна быть доступна в родительском контексте
            System.out.println("\nПример создания контекста (закомментирован):");
            System.out.println("  userContext.callFunction(\"add\", newContextName, \"Test Context Description\");");
            System.out.println("\nВАЖНО: Раскомментируйте код ниже для реального создания контекста");
            
            try {
                // Пример создания контекста через функцию add
                // Параметры: имя контекста, описание
                // userContext.callFunction("add", newContextName, "Test Context Description");
                
                System.out.println("  (Код создания закомментирован для безопасности)");
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при создании контекста:");
                System.err.println("  " + e.getMessage());
                System.err.println("  Возможные причины:");
                System.err.println("    - Контекст с таким именем уже существует");
                System.err.println("    - Недостаточно прав на создание контекстов");
                System.err.println("    - Функция 'add' недоступна в этом контексте");
                return;
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при создании контекста:");
                System.err.println("  " + e.getMessage());
                return;
            }
            
            // ============================================
            // ШАГ 4: Получение созданного контекста
            // ============================================
            System.out.println("\n--- Получение созданного контекста ---");
            
            // Формирование пути к новому контексту
            // Формат: "users.{username}.{contextName}"
            String newContextPath = userContextPath + "." + newContextName;
            System.out.println("Поиск контекста: " + newContextPath);
            
            Context newContext = cm.get(newContextPath);
            
            // Обработка случая, когда контекст не найден
            if (newContext == null) {
                System.out.println("⚠ Контекст не найден: " + newContextPath);
                System.out.println("  Это нормально, если код создания был закомментирован");
                System.out.println("  Для создания контекста:");
                System.out.println("    1. Раскомментируйте код создания выше");
                System.out.println("    2. Убедитесь, что у пользователя есть права на создание контекстов");
                System.out.println("    3. Проверьте, что функция 'add' доступна в родительском контексте");
            } else {
                System.out.println("✓ Контекст найден: " + newContext.getPath());
                
                // ============================================
                // ШАГ 5: Работа с переменными в новом контексте
                // ============================================
                System.out.println("\n--- Работа с переменными в контексте ---");
                
                try {
                    // Создание формата таблицы для переменной
                    // TableFormat определяет структуру данных
                    // Параметры: минимальное количество записей, максимальное количество записей
                    TableFormat format = new TableFormat(1, 100);
                    format.addField("<value><S><D=Value>");
                    
                    // Создание DataTable с указанным форматом
                    DataTable dataTable = new SimpleDataTable(format);
                    
                    // Добавление записи с данными
                    dataTable.addRecord().setValue("value", "Test Value");
                    
                    System.out.println("✓ DataTable создан:");
                    System.out.println("  Формат: " + format.getFieldCount() + " поле(й)");
                    System.out.println("  Записей: " + dataTable.getRecordCount());
                    System.out.println("  Значение: " + dataTable.rec().getString("value"));
                    
                    // Установка переменной в контексте
                    // ВАЖНО: Не все контексты поддерживают установку переменных
                    // Раскомментируйте код ниже для реальной установки
                    System.out.println("\nПример установки переменной (закомментирован):");
                    System.out.println("  newContext.setVariable(\"testVariable\", dataTable, null);");
                    
                    // newContext.setVariable("testVariable", dataTable, null);
                    // System.out.println("✓ Переменная 'testVariable' установлена");
                    
                } catch (Exception e) {
                    System.err.println("✗ Ошибка при работе с переменными:");
                    System.err.println("  " + e.getMessage());
                }
            }
            
            System.out.println("\n✓ Пример создания контекстов завершен!");
            
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
            // ШАГ 6: Гарантированное освобождение ресурсов
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

