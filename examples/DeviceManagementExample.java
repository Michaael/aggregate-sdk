package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример управления устройствами в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Получение списка устройств</li>
 *   <li>Чтение переменных устройства</li>
 *   <li>Выполнение операций устройства</li>
 * </ul>
 * </p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные параметры подключения</li>
 *   <li>В системе есть устройства</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.1
 */
public class DeviceManagementExample {
    
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
            
            // Проверка параметров (edge case)
            if (username == null || username.trim().isEmpty()) {
                System.err.println("✗ ОШИБКА: Имя пользователя не может быть пустым!");
                return;
            }
            
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
            // ШАГ 2: Получение контекста устройств
            // ============================================
            System.out.println("\n=== Работа с устройствами ===");
            
            // Получение менеджера контекстов
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            // Получение пути к контексту устройств пользователя
            // Формат пути: "users.{username}.devices"
            String devicesPath = ContextUtils.devicesContextPath(username);
            System.out.println("Поиск контекста устройств: " + devicesPath);
            
            Context devicesContext = cm.get(devicesPath);
            
            // Обработка случая, когда контекст устройств не найден
            if (devicesContext == null) {
                System.out.println("⚠ Контекст устройств не найден: " + devicesPath);
                System.out.println("  Возможные причины:");
                System.out.println("    - У пользователя нет устройств");
                System.out.println("    - Пользователь не имеет прав на доступ к устройствам");
                System.out.println("    - Контекст еще не создан");
                System.out.println("\n  Для получения списка устройств используйте:");
                System.out.println("    List<Context> devices = ContextUtils.expandMaskToContexts(");
                System.out.println("        cm, devicesPath + \".*\");");
                return;
            }
            
            System.out.println("✓ Контекст устройств найден: " + devicesContext.getPath());
            
            // ============================================
            // ШАГ 3: Работа со списком устройств
            // ============================================
            System.out.println("\n--- Информация о работе с устройствами ---");
            System.out.println("Для получения списка устройств используйте:");
            System.out.println("  List<Context> devices = ContextUtils.expandMaskToContexts(");
            System.out.println("      cm, devicesPath + \".*\");");
            System.out.println("\nДля каждого устройства можно:");
            System.out.println("  - Читать переменные: deviceContext.getVariable(\"varName\")");
            System.out.println("  - Вызывать функции: deviceContext.callFunction(\"funcName\", params)");
            System.out.println("  - Подписываться на события: deviceContext.addEventListener(...)");
            
            // ============================================
            // ШАГ 4: Пример работы с конкретным устройством
            // ============================================
            System.out.println("\n--- Пример работы с конкретным устройством ---");
            
            // ВАЖНО: Замените "deviceName" на реальное имя устройства из вашей системы
            // Для получения списка реальных устройств используйте ContextUtils.expandMaskToContexts
            String deviceName = "deviceName"; // TODO: Замените на реальное имя устройства
            
            // Проверка имени устройства (edge case)
            if (deviceName == null || deviceName.trim().isEmpty() || "deviceName".equals(deviceName)) {
                System.out.println("⚠ ВНИМАНИЕ: Используется пример имени устройства!");
                System.out.println("  Замените 'deviceName' на реальное имя устройства");
                System.out.println("  Для получения списка устройств используйте ContextUtils.expandMaskToContexts");
            } else {
                // Получение пути к контексту устройства
                // Формат пути: "users.{username}.devices.{deviceName}"
                String devicePath = ContextUtils.deviceContextPath(username, deviceName);
                System.out.println("Поиск устройства: " + devicePath);
                
                Context deviceContext = cm.get(devicePath);
                
                if (deviceContext == null) {
                    System.out.println("⚠ Устройство не найдено: " + devicePath);
                    System.out.println("  Проверьте правильность имени устройства");
                } else {
                    System.out.println("✓ Устройство найдено: " + deviceContext.getPath());
                    
                    // ============================================
                    // ШАГ 5: Чтение переменной устройства
                    // ============================================
                    System.out.println("\n--- Чтение переменной устройства ---");
                    
                    // ВАЖНО: Замените на реальное имя переменной вашего устройства
                    String variableName = "variableName"; // TODO: Замените на реальное имя
                    
                    try {
                        DataTable variable = deviceContext.getVariable(variableName);
                        
                        if (variable == null) {
                            System.out.println("⚠ Переменная '" + variableName + "' не найдена");
                            System.out.println("  Возможные причины:");
                            System.out.println("    - Переменная не существует");
                            System.out.println("    - Нет прав на чтение переменной");
                        } else if (variable.getRecordCount() == 0) {
                            System.out.println("⚠ Переменная '" + variableName + "' пуста");
                        } else {
                            System.out.println("✓ Значение переменной '" + variableName + "':");
                            System.out.println("  " + variable.toString());
                            
                            // Пример получения конкретного поля
                            // String value = variable.rec().getString("fieldName");
                        }
                    } catch (com.tibbo.aggregate.common.context.ContextException e) {
                        System.err.println("✗ Ошибка при чтении переменной:");
                        System.err.println("  " + e.getMessage());
                    } catch (Exception e) {
                        System.err.println("✗ Неожиданная ошибка при чтении переменной:");
                        System.err.println("  " + e.getMessage());
                    }
                    
                    // ============================================
                    // ШАГ 6: Выполнение функции устройства
                    // ============================================
                    System.out.println("\n--- Выполнение функции устройства ---");
                    
                    // ВАЖНО: Замените на реальное имя функции вашего устройства
                    String functionName = "functionName"; // TODO: Замените на реальное имя
                    
                    System.out.println("Пример синтаксиса вызова функции:");
                    System.out.println("  // Создание параметров (если нужны)");
                    System.out.println("  DataTable params = new SimpleDataTable(format);");
                    System.out.println("  params.addRecord().setValue(\"paramName\", \"value\");");
                    System.out.println("  ");
                    System.out.println("  // Вызов функции");
                    System.out.println("  DataTable result = deviceContext.callFunction(");
                    System.out.println("      \"" + functionName + "\", params);");
                    System.out.println("  ");
                    System.out.println("  // Обработка результата");
                    System.out.println("  if (result != null && result.getRecordCount() > 0) {");
                    System.out.println("      String value = result.rec().getString(\"fieldName\");");
                    System.out.println("  }");
                }
            }
            
            System.out.println("\n✓ Пример работы с устройствами завершен!");
            
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
            // ШАГ 7: Гарантированное освобождение ресурсов
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

