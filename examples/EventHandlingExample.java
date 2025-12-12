package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.event.ContextEventListener;
import com.tibbo.aggregate.common.context.DefaultContextEventListener;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример работы с событиями в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Подписку на события</li>
 *   <li>Обработку событий</li>
 *   <li>Отписку от событий</li>
 * </ul>
 * </p>
 * 
 * <p>Для запуска убедитесь, что:
 * <ul>
 *   <li>AggreGate сервер запущен</li>
 *   <li>Указаны правильные параметры подключения</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.2.1
 */
public class EventHandlingExample {
    
    private static RemoteServerController controller;
    private static ContextEventListener eventListener;
    
    /**
     * Точка входа в приложение.
     * 
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Инициализация системы логирования
        Log.start();
        
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
            // ШАГ 2: Получение контекста для подписки на события
            // ============================================
            System.out.println("\n=== Настройка обработки событий ===");
            
            // Получение менеджера контекстов
            ContextManager cm = controller.getContextManager();
            if (cm == null) {
                System.err.println("✗ Ошибка: Не удалось получить менеджер контекстов");
                return;
            }
            
            // Получение контекста для подписки на события
            // Можно использовать корневой контекст или любой другой контекст
            Context rootContext = cm.getRoot();
            if (rootContext == null) {
                System.err.println("✗ Ошибка: Не удалось получить корневой контекст");
                return;
            }
            
            // ============================================
            // ШАГ 3: Создание обработчика событий
            // ============================================
            // ContextEventListener - интерфейс для обработки событий
            // DefaultContextEventListener - базовая реализация
            eventListener = new DefaultContextEventListener() {
                @Override
                public void handle(Event event) {
                    // Этот метод вызывается при получении события
                    System.out.println("\n[СОБЫТИЕ] " + new java.util.Date());
                    System.out.println("  Контекст: " + 
                        (event.getContext() != null ? event.getContext().getPath() : "неизвестно"));
                    System.out.println("  Имя события: " + event.getName());
                    
                    // Обработка данных события
                    DataTable eventData = event.getData();
                    if (eventData != null) {
                        System.out.println("  Данные события:");
                        if (eventData.getRecordCount() > 0) {
                            // Вывод основных полей первой записи
                            System.out.println("    Записей: " + eventData.getRecordCount());
                            // Можно перебрать все поля:
                            // for (int i = 0; i < eventData.getFormat().getFieldCount(); i++) {
                            //     String fieldName = eventData.getFormat().getField(i).getName();
                            //     Object value = eventData.rec().getValue(i);
                            //     System.out.println("    " + fieldName + ": " + value);
                            // }
                        } else {
                            System.out.println("    (нет данных)");
                        }
                    } else {
                        System.out.println("  Данные: нет данных");
                    }
                }
            };
            
            System.out.println("✓ Обработчик событий создан");
            
            // ============================================
            // ШАГ 4: Подписка на события
            // ============================================
            // ВАЖНО: Замените "eventName" на реальное имя события
            // Для получения списка доступных событий используйте:
            //   List<EventDefinition> events = context.getEventDefinitions();
            String eventName = "eventName"; // TODO: Замените на реальное имя события
            
            // Проверка имени события (edge case)
            if (eventName == null || eventName.trim().isEmpty() || "eventName".equals(eventName)) {
                System.out.println("⚠ ВНИМАНИЕ: Используется пример имени события!");
                System.out.println("  Замените 'eventName' на реальное имя события");
                System.out.println("  Для получения списка событий используйте:");
                System.out.println("    List<EventDefinition> events = context.getEventDefinitions();");
            }
            
            try {
                // Подписка на событие
                // addEventListener добавляет обработчик для указанного события
                boolean added = rootContext.addEventListener(eventName, eventListener);
                
                if (added) {
                    System.out.println("✓ Подписка на событие '" + eventName + "' установлена");
                } else {
                    System.out.println("⚠ Подписка не установлена (возможно, обработчик уже был добавлен)");
                }
            } catch (IllegalArgumentException e) {
                // Событие не существует в контексте
                System.err.println("✗ Ошибка: Событие '" + eventName + "' не найдено в контексте");
                System.err.println("  Сообщение: " + e.getMessage());
                System.err.println("  Для получения списка доступных событий используйте:");
                System.err.println("    context.getEventDefinitions()");
                return;
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при подписке на событие:");
                System.err.println("  " + e.getMessage());
                return;
            }
            
            // ============================================
            // ШАГ 5: Ожидание событий
            // ============================================
            // В реальном приложении это может быть бесконечный цикл
            // или работа в отдельном потоке
            System.out.println("\n=== Ожидание событий ===");
            System.out.println("Ожидание событий (10 секунд)...");
            System.out.println("(В реальном приложении это может быть бесконечный цикл)");
            System.out.println("Для остановки нажмите Ctrl+C");
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\n⚠ Ожидание прервано пользователем");
            }
            
            // ============================================
            // ШАГ 6: Отписка от событий
            // ============================================
            System.out.println("\n=== Отписка от событий ===");
            
            try {
                // Отписка от события
                // ВАЖНО: Всегда отписывайтесь от событий перед завершением работы
                // Это освобождает ресурсы и предотвращает утечки памяти
                boolean removed = rootContext.removeEventListener(eventName, eventListener);
                
                if (removed) {
                    System.out.println("✓ Отписка от события '" + eventName + "' выполнена");
                } else {
                    System.out.println("⚠ Обработчик не был найден (возможно, уже удален)");
                }
            } catch (Exception e) {
                System.err.println("⚠ Ошибка при отписке (игнорируется): " + e.getMessage());
            }
            
            System.out.println("\n✓ Пример работы с событиями завершен!");
            
        } catch (InterruptedException e) {
            // Обработка прерывания потока
            Thread.currentThread().interrupt();
            System.err.println("\n⚠ Прервано ожидание: " + e.getMessage());
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
            // ВАЖНО: Отписываемся от событий перед отключением
            if (eventListener != null && controller != null) {
                try {
                    ContextManager cm = controller.getContextManager();
                    if (cm != null) {
                        Context rootContext = cm.getRoot();
                        if (rootContext != null) {
                            // Пытаемся отписаться от всех возможных событий
                            // В реальном приложении храните список подписок
                            try {
                                rootContext.removeEventListener("eventName", eventListener);
                            } catch (Exception e) {
                                // Игнорируем ошибки при отписке в finally
                            }
                        }
                    }
                } catch (Exception e) {
                    // Игнорируем ошибки
                }
            }
            
            // Отключение от сервера
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

