package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.ActionIdentifier;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.action.GenericActionResponse;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример выполнения действий (actions) в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Поиск доступных действий</li>
 *   <li>Выполнение действия</li>
 *   <li>Обработку результата</li>
 * </ul>
 * </p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные параметры подключения</li>
 *   <li>В контексте есть доступные действия</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.1
 */
public class ActionExecutionExample {
    
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
            // ШАГ 2: Получение контекста с действиями
            // ============================================
            System.out.println("\n=== Поиск доступных действий ===");
            
            // Получение менеджера контекстов
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            // Получение корневого контекста
            Context rootContext = cm.getRoot();
            if (rootContext == null) {
                System.err.println("✗ Ошибка: Не удалось получить корневой контекст");
                return;
            }
            
            // ВАЖНО: Замените на реальный путь к контексту с действиями
            // Действия обычно находятся в контекстах пользователей или устройств
            String contextPath = "users.admin"; // TODO: Замените на реальный путь
            
            System.out.println("Поиск контекста: " + contextPath);
            Context targetContext = cm.get(contextPath);
            
            // Обработка случая, когда контекст не найден
            if (targetContext == null) {
                System.out.println("⚠ Контекст не найден: " + contextPath);
                System.out.println("  Возможные причины:");
                System.out.println("    - Контекст не существует");
                System.out.println("    - Нет прав на доступ к контексту");
                System.out.println("    - Неправильный путь к контексту");
                System.out.println("\n  Попробуйте использовать существующий контекст:");
                System.out.println("    - users.{username} - контекст пользователя");
                System.out.println("    - users.{username}.devices.{deviceName} - контекст устройства");
                return;
            }
            
            System.out.println("✓ Контекст найден: " + targetContext.getPath());
            
            // ============================================
            // ШАГ 3: Получение списка доступных действий
            // ============================================
            System.out.println("\n--- Получение списка действий ---");
            
            try {
                // Получение определений действий из контекста
                java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actionDefinitions = 
                    targetContext.getActionDefinitions();
                
                if (actionDefinitions == null || actionDefinitions.isEmpty()) {
                    System.out.println("⚠ В контексте нет доступных действий");
                    System.out.println("  Действия могут быть определены в других контекстах");
                } else {
                    System.out.println("✓ Найдено действий: " + actionDefinitions.size());
                    System.out.println("  Список действий:");
                    for (com.tibbo.aggregate.common.action.ActionDefinition def : actionDefinitions) {
                        System.out.println("    - " + def.getName() + 
                            (def.getDescription() != null ? " (" + def.getDescription() + ")" : ""));
                    }
                }
            } catch (Exception e) {
                System.err.println("✗ Ошибка при получении списка действий:");
                System.err.println("  " + e.getMessage());
            }
            
            // ============================================
            // ШАГ 4: Структура выполнения действия
            // ============================================
            System.out.println("\n--- Структура выполнения действия ---");
            System.out.println("Для выполнения действия необходимо выполнить следующие шаги:");
            System.out.println();
            System.out.println("1. Получить ActionIdentifier из контекста:");
            System.out.println("   ActionIdentifier actionId = targetContext.getActionIdentifier(\"actionName\");");
            System.out.println();
            System.out.println("2. Подготовить входные данные (если нужны):");
            System.out.println("   DataTable inputData = new SimpleDataTable(format);");
            System.out.println("   inputData.addRecord().setValue(\"paramName\", \"value\");");
            System.out.println();
            System.out.println("3. Создать команду действия:");
            System.out.println("   GenericActionCommand command = new GenericActionCommand(");
            System.out.println("       actionId, inputData);");
            System.out.println();
            System.out.println("4. Выполнить действие:");
            System.out.println("   GenericActionResponse response = targetContext.executeAction(command);");
            System.out.println();
            System.out.println("5. Обработать результат:");
            System.out.println("   if (response != null) {");
            System.out.println("       DataTable result = response.getResult();");
            System.out.println("       if (result != null && result.getRecordCount() > 0) {");
            System.out.println("           // Обработка результата");
            System.out.println("       }");
            System.out.println("   }");
            
            // ============================================
            // ШАГ 5: Пример кода выполнения действия (закомментирован)
            // ============================================
            System.out.println("\n--- Пример кода (закомментирован) ---");
            System.out.println("/*");
            System.out.println("try {");
            System.out.println("    // Получение идентификатора действия");
            System.out.println("    ActionIdentifier actionId = targetContext.getActionIdentifier(\"actionName\");");
            System.out.println("    ");
            System.out.println("    if (actionId == null) {");
            System.out.println("        System.err.println(\"Действие не найдено\");");
            System.out.println("        return;");
            System.out.println("    }");
            System.out.println("    ");
            System.out.println("    // Создание входных данных (если нужны)");
            System.out.println("    DataTable inputData = new SimpleDataTable(actionId.getInputFormat());");
            System.out.println("    inputData.addRecord().setValue(\"paramName\", \"value\");");
            System.out.println("    ");
            System.out.println("    // Создание команды");
            System.out.println("    GenericActionCommand command = new GenericActionCommand(");
            System.out.println("        actionId, inputData);");
            System.out.println("    ");
            System.out.println("    // Выполнение действия");
            System.out.println("    GenericActionResponse response = targetContext.executeAction(command);");
            System.out.println("    ");
            System.out.println("    // Обработка результата");
            System.out.println("    if (response != null) {");
            System.out.println("        DataTable result = response.getResult();");
            System.out.println("        if (result != null) {");
            System.out.println("            System.out.println(\"Результат: \" + result);");
            System.out.println("        }");
            System.out.println("    }");
            System.out.println("} catch (ContextException e) {");
            System.out.println("    System.err.println(\"Ошибка выполнения действия: \" + e.getMessage());");
            System.out.println("}");
            System.out.println("*/");
            
            System.out.println("\n✓ Пример работы с действиями завершен!");
            
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

