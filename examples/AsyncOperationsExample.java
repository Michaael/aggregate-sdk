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
 * Пример асинхронных операций в AggreGate.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу</li>
 *   <li>Асинхронную подписку на события</li>
 *   <li>Обработку событий в отдельном потоке</li>
 *   <li>Асинхронное выполнение операций</li>
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
 * @version 1.3.5
 */
public class AsyncOperationsExample {
    
    private static RemoteServerController controller;
    private static ContextEventListener eventListener;
    private static volatile boolean running = true;
    
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
            System.out.println("\n=== Настройка асинхронной обработки событий ===");
            
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
            
            // ============================================
            // ШАГ 3: Создание асинхронного обработчика событий
            // ============================================
            // ContextEventListener обрабатывает события асинхронно
            // Метод handle() вызывается в отдельном потоке при получении события
            eventListener = new DefaultContextEventListener() {
                @Override
                public void handle(Event event) {
                    // ВАЖНО: Этот метод вызывается асинхронно в отдельном потоке!
                    // Убедитесь, что код здесь потокобезопасен
                    try {
                        System.out.println("\n[АСИНХРОННОЕ СОБЫТИЕ] " + new java.util.Date());
                        System.out.println("  Поток: " + Thread.currentThread().getName());
                        System.out.println("  Контекст: " + 
                            (event.getContext() != null ? event.getContext().getPath() : "неизвестно"));
                        System.out.println("  Имя события: " + event.getName());
                        
                        // Обработка данных события
                        DataTable eventData = event.getData();
                        if (eventData != null) {
                            System.out.println("  Данные события:");
                            if (eventData.getRecordCount() > 0) {
                                System.out.println("    Записей: " + eventData.getRecordCount());
                                // Можно обработать данные события здесь
                            } else {
                                System.out.println("    (нет данных)");
                            }
                        } else {
                            System.out.println("  Данные: нет данных");
                        }
                    } catch (Exception e) {
                        // ВАЖНО: Обрабатывайте исключения в обработчике событий
                        // чтобы не прервать обработку других событий
                        System.err.println("  ✗ Ошибка в обработчике событий: " + e.getMessage());
                    }
                }
            };
            
            System.out.println("✓ Асинхронный обработчик событий создан");
            
            // ============================================
            // ШАГ 4: Подписка на события
            // ============================================
            // ВАЖНО: Замените "eventName" на реальное имя события
            String eventName = "eventName"; // TODO: Замените на реальное имя события
            
            // Проверка имени события (edge case)
            if (eventName == null || eventName.trim().isEmpty() || "eventName".equals(eventName)) {
                System.out.println("⚠ ВНИМАНИЕ: Используется пример имени события!");
                System.out.println("  Замените 'eventName' на реальное имя события");
            }
            
            try {
                boolean added = rootContext.addEventListener(eventName, eventListener);
                if (added) {
                    System.out.println("✓ Подписка на событие '" + eventName + "' установлена");
                } else {
                    System.out.println("⚠ Подписка не установлена (возможно, обработчик уже был добавлен)");
                }
            } catch (IllegalArgumentException e) {
                System.err.println("✗ Ошибка: Событие '" + eventName + "' не найдено в контексте");
                System.err.println("  " + e.getMessage());
                return;
            } catch (com.tibbo.aggregate.common.context.ContextException e) {
                System.err.println("✗ Ошибка при подписке на событие:");
                System.err.println("  " + e.getMessage());
                return;
            }
            
            // ============================================
            // ШАГ 5: Запуск асинхронного потока обработки
            // ============================================
            // В реальном приложении это может быть пул потоков или ExecutorService
            System.out.println("\n--- Запуск асинхронного потока обработки ---");
            
            Thread eventThread = new Thread(() -> {
                // Этот поток может выполнять дополнительную обработку
                // В данном примере он просто работает как таймер
                System.out.println("✓ Поток обработки событий запущен");
                System.out.println("  Имя потока: " + Thread.currentThread().getName());
                
                int iteration = 0;
                while (running) {
                    try {
                        Thread.sleep(1000);
                        iteration++;
                        
                        // В реальном приложении здесь может быть:
                        // - Обработка очереди событий
                        // - Периодические операции
                        // - Мониторинг состояния
                        
                        if (iteration % 5 == 0) {
                            System.out.println("  [Поток] Итерация " + iteration + " (поток работает)");
                        }
                    } catch (InterruptedException e) {
                        // Корректная обработка прерывания потока
                        Thread.currentThread().interrupt();
                        System.out.println("  [Поток] Получен сигнал прерывания");
                        break;
                    }
                }
                System.out.println("✓ Поток обработки событий завершен");
            });
            
            // Установка имени потока для удобства отладки
            eventThread.setName("EventProcessingThread");
            eventThread.setDaemon(false); // Не daemon поток, чтобы дождаться завершения
            
            // Запуск потока
            eventThread.start();
            System.out.println("✓ Асинхронный поток запущен");
            
            // ============================================
            // ШАГ 6: Ожидание событий
            // ============================================
            System.out.println("\n=== Ожидание событий ===");
            System.out.println("Ожидание событий (10 секунд)...");
            System.out.println("В это время:");
            System.out.println("  - События будут обрабатываться асинхронно в обработчике");
            System.out.println("  - Дополнительный поток будет работать параллельно");
            System.out.println("Для остановки нажмите Ctrl+C");
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("\n⚠ Ожидание прервано пользователем");
            }
            
            // ============================================
            // ШАГ 7: Остановка асинхронной обработки
            // ============================================
            System.out.println("\n--- Остановка асинхронной обработки ---");
            
            // Установка флага остановки
            running = false;
            System.out.println("✓ Флаг остановки установлен");
            
            // Прерывание потока, если он еще работает
            if (eventThread.isAlive()) {
                eventThread.interrupt();
            }
            
            // Ожидание завершения потока
            try {
                eventThread.join(2000); // Ждем максимум 2 секунды
                if (eventThread.isAlive()) {
                    System.out.println("⚠ Поток не завершился в течение 2 секунд");
                } else {
                    System.out.println("✓ Поток завершен корректно");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("⚠ Ожидание завершения потока прервано");
            }
            
            // ============================================
            // ШАГ 8: Отписка от событий
            // ============================================
            System.out.println("\n--- Отписка от событий ---");
            
            try {
                // ВАЖНО: Всегда отписывайтесь от событий перед завершением работы
                boolean removed = rootContext.removeEventListener(eventName, eventListener);
                
                if (removed) {
                    System.out.println("✓ Отписка от события '" + eventName + "' выполнена");
                } else {
                    System.out.println("⚠ Обработчик не был найден (возможно, уже удален)");
                }
            } catch (Exception e) {
                System.err.println("⚠ Ошибка при отписке (игнорируется): " + e.getMessage());
            }
            
            System.out.println("\n✓ Пример асинхронных операций завершен!");
            
        } catch (InterruptedException e) {
            // Обработка прерывания главного потока
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
            // ШАГ 9: Гарантированное освобождение ресурсов
            // ============================================
            // ВАЖНО: Устанавливаем флаг остановки перед отключением
            running = false;
            
            // Отписываемся от событий, если еще не отписались
            if (eventListener != null && controller != null) {
                try {
                    ContextManager cm = controller.getContextManager();
                    if (cm != null) {
                        Context rootContext = cm.getRoot();
                        if (rootContext != null) {
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

