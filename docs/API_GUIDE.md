# Руководство по API AggreGate SDK

Это руководство поможет вам понять и использовать API AggreGate SDK.

## Основные концепции

### Контексты

Контекст - это основной элемент AggreGate, представляющий контейнер для переменных, функций и событий.

```java
Context context = contextManager.get("users.admin");
```

### Переменные

Переменные хранят данные в формате DataTable.

```java
// Чтение переменной
DataTable variable = context.getVariable("variableName");
String value = variable.rec().getString("fieldName");

// Запись переменной
context.setVariableField("variableName", "fieldName", "newValue", null);
```

### Функции

Функции выполняют операции на сервере.

```java
// Вызов функции
DataTable result = context.callFunction("functionName", param1, param2);
```

### События

События используются для асинхронной коммуникации.

```java
// Подписка на событие
context.addEventListener("eventName", new DefaultContextEventListener() {
    @Override
    public void handle(Event event) {
        // Обработка события
    }
});
```

## Работа с агентами

### Создание агента

```java
RemoteServer server = new RemoteServer("localhost", 6460, "admin", "admin");
AgentContext context = new AgentContext(server, "myAgent", true);
Agent agent = new Agent(context, false, false, 0);
```

### Подключение агента

```java
agent.connect();
// Агент работает...
agent.disconnect();
```

## Работа с устройствами

### Создание устройства

```java
Context devicesContext = contextManager.get("users.admin.devices");
devicesContext.callFunction("add", driverId, deviceName, description);
```

### Чтение/запись переменных устройства

```java
Context deviceContext = contextManager.get("users.admin.devices.myDevice");
DataTable value = deviceContext.getVariable("setting");
deviceContext.setVariable("setting", newValue);
```

## Работа с пользователями

### Создание пользователя

```java
Context root = contextManager.getRoot();
root.callFunction("register", username, password, password);
```

### Управление пользователем

```java
Context userContext = contextManager.get("users.username");
userContext.setVariableField("childInfo", "email", "user@example.com", null);
```

## DataTable

### Создание DataTable

```java
TableFormat format = new TableFormat(1, 100);
format.addField("<name><S><D=Name>");
DataTable table = new SimpleDataTable(format);
```

### Работа с записями

```java
// Добавление записи
DataRecord record = table.addRecord();
record.setValue("name", "Value");

// Чтение записи
String value = table.rec().getString("name");
```

## Обработка ошибок

### Типы исключений

- `AggreGateException` - базовое исключение
- `ContextException` - ошибки работы с контекстом
- `DeviceException` - ошибки работы с устройствами
- `DisconnectionException` - разрыв соединения

### Обработка

```java
try {
    // Код работы с API
} catch (ContextException e) {
    Log.TEST.error("Context error", e);
} catch (DisconnectionException e) {
    // Переподключение
    agent.connect();
} catch (Exception e) {
    Log.TEST.error("Unexpected error", e);
}
```

## Лучшие практики

### 1. Всегда закрывайте соединения

```java
try {
    controller.connect();
    // Работа
} finally {
    controller.disconnect();
}
```

### 2. Используйте логирование

```java
Log.TEST.info("Operation completed");
Log.TEST.error("Error occurred", exception);
```

### 3. Проверяйте null

```java
DataTable value = context.getVariable("variable");
if (value != null && value.getRecordCount() > 0) {
    // Работа с данными
}
```

### 4. Обрабатывайте исключения

```java
try {
    context.callFunction("function");
} catch (ContextException e) {
    // Обработка ошибки
}
```

## Дополнительные ресурсы

- [Примеры использования](../examples/README.md)
- [Архитектура проекта](../ARCHITECTURE.md)
- [JavaDoc документация](../docs/)

---

*Документ обновлен: 2024-12-XX*

