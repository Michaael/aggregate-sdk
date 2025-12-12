package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

/**
 * Пример чтения и записи переменных контекста.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Чтение переменной из корневого контекста</li>
 *   <li>Запись значения в переменную</li>
 *   <li>Проверку изменений</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.0
 */
public class ReadWriteVariableExample {
    
    /**
     * Точка входа в приложение.
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация системы логирования
        Log.start();
        
        // Объявляем controller как null для гарантированного освобождения ресурсов
        RemoteServerController controller = null;
        
        try {
            // ============================================
            // ШАГ 1: Настройка и подключение к серверу
            // ============================================
            System.out.println("=== Настройка подключения ===");
            
            // Создание объекта сервера
            // ВАЖНО: Замените параметры на реальные значения вашего сервера
            RemoteServer server = new RemoteServer(
                "localhost",                    // Адрес сервера
                RemoteServer.DEFAULT_PORT,       // Порт (по умолчанию: 6460)
                "admin",                        // Имя пользователя
                "admin"                         // Пароль
            );
            
            // Создание контроллера с автоматическим переподключением
            controller = new RemoteServerController(server, true);
            
            // Подключение с обработкой ошибок
            try {
                System.out.println("Подключение к серверу...");
                controller.connect();
                System.out.println("✓ Подключение установлено");
            } catch (Exception e) {
                System.err.println("✗ Ошибка подключения: " + e.getMessage());
                System.err.println("  Проверьте, что сервер запущен и доступен");
                return;
            }
            
            // Аутентификация с обработкой ошибок
            try {
                System.out.println("Аутентификация...");
                controller.login();
                System.out.println("✓ Аутентификация успешна");
            } catch (Exception e) {
                System.err.println("✗ Ошибка аутентификации: " + e.getMessage());
                System.err.println("  Проверьте правильность имени пользователя и пароля");
                return;
            }
            
            // Получение менеджера контекстов
            // ContextManager - это основной интерфейс для работы с контекстами сервера
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            // Получение корневого контекста
            // Корневой контекст - это точка входа в иерархию контекстов сервера
            Context rootContext = cm.getRoot();
            if (rootContext == null) {
                System.err.println("✗ Ошибка: Не удалось получить корневой контекст");
                return;
            }
            
            // ============================================
            // ШАГ 2: Пример 1 - Чтение переменной версии
            // ============================================
            System.out.println("\n=== Пример 1: Чтение переменной версии сервера ===");
            
            try {
                // Получение переменной V_VERSION из корневого контекста
                // Переменные в AggreGate хранятся в формате DataTable
                DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
                
                // Проверка на null (edge case)
                if (versionData == null) {
                    System.err.println("⚠ Переменная версии не найдена");
                } else if (versionData.getRecordCount() == 0) {
                    System.err.println("⚠ Переменная версии пуста");
                } else {
                    // Получение значения поля из первой записи
                    // RootContextConstants.VF_VERSION_VERSION - это имя поля с версией
                    String serverVersion = versionData.rec().getString(
                        RootContextConstants.VF_VERSION_VERSION);
                    
                    if (serverVersion != null) {
                        System.out.println("✓ Версия сервера: " + serverVersion);
                    } else {
                        System.out.println("⚠ Поле версии не содержит значения");
                    }
                }
            } catch (ContextException e) {
                System.err.println("✗ Ошибка при чтении переменной версии:");
                System.err.println("  " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("  Причина: " + e.getCause().getMessage());
                }
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при чтении переменной:");
                System.err.println("  " + e.getMessage());
                e.printStackTrace();
            }
            
            // ============================================
            // ШАГ 3: Пример 2 - Чтение всех полей переменной
            // ============================================
            System.out.println("\n=== Пример 2: Чтение всех полей переменной ===");
            
            try {
                // Получение переменной V_INFO с информацией о сервере
                DataTable infoData = rootContext.getVariable(RootContextConstants.V_INFO);
                
                // Обработка edge cases
                if (infoData == null) {
                    System.err.println("⚠ Переменная информации о сервере не найдена");
                } else if (infoData.getRecordCount() == 0) {
                    System.err.println("⚠ Переменная информации о сервере пуста");
                } else {
                    System.out.println("✓ Информация о сервере:");
                    System.out.println("  Записей в таблице: " + infoData.getRecordCount());
                    System.out.println("  Полей в записи: " + infoData.getFormat().getFieldCount());
                    
                    // Пример перебора всех полей первой записи
                    System.out.println("  Поля и значения:");
                    for (int i = 0; i < infoData.getFormat().getFieldCount(); i++) {
                        try {
                            String fieldName = infoData.getFormat().getField(i).getName();
                            Object value = infoData.rec().getValue(i);
                            String valueStr = (value != null) ? value.toString() : "null";
                            // Ограничиваем длину значения для читаемости
                            if (valueStr.length() > 50) {
                                valueStr = valueStr.substring(0, 47) + "...";
                            }
                            System.out.println("    " + fieldName + ": " + valueStr);
                        } catch (Exception e) {
                            System.err.println("    Ошибка при чтении поля " + i + ": " + e.getMessage());
                        }
                    }
                }
            } catch (ContextException e) {
                System.err.println("✗ Ошибка при чтении переменной информации:");
                System.err.println("  " + e.getMessage());
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка:");
                System.err.println("  " + e.getMessage());
            }
            
            // ============================================
            // ШАГ 4: Пример 3 - Запись переменной
            // ============================================
            System.out.println("\n=== Пример 3: Запись переменной ===");
            System.out.println("ВАЖНО: Не все переменные доступны для записи!");
            System.out.println("       Многие системные переменные только для чтения.");
            
            try {
                // Пример синтаксиса записи переменной
                // ВАЖНО: Этот код закомментирован, так как корневой контекст
                // обычно не позволяет записывать переменные напрямую
                
                // Синтаксис 1: Запись через setVariableField
                // rootContext.setVariableField(
                //     "variableName",    // Имя переменной
                //     "fieldName",      // Имя поля
                //     "newValue",       // Новое значение
                //     null              // CallerController (null = текущий пользователь)
                // );
                
                // Синтаксис 2: Запись всей переменной
                // DataTable newData = new SimpleDataTable(format);
                // newData.addRecord().setValue("fieldName", "value");
                // rootContext.setVariable("variableName", newData, null);
                
                System.out.println("  Пример синтаксиса (закомментирован):");
                System.out.println("    context.setVariableField(\"varName\", \"fieldName\", \"value\", null);");
                System.out.println("  Для реальной записи используйте контекст пользователя:");
                System.out.println("    Context userContext = cm.get(\"users.admin\");");
                System.out.println("    userContext.setVariableField(...);");
                
            } catch (ContextException e) {
                // Ожидаемая ошибка для системных переменных
                System.out.println("  ⚠ Переменная недоступна для записи (это нормально для системных переменных)");
                System.out.println("    Сообщение: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("✗ Неожиданная ошибка при записи:");
                System.err.println("  " + e.getMessage());
            }
            
            // ============================================
            // ШАГ 5: Пример 4 - Проверка существования переменной
            // ============================================
            System.out.println("\n=== Пример 4: Проверка существования переменной ===");
            
            try {
                // Попытка получить переменную
                DataTable testVar = rootContext.getVariable("nonexistentVariable");
                
                if (testVar == null) {
                    System.out.println("  ✓ Переменная не существует (вернулся null)");
                } else {
                    System.out.println("  ✓ Переменная существует");
                }
            } catch (ContextException e) {
                // Исключение также может указывать на отсутствие переменной
                System.out.println("  ⚠ Переменная не найдена или недоступна:");
                System.out.println("    " + e.getMessage());
            }
            
            System.out.println("\n✓ Все примеры выполнены!");
            
        } catch (ContextException e) {
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

