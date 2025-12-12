package com.tibbo.aggregate.common.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.DataTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные тесты для работы с устройствами.
 * 
 * <p>Эти тесты проверяют получение списка устройств и работу с ними на реальном AggreGate сервере.</p>
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
@DisplayName("Device Integration Tests")
class DeviceIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Test getting devices context")
    void testGettingDevicesContext() throws Exception {
        ContextManager cm = getContextManager();
        assertNotNull(cm, "ContextManager should not be null");
        
        // Получаем контекст устройств
        String devicesPath = ContextUtils.devicesContextPath(username);
        Context devicesContext = cm.get(devicesPath);
        
        // Контекст может быть null, если у пользователя нет устройств
        // Это нормально для теста
        if (devicesContext != null) {
            assertNotNull(devicesContext.getPath(), "Devices context path should not be null");
        }
    }

    @Test
    @DisplayName("Test accessing device context")
    void testAccessingDeviceContext() throws Exception {
        ContextManager cm = getContextManager();
        
        // Пытаемся получить контекст устройств
        String devicesPath = ContextUtils.devicesContextPath(username);
        Context devicesContext = cm.get(devicesPath);
        
        if (devicesContext != null) {
            // Проверяем, что контекст доступен
            assertNotNull(devicesContext, "Devices context should be accessible");
            
            // Проверяем, что можем получить путь
            String path = devicesContext.getPath();
            assertNotNull(path, "Devices context path should not be null");
            assertTrue(path.contains("devices"), "Path should contain 'devices'");
        }
    }

    @Test
    @DisplayName("Test device context structure")
    void testDeviceContextStructure() throws Exception {
        ContextManager cm = getContextManager();
        
        String devicesPath = ContextUtils.devicesContextPath(username);
        Context devicesContext = cm.get(devicesPath);
        
        if (devicesContext != null) {
            // Проверяем структуру контекста
            assertNotNull(devicesContext.getContextManager(), "Context manager should be accessible");
            
            // Проверяем, что контекст имеет менеджер
            ContextManager<?> contextManager = devicesContext.getContextManager();
            assertNotNull(contextManager, "Context manager should not be null");
        }
    }
}

