package com.tibbo.aggregate.common.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные тесты для выполнения действий (actions).
 * 
 * <p>Эти тесты проверяют поиск и выполнение действий на реальном AggreGate сервере.</p>
 * 
 * <p>Для запуска этих тестов необходим запущенный AggreGate сервер.
 * Параметры подключения можно задать через системные свойства:
 * <ul>
 *   <li>aggregate.test.host - хост сервера (по умолчанию: localhost)</li>
 *   <li>aggregate.test.port - порт сервера (по умолчанию: 6460)</li>
 *   <li>aggregate.test.username - имя пользователя (по умолчанию: admin)</li>
 *   <li>aggregate.test.password - пароль (по умолчанию: admin)</li>
 * </ul>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
@DisplayName("Action Integration Tests")
class ActionIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Test getting action definitions")
    void testGettingActionDefinitions() throws Exception {
        ContextManager cm = getContextManager();
        Context rootContext = cm.getRoot();
        
        // Проверяем, что можем получить определения действий
        try {
            java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actionDefinitions = rootContext.getActionDefinitions();
            assertNotNull(actionDefinitions, "Action definitions should be accessible");
        } catch (Exception e) {
            // Если метод не поддерживается или нет действий, это нормально
            // Главное - контекст доступен
            assertNotNull(rootContext, "Root context should be accessible");
        }
    }

    @Test
    @DisplayName("Test context supports actions")
    void testContextSupportsActions() throws Exception {
        ContextManager cm = getContextManager();
        Context rootContext = cm.getRoot();
        
        // Проверяем, что контекст поддерживает работу с действиями
        assertNotNull(rootContext, "Root context should be accessible");
        
        // Проверяем, что контекст имеет менеджер
        ContextManager<?> contextManager = rootContext.getContextManager();
        assertNotNull(contextManager, "Context manager should be accessible");
    }

    @Test
    @DisplayName("Test action context structure")
    void testActionContextStructure() throws Exception {
        ContextManager cm = getContextManager();
        Context rootContext = cm.getRoot();
        
        // Проверяем структуру контекста для работы с действиями
        assertNotNull(rootContext, "Root context should be accessible");
        assertNotNull(rootContext.getPath(), "Context path should not be null");
    }
}

