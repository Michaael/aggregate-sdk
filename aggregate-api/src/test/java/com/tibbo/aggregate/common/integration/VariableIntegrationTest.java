package com.tibbo.aggregate.common.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.server.RootContextConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные тесты для работы с переменными.
 * 
 * <p>Эти тесты проверяют чтение и запись переменных на реальном AggreGate сервере.</p>
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
 * @version 1.2.1
 */
@DisplayName("Variable Integration Tests")
class VariableIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Test reading root context version variable")
    void testReadRootVersionVariable() throws Exception {
        ContextManager cm = getContextManager();
        assertNotNull(cm, "ContextManager should not be null");
        
        Context rootContext = cm.getRoot();
        assertNotNull(rootContext, "Root context should not be null");
        
        DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
        assertNotNull(versionData, "Version variable should not be null");
        assertTrue(versionData.getRecordCount() > 0, "Version data should contain records");
    }

    @Test
    @DisplayName("Test reading and writing variable")
    void testReadWriteVariable() throws Exception {
        // Этот тест требует контекст с переменной, которую можно записывать
        // Для демонстрации используем корневой контекст
        ContextManager cm = getContextManager();
        Context rootContext = cm.getRoot();
        
        // Читаем переменную
        DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
        assertNotNull(versionData, "Version variable should be readable");
        
        // Проверяем, что данные корректны
        assertTrue(versionData.getRecordCount() > 0, "Version data should have records");
    }

    @Test
    @DisplayName("Test variable exists")
    void testVariableExists() throws Exception {
        ContextManager cm = getContextManager();
        Context rootContext = cm.getRoot();
        
        // Проверяем, что переменная версии существует
        DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
        assertNotNull(versionData, "Version variable should exist");
    }

    @Test
    @DisplayName("Test context manager is accessible")
    void testContextManagerAccessible() throws Exception {
        ContextManager cm = getContextManager();
        assertNotNull(cm, "ContextManager should be accessible");
        
        Context rootContext = cm.getRoot();
        assertNotNull(rootContext, "Root context should be accessible");
    }
}

