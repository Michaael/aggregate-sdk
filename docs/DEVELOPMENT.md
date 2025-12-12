# Руководство по разработке

Это руководство поможет вам настроить среду разработки для работы с AggreGate SDK.

## Требования

- **Java**: JDK 8 или выше
- **Gradle**: 8.5 (используется Gradle Wrapper)
- **IDE**: IntelliJ IDEA, Eclipse или VS Code
- **Git**: для работы с репозиторием

## Настройка среды разработки

### IntelliJ IDEA

1. **Импорт проекта**:
   - File → Open → выберите папку проекта
   - Gradle синхронизируется автоматически

2. **Настройки стиля кода**:
   - Проект использует настройки из `.intellij/codeStyles/`
   - Настройки применяются автоматически при импорте

3. **Запуск тестов**:
   - Правый клик на тестовом классе → Run
   - Или используйте Gradle: `./gradlew test`

### Eclipse

1. **Импорт проекта**:
   ```bash
   ./gradlew eclipse
   ```
   Затем в Eclipse: File → Import → Existing Projects into Workspace

2. **Настройки**:
   - Используйте настройки из `.editorconfig`
   - Рекомендуется установить плагин EditorConfig

### VS Code

1. **Расширения**:
   - Java Extension Pack
   - Gradle for Java
   - EditorConfig for VS Code

2. **Настройки**:
   - Проект использует `.editorconfig` для форматирования
   - Настройки применяются автоматически

## Структура проекта

```
aggregate-sdk/
├── aggregate-api/          # Основная API библиотека
│   ├── src/main/java/      # Исходный код
│   └── src/test/java/      # Тесты
├── demo-*/                 # Примеры использования
├── buildSrc/               # Gradle build scripts
└── docs/                   # Документация
```

## Рабочий процесс разработки

### 1. Создание ветки

```bash
git checkout -b feature/my-feature
# или
git checkout -b fix/bug-description
```

### 2. Разработка

- Пишите код согласно стилю проекта
- Добавляйте тесты для нового функционала
- Обновляйте документацию при необходимости

### 3. Проверка перед коммитом

```bash
# Форматирование кода
./gradlew spotlessApply

# Проверка стиля
./gradlew checkstyleMain

# Запуск тестов
./gradlew test

# Проверка покрытия
./gradlew jacocoTestReport
```

### 4. Коммит

```bash
git add .
git commit -m "feat: add new feature"
```

Используйте [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` - новая функция
- `fix:` - исправление бага
- `docs:` - документация
- `refactor:` - рефакторинг
- `test:` - тесты
- `chore:` - обслуживание

### 5. Push и Pull Request

```bash
git push origin feature/my-feature
```

Создайте Pull Request через GitHub, используя шаблон.

## Тестирование

### Unit-тесты

```bash
# Все тесты
./gradlew test

# Конкретный модуль
./gradlew :aggregate-api:test

# Конкретный тест
./gradlew :aggregate-api:test --tests "TestAggreGateException"
```

### Покрытие кода

```bash
# Генерация отчетов
./gradlew jacocoRootReport

# Просмотр HTML отчета
open build/reports/jacoco/jacocoRootReport/html/index.html
```

### Интеграционные тесты

Интеграционные тесты требуют запущенного AggreGate сервера:

```bash
# Запуск интеграционных тестов
./gradlew test --tests "*IntegrationTest"
```

## Отладка

### IntelliJ IDEA

1. Установите breakpoint
2. Правый клик → Debug
3. Используйте Debug панель для навигации

### Логирование

Используйте класс `Log` для логирования:

```java
Log.TEST.info("Information message");
Log.TEST.error("Error message", exception);
```

## Сборка

### Локальная сборка

```bash
# Полная сборка
./gradlew clean build

# Без тестов
./gradlew build -x test

# Публикация в локальный Maven
./gradlew publishToMavenLocal
```

### Публикация

```bash
# Публикация артефактов
./gradlew publish
```

## Полезные команды

```bash
# Очистка
./gradlew clean

# Просмотр зависимостей
./gradlew dependencies

# Обновление зависимостей
./gradlew dependencyUpdates

# Проверка обновлений Gradle
./gradlew wrapper --gradle-version=8.5
```

## Решение проблем

### Проблемы с Gradle

```bash
# Очистка кэша Gradle
./gradlew clean --refresh-dependencies

# Удаление .gradle папки
rm -rf .gradle
```

### Проблемы с зависимостями

```bash
# Просмотр конфликтов
./gradlew dependencyInsight --dependency <dependency-name>
```

### Проблемы с тестами

- Убедитесь, что все зависимости установлены
- Проверьте версию Java
- Очистите build папку: `./gradlew clean`

## Дополнительные ресурсы

- [Gradle User Guide](https://docs.gradle.org/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Java Best Practices](https://google.github.io/styleguide/javaguide.html)

---

*Документ обновлен: 2024-12-XX*

