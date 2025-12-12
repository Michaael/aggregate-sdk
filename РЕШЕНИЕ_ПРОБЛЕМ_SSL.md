# Решение проблем SSL/TLS при подключении к AggreGate серверу

## Проблема

При подключении к AggreGate серверу могут возникать ошибки SSL/TLS:

```
javax.net.ssl.SSLException: Software caused connection abort: recv failed
javax.net.ssl.SSLException: Connection reset
```

## Причины

1. **Несоответствие типа соединения**: Клиент пытается установить SSL соединение, а сервер ожидает обычное соединение (или наоборот)
2. **Проблемы с сертификатами**: Самоподписанные сертификаты или проблемы с доверием
3. **Неправильный порт**: Использование порта для SSL на не-SSL сервере или наоборот

## Решение

### Вариант 1: Использование небезопасного порта (рекомендуется для тестирования)

Если сервер настроен на небезопасное соединение, используйте порт **6461** вместо **6460**:

```java
// Небезопасное соединение (без SSL)
int serverPort = RemoteServer.DEFAULT_NON_SECURE_PORT; // 6461
RemoteServer server = new RemoteServer("localhost", serverPort, "admin", "admin");
RemoteServerController controller = new RemoteServerController(server, true);
```

**Важно**: SDK автоматически определяет тип соединения по номеру порта:
- Порт **6460** → SSL соединение
- Порт **6461** → Небезопасное соединение (без SSL)

### Вариант 2: Настройка доверия к сертификатам

Если необходимо использовать SSL, но возникают проблемы с сертификатами, настройте `RemoteServer` для доверия всем сертификатам:

```java
RemoteServer server = new RemoteServer("localhost", 6460, "admin", "admin");
server.setTrustAll(true); // Доверять всем сертификатам (включая самоподписанные)
RemoteServerController controller = new RemoteServerController(server, true);
```

### Вариант 3: Проверка настроек сервера

Убедитесь, что сервер настроен правильно:

1. **Для SSL соединения (порт 6460)**:
   - Убедитесь, что SSL включен на сервере
   - Проверьте, что сертификат валиден
   - Проверьте настройки файрвола

2. **Для небезопасного соединения (порт 6461)**:
   - Убедитесь, что небезопасное соединение включено на сервере
   - Проверьте настройки файрвола

## Изменения в SDK

В версии 1.3.6 добавлена автоматическая поддержка небезопасного соединения:

- `RemoteServerController` теперь автоматически определяет тип соединения по порту
- Порт 6461 → небезопасное соединение (без SSL)
- Порт 6460 (и другие) → SSL соединение

Это решает проблему несоответствия типов соединения между клиентом и сервером.

## Примеры использования

### Пример 1: Безопасное SSL соединение

```java
Log.start();
RemoteServer server = new RemoteServer("localhost", RemoteServer.DEFAULT_PORT, "admin", "admin");
server.setTrustAll(true); // Для самоподписанных сертификатов
RemoteServerController controller = new RemoteServerController(server, true);
controller.connect();
controller.login();
// ... работа с сервером ...
controller.disconnect();
```

### Пример 2: Небезопасное соединение (для тестирования)

```java
Log.start();
RemoteServer server = new RemoteServer("localhost", RemoteServer.DEFAULT_NON_SECURE_PORT, "admin", "admin");
RemoteServerController controller = new RemoteServerController(server, true);
controller.connect();
controller.login();
// ... работа с сервером ...
controller.disconnect();
```

## Рекомендации

1. **Для продакшена**: Всегда используйте SSL соединение (порт 6460) с валидными сертификатами
2. **Для разработки/тестирования**: Можно использовать небезопасное соединение (порт 6461) для упрощения настройки
3. **При проблемах с SSL**: Проверьте логи сервера для получения более детальной информации об ошибке

## Дополнительная информация

- Документация по SSL: `docs/TROUBLESHOOTING.md`
- Примеры подключения: `examples/SimpleConnectionExample.java`
- Константы портов: `RemoteServer.DEFAULT_PORT` (6460) и `RemoteServer.DEFAULT_NON_SECURE_PORT` (6461)

