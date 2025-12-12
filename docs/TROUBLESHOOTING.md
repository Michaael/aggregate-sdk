# Решение проблем

Это руководство поможет вам решить распространенные проблемы при работе с AggreGate SDK.

## Проблемы со сборкой

### Ошибка: "Could not resolve all dependencies"

**Причина:** Проблемы с доступом к Maven репозиторию или сетевые проблемы.

**Решение:**
```bash
# Очистка кэша Gradle
./gradlew clean --refresh-dependencies

# Проверка доступности репозитория
curl https://store.aggregate.digital/repository/maven-public
```

### Ошибка: "Unsupported class file major version"

**Причина:** Несовместимость версий Java.

**Решение:**
- Убедитесь, что используется Java 8 или выше
- Проверьте версию: `java -version`
- Установите правильную версию JDK

### Ошибка компиляции: "package org.junit.jupiter does not exist"

**Причина:** Зависимости JUnit не синхронизированы.

**Решение:**
```bash
# Синхронизация зависимостей
./gradlew build --refresh-dependencies

# В IntelliJ IDEA: File → Invalidate Caches / Restart
```

## Проблемы с тестами

### Тесты не запускаются

**Решение:**
```bash
# Проверка конфигурации
./gradlew test --info

# Запуск конкретного теста
./gradlew :aggregate-api:test --tests "TestAggreGateException"
```

### Ошибка: "Test engine execution failed"

**Причина:** Конфликт версий JUnit или проблемы с конфигурацией.

**Решение:**
```bash
# Очистка и пересборка
./gradlew clean test
```

## Проблемы с подключением к серверу

### Ошибка: "Connection refused"

**Причины и решения:**
1. **Сервер не запущен**
   - Убедитесь, что AggreGate сервер запущен
   - Проверьте порт: по умолчанию 6460

2. **Неправильный адрес**
   - Проверьте адрес сервера: `localhost` или IP адрес
   - Проверьте файрвол

3. **Неправильные учетные данные**
   - Проверьте username и password
   - Убедитесь, что пользователь существует

### Ошибка: "Authentication failed"

**Решение:**
- Проверьте правильность username и password
- Убедитесь, что пользователь имеет необходимые права доступа
- Проверьте настройки безопасности сервера

### Ошибка: "DisconnectionException"

**Причина:** Разрыв соединения с сервером.

**Решение:**
```java
// Реализуйте автоматическое переподключение
while (!connected) {
    try {
        agent.connect();
        connected = true;
    } catch (DisconnectionException e) {
        Thread.sleep(5000); // Пауза перед повторной попыткой
    }
}
```

## Проблемы с IDE

### IntelliJ IDEA не видит классы

**Решение:**
1. File → Invalidate Caches / Restart
2. File → Sync Project with Gradle Files
3. Пересоберите проект: Build → Rebuild Project

### Eclipse не компилирует проект

**Решение:**
```bash
# Генерация Eclipse файлов
./gradlew eclipse

# В Eclipse: Project → Clean
```

### VS Code не находит классы

**Решение:**
1. Установите Java Extension Pack
2. Откройте Command Palette (Ctrl+Shift+P)
3. Выполните: "Java: Clean Java Language Server Workspace"
4. Перезагрузите окно

## Проблемы с производительностью

### Медленная сборка

**Решение:**
```bash
# Использование Gradle daemon
./gradlew build --daemon

# Увеличение памяти для Gradle
# В gradle.properties:
org.gradle.jvmargs=-Xmx4g -Xss512k
```

### Медленные тесты

**Решение:**
- Запускайте только необходимые тесты
- Используйте параллельное выполнение тестов
- Проверьте настройки JVM для тестов

## Проблемы с зависимостями

### Конфликты версий

**Решение:**
```bash
# Просмотр зависимостей
./gradlew dependencies

# Анализ конфликтов
./gradlew dependencyInsight --dependency <dependency-name>
```

### Устаревшие зависимости

**Решение:**
```bash
# Проверка обновлений
./gradlew dependencyUpdates

# Обновление через Dependabot (автоматически)
# Или вручную в buildSrc/src/main/java/Dependencies.kt
```

## Проблемы с форматированием кода

### Spotless не форматирует код

**Решение:**
```bash
# Применение форматирования
./gradlew spotlessApply

# Проверка форматирования
./gradlew spotlessCheck
```

### Конфликты стиля кода

**Решение:**
- Используйте `.editorconfig` для единообразия
- Настройте IDE согласно `.editorconfig`
- Используйте Spotless для автоматического форматирования

## Проблемы с покрытием кода

### JaCoCo не генерирует отчеты

**Решение:**
```bash
# Запуск тестов перед генерацией отчетов
./gradlew test jacocoTestReport

# Просмотр отчетов
open build/reports/jacoco/jacocoRootReport/html/index.html
```

### Низкое покрытие кода

**Решение:**
- Добавьте больше unit-тестов
- Проверьте, какие классы не покрыты тестами
- Используйте отчеты JaCoCo для анализа

## Проблемы с логированием

### Логи не отображаются

**Решение:**
```java
// Инициализация логирования
Log.start();

// Проверка конфигурации
// Файл logging.xml должен быть в classpath
```

### Слишком много логов

**Решение:**
- Настройте уровни логирования в `logging.xml`
- Используйте соответствующие категории логов
- Фильтруйте ненужные сообщения

## Получение помощи

Если проблема не решена:

1. **Проверьте документацию:**
   - [README.md](../README.md)
   - [DEVELOPMENT.md](DEVELOPMENT.md)
   - [API_GUIDE.md](API_GUIDE.md)

2. **Создайте Issue:**
   - Используйте шаблон [bug_report.md](../.github/ISSUE_TEMPLATE/bug_report.md)
   - Включите логи и описание проблемы

3. **Обратитесь в поддержку:**
   - Email: support@tibbo.com
   - Форум: https://forum.tibbo.com/

## Полезные команды

```bash
# Полная очистка и пересборка
./gradlew clean build

# Проверка конфигурации
./gradlew tasks

# Просмотр всех зависимостей
./gradlew dependencies > dependencies.txt

# Запуск с отладочной информацией
./gradlew build --debug

# Проверка версии Gradle
./gradlew --version
```

---

*Документ обновлен: 2024-12-XX*

