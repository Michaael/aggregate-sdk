# AggreGate SDK

[![Build Status](https://github.com/tibbo/aggregate-sdk/workflows/CI/badge.svg)](https://github.com/tibbo/aggregate-sdk/actions)
[![License](https://img.shields.io/badge/license-Proprietary-blue.svg)](LICENSE)

**AggreGate SDK** - это набор инструментов и библиотек для разработки приложений на платформе AggreGate. SDK предоставляет API для создания агентов, драйверов устройств, плагинов сервера и клиентских приложений.

## 📋 Содержание

- [Возможности](#возможности)
- [Требования](#требования)
- [Установка](#установка)
- [Быстрый старт](#быстрый-старт)
- [Структура проекта](#структура-проекта)
- [Примеры использования](#примеры-использования)
- [Документация](#документация)
- [Сборка проекта](#сборка-проекта)
- [Тестирование](#тестирование)
- [Вклад в проект](#вклад-в-проект)
- [Лицензия](#лицензия)

## ✨ Возможности

- **API для агентов** - создание агентов для подключения устройств к серверу AggreGate
- **API для драйверов** - разработка драйверов устройств
- **API для плагинов** - создание серверных плагинов
- **Widget API** - разработка пользовательских виджетов
- **Remote Server API** - удаленное управление сервером AggreGate
- **Примеры кода** - готовые примеры для всех типов приложений

## 🔧 Требования

- **Java**: JDK 8 или выше
- **Gradle**: 8.5 или выше (используется Gradle Wrapper)
- **Операционная система**: Windows, Linux, macOS

## 📦 Установка

### Клонирование репозитория

```bash
git clone https://github.com/tibbo/aggregate-sdk.git
cd aggregate-sdk
```

### Сборка проекта

```bash
# Использование Gradle Wrapper (рекомендуется)
./gradlew build

# Или на Windows
gradlew.bat build
```

### Установка в локальный Maven репозиторий

```bash
./gradlew publishToMavenLocal
```

## 🚀 Быстрый старт

### Пример 1: Получение версии сервера

```java
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

public class GetServerVersion {
    public static void main(String[] args) {
        try {
            Log.start();
            
            RemoteServer rls = new RemoteServer("localhost", 
                RemoteServer.DEFAULT_PORT, "admin", "admin");
            RemoteServerController rlc = new RemoteServerController(rls, true);
            
            rlc.connect();
            rlc.login();
            
            ContextManager cm = rlc.getContextManager();
            Context rootContext = cm.getRoot();
            DataTable versionData = rootContext.getVariable(
                RootContextConstants.V_VERSION);
            String serverVersion = versionData.rec().getString(
                RootContextConstants.VF_VERSION_VERSION);
            
            System.out.println("Server version: " + serverVersion);
            rlc.disconnect();
        } catch (Exception ex) {
            Log.TEST.error("Failed to fetch server version", ex);
        }
    }
}
```

### Пример 2: Создание агента

```java
import com.tibbo.aggregate.common.agent.Agent;
import com.tibbo.aggregate.common.agent.AgentContext;
import com.tibbo.aggregate.common.protocol.RemoteServer;

public class SimpleAgent {
    public static void main(String[] args) {
        // Настройка подключения к серверу
        RemoteServer server = new RemoteServer(
            "localhost", 
            RemoteServer.DEFAULT_PORT, 
            "admin", 
            "admin"
        );
        
        // Создание контекста агента
        AgentContext context = new AgentContext(server, "myAgent", true);
        
        // Создание и запуск агента
        Agent agent = new Agent(context, false, false, 0);
        
        try {
            agent.connect();
            // Агент работает...
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            agent.disconnect();
        }
    }
}
```

Полный пример с настройками и событиями см. в модуле `demo-agent`:
- [DemoAgent.java](demo-agent/src/main/java/examples/agent/DemoAgent.java)

### Пример 3: Управление пользователями

```java
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;

// Создание нового пользователя
ContextManager cm = rlc.getContextManager();
cm.getRoot().callFunction("register", "newUser", "password", "password");

// Получение контекста пользователя
Context userContext = cm.get(ContextUtils.userContextPath("newUser"));

// Изменение информации о пользователе
userContext.setVariableField("childInfo", "email", "user@example.com", null);
```

Полный пример см. в модуле `demo-api`:
- [ManageUsers.java](demo-api/src/main/java/examples/api/ManageUsers.java)

### Пример 4: Работа с устройствами

```java
// Создание устройства
Context deviceContext = ManageDevices.createDeviceAccount(
    "admin", 
    "myDevice", 
    "Device Description", 
    "com.tibbo.linkserver.plugin.device.virtual", 
    cm
);

// Чтение переменной устройства
DataTable value = deviceContext.getVariable("setting");

// Выполнение операции устройства
DataTable result = deviceContext.callFunction("operation", 123);
```

Полный пример см. в модуле `demo-api`:
- [ManageDevices.java](demo-api/src/main/java/examples/api/ManageDevices.java)

## 📁 Структура проекта

```
aggregate-sdk/
├── aggregate-api/          # Основная API библиотека
├── widget-api/             # API для виджетов
├── demo-agent/             # Пример агента
├── demo-api/               # Примеры использования API
├── demo-driver/            # Пример драйвера устройства
├── demo-plugin/            # Пример серверного плагина
├── context-demo-web-app/   # Пример веб-приложения
├── buildSrc/               # Gradle build scripts
├── docs/                   # Сгенерированная JavaDoc документация
└── gradle/                 # Gradle wrapper
```

## 📚 Примеры использования

### Дополнительные примеры

Дополнительные примеры использования доступны в папке [`examples/`](examples/):
- [SimpleConnectionExample.java](examples/SimpleConnectionExample.java) - Простое подключение к серверу
- [ReadWriteVariableExample.java](examples/ReadWriteVariableExample.java) - Работа с переменными
- [README с примерами](examples/README.md) - Подробное описание примеров

### Демонстрационные модули

Проект содержит несколько демонстрационных модулей:

### 1. Demo Agent (`demo-agent`)
Пример создания агента, который подключается к серверу и отправляет события.

**Запуск:**
```bash
./gradlew :demo-agent:run
```

### 2. Demo API (`demo-api`)
Примеры использования Remote Server API:
- `GetServerVersion` - получение версии сервера
- `ManageUsers` - управление пользователями
- `ManageDevices` - управление устройствами
- `ExecuteAction` - выполнение действий

**Запуск:**
```bash
./gradlew :demo-api:run
```

### 3. Demo Driver (`demo-driver`)
Пример создания драйвера устройства.

**Запуск:**
```bash
./gradlew :demo-driver:run
```

### 4. Demo Plugin (`demo-plugin`)
Пример создания серверного плагина.

## 📖 Документация

### Основная документация

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Архитектура проекта
- **[DEVELOPMENT.md](docs/DEVELOPMENT.md)** - Руководство по разработке
- **[API_GUIDE.md](docs/API_GUIDE.md)** - Руководство по использованию API
- **[TROUBLESHOOTING.md](docs/TROUBLESHOOTING.md)** - Решение проблем
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - Руководство по вкладу
- **[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)** - Кодекс поведения
- **[SECURITY.md](SECURITY.md)** - Политика безопасности
- **[CHANGELOG.md](CHANGELOG.md)** - История изменений

### JavaDoc

Сгенерированная JavaDoc документация находится в папке `docs/`. Для генерации документации:

```bash
./gradlew javadoc
```

Документация будет доступна в `build/docs/javadoc/`.

### Онлайн документация

Полная документация доступна на [официальном сайте AggreGate](https://aggregate.tibbo.com/docs/).

## 🔨 Сборка проекта

### Полная сборка

```bash
./gradlew clean build
```

### Сборка без тестов

```bash
./gradlew build -x test
```

### Сборка конкретного модуля

```bash
./gradlew :aggregate-api:build
```

### Публикация артефактов

```bash
./gradlew publish
```

## 🧪 Тестирование

### Запуск всех тестов

```bash
./gradlew test
```

### Запуск тестов конкретного модуля

```bash
./gradlew :demo-agent:test
```

### Просмотр отчетов о тестировании

После выполнения тестов отчеты доступны в:
```
build/reports/tests/test/index.html
```

### Покрытие кода (Code Coverage)

Для генерации отчетов о покрытии кода:

```bash
# Генерация отчетов для всех модулей
./gradlew jacocoRootReport

# Просмотр HTML отчета
open build/reports/jacoco/jacocoRootReport/html/index.html
```

Отчеты о покрытии доступны в:
- HTML: `build/reports/jacoco/jacocoRootReport/html/index.html`
- XML: `build/reports/jacoco/jacocoRootReport/jacocoRootReport.xml`

Минимальное покрытие кода установлено на 30%.

## 🛠️ Разработка

### Настройка IDE

Проект использует Gradle, поэтому можно импортировать в любую IDE, поддерживающую Gradle:

**IntelliJ IDEA:**
1. File → Open → выберите папку проекта
2. Gradle синхронизируется автоматически

**Eclipse:**
```bash
./gradlew eclipse
```

### Стиль кода

Проект следует стандартным Java conventions. Рекомендуется использовать:
- Java 8+ features (lambda, streams, Optional)
- Правильное использование generics
- Избегать raw types
- Документировать публичные API

### Коммиты

При создании коммитов следуйте соглашениям:
- Используйте понятные сообщения коммитов
- Один коммит = одно логическое изменение
- Тесты должны проходить перед коммитом

## 🤝 Вклад в проект

Мы приветствуем вклад в развитие проекта! Пожалуйста:

1. Создайте форк проекта
2. Создайте ветку для вашей функции (`git checkout -b feature/AmazingFeature`)
3. Зафиксируйте изменения (`git commit -m 'Add some AmazingFeature'`)
4. Отправьте в ветку (`git push origin feature/AmazingFeature`)
5. Откройте Pull Request

## 📝 Лицензия

Этот проект является проприетарным программным обеспечением. См. файл [LICENSE](LICENSE) для подробностей.

## 📞 Поддержка

- **Документация**: [https://aggregate.digital/docs/](https://aggregate.digital/docs/)
- **Email поддержка**: aggregate-support@tibbo.com
- **Issues**: [GitHub Issues](https://github.com/Michaael/AggreGate-SDK-CE/issues)
- **Telegram** [Telegram](https://t.me/IoT_tips)


## 📋 Дополнительная документация

- [Архитектура проекта](ARCHITECTURE.md) - Подробное описание архитектуры
- [Руководство по вкладу](CONTRIBUTING.md) - Как внести вклад в проект
- [Кодекс поведения](CODE_OF_CONDUCT.md) - Стандарты поведения в сообществе
- [История изменений](CHANGELOG.md) - История версий и изменений
- [Примеры использования](examples/README.md) - Дополнительные примеры кода

## 🙏 Благодарности

Спасибо всем разработчикам и пользователям, которые вносят вклад в развитие AggreGate SDK!

---

**Версия SDK**: 1.0  
**Последнее обновление**: 2025

