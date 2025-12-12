# Примеры использования AggreGate SDK

Эта папка содержит дополнительные примеры использования AggreGate SDK, которые помогут вам быстро начать работу.

## 📚 Содержание

### Базовые примеры

1. **SimpleConnectionExample.java** - Простейший пример подключения к серверу
   - ✅ Улучшено в 1.3.5: подробные комментарии, обработка edge cases, улучшенная обработка ошибок
2. **ReadWriteVariableExample.java** - Работа с переменными контекста (чтение и запись)
   - ✅ Улучшено в 1.3.5: расширенные комментарии, примеры обработки ошибок, проверка edge cases

### Продвинутые примеры

3. **EventHandlingExample.java** - Подписка и обработка событий (новое в 1.3.4)
   - ✅ Улучшено в 1.3.5: улучшенная обработка событий, комментарии по асинхронной работе
4. **ActionExecutionExample.java** - Выполнение действий через контекст (новое в 1.3.4)
   - ✅ Улучшено в 1.3.5: детальные комментарии, структура выполнения действий, обработка ошибок
5. **DeviceManagementExample.java** - Управление устройствами (новое в 1.3.4)
   - ✅ Улучшено в 1.3.5: подробные комментарии, обработка ошибок, примеры работы с устройствами
6. **ErrorHandlingExample.java** - Правильная обработка ошибок (новое в 1.3.4)
7. **UserManagementExample.java** - Управление пользователями (новое в 1.3.5)
   - ✅ Улучшено в 1.3.5: расширенные комментарии, обработка ошибок, примеры работы с пользователями
8. **ContextCreationExample.java** - Создание контекстов (новое в 1.3.5)
   - ✅ Улучшено в 1.3.5: подробные комментарии, примеры создания контекстов
9. **AsyncOperationsExample.java** - Асинхронные операции (новое в 1.3.5)
   - ✅ Улучшено в 1.3.5: детальные комментарии по асинхронной работе, обработка потоков

### ✨ Улучшения в версии 1.3.5

Все примеры были значительно улучшены с добавлением:
- 📝 **Подробных пошаговых комментариев** - каждый шаг объяснен детально
- ⚠️ **Обработки edge cases** - проверки на null, пустые значения, некорректные параметры
- 🔧 **Улучшенной обработки ошибок** - понятные сообщения об ошибках с рекомендациями
- ✅ **Визуальных индикаторов** - использование ✓/✗ для обозначения успеха/ошибки
- 🔒 **Правильного освобождения ресурсов** - гарантированное закрытие соединений в finally блоках

### Дополнительные примеры в модулях

- **demo-agent** - Полнофункциональный агент
- **demo-api** - Примеры работы с API (пользователи, устройства, действия)
- **demo-driver** - Пример драйвера устройства
- **demo-plugin** - Пример серверного плагина

## 🚀 Быстрый старт

### Пример 1: Простое подключение

```java
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

public class SimpleConnection {
    public static void main(String[] args) {
        try {
            Log.start();
            
            RemoteServer server = new RemoteServer(
                "localhost", 
                RemoteServer.DEFAULT_PORT, 
                "admin", 
                "admin"
            );
            
            RemoteServerController controller = new RemoteServerController(server, true);
            controller.connect();
            controller.login();
            
            System.out.println("Успешно подключено к серверу!");
            
            controller.disconnect();
        } catch (Exception e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

### Пример 2: Чтение переменной

```java
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

public class ReadVariable {
    public static void main(String[] args) {
        try {
            RemoteServer server = new RemoteServer("localhost", 
                RemoteServer.DEFAULT_PORT, "admin", "admin");
            RemoteServerController controller = new RemoteServerController(server, true);
            
            controller.connect();
            controller.login();
            
            ContextManager cm = controller.getContextManager();
            Context root = cm.getRoot();
            
            // Чтение переменной версии
            DataTable version = root.getVariable(RootContextConstants.V_VERSION);
            String versionString = version.rec().getString(
                RootContextConstants.VF_VERSION_VERSION);
            
            System.out.println("Версия сервера: " + versionString);
            
            controller.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### Пример 3: Запись переменной

```java
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

public class WriteVariable {
    public static void main(String[] args) {
        try {
            RemoteServer server = new RemoteServer("localhost", 
                RemoteServer.DEFAULT_PORT, "admin", "admin");
            RemoteServerController controller = new RemoteServerController(server, true);
            
            controller.connect();
            controller.login();
            
            ContextManager cm = controller.getContextManager();
            Context context = cm.get("users.admin"); // Пример контекста
            
            // Запись простого значения
            context.setVariableField("variableName", "fieldName", "новое значение", null);
            
            // Или запись через DataTable
            DataTable value = context.getVariable("variableName");
            value.rec().setValue("fieldName", "новое значение");
            context.setVariable("variableName", value);
            
            System.out.println("Переменная успешно обновлена!");
            
            controller.disconnect();
        } catch (ContextException e) {
            System.err.println("Ошибка работы с контекстом: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 📖 Дополнительные примеры

Более сложные примеры доступны в модулях проекта:

- **demo-agent** - Полнофункциональный агент с событиями и переменными
- **demo-api** - Примеры работы с API (пользователи, устройства, действия)
- **demo-driver** - Пример драйвера устройства
- **demo-plugin** - Пример серверного плагина

## 🔗 Полезные ссылки

- [Основная документация](../README.md)
- [Архитектура проекта](../ARCHITECTURE.md)
- [Руководство по вкладу](../CONTRIBUTING.md)

## 💡 Советы

1. **Всегда используйте try-catch** для обработки исключений
   - Обрабатывайте специфические исключения (ContextException, AggreGateException)
   - Предоставляйте понятные сообщения об ошибках с рекомендациями
2. **Закрывайте соединения** в блоке finally или используйте try-with-resources
   - Всегда проверяйте `controller.isConnected()` перед отключением
   - Игнорируйте ошибки при отключении в finally блоках
3. **Логируйте ошибки** используя `Log` класс
   - Используйте `Log.start()` в начале приложения
4. **Проверяйте null** перед использованием объектов
   - Проверяйте результаты `cm.get()`, `context.getVariable()` и т.д.
   - Обрабатывайте edge cases (пустые строки, null значения)
5. **Используйте визуальные индикаторы** для лучшей читаемости
   - ✓ для успешных операций
   - ✗ для ошибок
   - ⚠ для предупреждений
6. **Добавляйте комментарии** для сложных операций
   - Объясняйте каждый шаг в сложных примерах
   - Указывайте возможные причины ошибок

## ❓ Вопросы?

Если у вас есть вопросы или нужна помощь, создайте [issue](https://github.com/tibbo/aggregate-sdk/issues) в репозитории.

