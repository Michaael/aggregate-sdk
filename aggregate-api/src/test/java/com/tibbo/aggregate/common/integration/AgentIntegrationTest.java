package com.tibbo.aggregate.common.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.agent.Agent;
import com.tibbo.aggregate.common.agent.AgentContext;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Интеграционные тесты для работы с агентами.
 * 
 * <p>Эти тесты проверяют создание, подключение и работу агентов на реальном AggreGate сервере.</p>
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
@DisplayName("Agent Integration Tests")
class AgentIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("Test agent creation and connection")
    void testAgentCreationAndConnection() throws Exception {
        // Создаем агента
        RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
        AgentContext agentContext = new AgentContext(server, "testAgent", true);
        Agent agent = new Agent(agentContext, false, false, 0);
        
        try {
            // Подключаем агента
            agent.connect();
            
            // Проверяем, что агент подключен
            AgentContext agentCtx = agent.getContext();
            assertNotNull(agentCtx, "Agent context should not be null");
            
            // Проверяем корневой контекст
            Context rootContext = agentCtx;
            assertNotNull(rootContext, "Root context should not be null");
            
        } finally {
            // Отключаем агента
            agent.disconnect();
        }
    }

    @Test
    @DisplayName("Test agent context manager")
    void testAgentContextManager() throws Exception {
        RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
        AgentContext agentContext = new AgentContext(server, "testAgent2", true);
        Agent agent = new Agent(agentContext, false, false, 0);
        
        try {
            agent.connect();
            
            AgentContext agentCtx = agent.getContext();
            assertNotNull(agentCtx, "Agent context should be accessible");
            
            Context rootContext = agentCtx;
            assertNotNull(rootContext, "Root context should be accessible");
            
            // Проверяем, что контекст имеет путь
            String path = rootContext.getPath();
            assertNotNull(path, "Context path should not be null");
            assertTrue(path.length() > 0, "Context path should not be empty");
            
        } finally {
            agent.disconnect();
        }
    }

    @Test
    @DisplayName("Test agent can access server context")
    void testAgentCanAccessServerContext() throws Exception {
        RemoteServer server = new RemoteServer(serverHost, serverPort, username, password);
        AgentContext agentContext = new AgentContext(server, "testAgent3", true);
        Agent agent = new Agent(agentContext, false, false, 0);
        
        try {
            agent.connect();
            
            AgentContext agentCtx = agent.getContext();
            Context rootContext = agentCtx;
            
            // Проверяем, что можем получить доступ к контексту
            assertNotNull(rootContext, "Root context should be accessible");
            
            // Проверяем, что контекст имеет менеджер
            ContextManager<?> contextManager = rootContext.getContextManager();
            assertNotNull(contextManager, "Context manager should be accessible");
            
        } finally {
            agent.disconnect();
        }
    }
}

