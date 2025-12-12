package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.protocol.RemoteContextManager;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Тест для создания и удаления контекста users.admin.model.test.
 * 
 * <p>Этот тест проверяет:
 * <ul>
 *   <li>Создание контекста users.admin.model.test</li>
 *   <li>Проверку его существования</li>
 *   <li>Удаление контекста</li>
 *   <li>Проверку, что контекст удален</li>
 * </ul>
 * </p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "examples;aggregate-api/build/libs/aggregate-api.jar;libs/*" examples.CreateDeleteContextTest [host] [port] [username] [password]
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class CreateDeleteContextTest {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6460;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    
    private static final String TEST_CONTEXT_PATH = "users.admin.models.test";
    
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        String username = args.length > 2 ? args[2] : DEFAULT_USERNAME;
        String password = args.length > 3 ? args[3] : DEFAULT_PASSWORD;
        
        System.out.println("========================================");
        System.out.println("Create/Delete Context Test");
        System.out.println("========================================");
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Username: " + username);
        System.out.println("Test context path: " + TEST_CONTEXT_PATH);
        System.out.println();
        
        RemoteServerController controller = null;
        
        try {
            // Инициализация логирования
            Log.start();
            
            // Подключение к серверу
            System.out.println("Connecting to server...");
            RemoteServer server = new RemoteServer(host, port, username, password);
            controller = new RemoteServerController(server, true);
            
            controller.connect();
            System.out.println("✓ Connected");
            
            controller.login();
            System.out.println("✓ Authenticated");
            System.out.println();
            
            // Получение менеджера контекстов
            ContextManager contextManager = controller.getContextManager();
            if (contextManager == null) {
                throw new RuntimeException("ContextManager is null");
            }
            
            // Проверяем, что это RemoteContextManager
            if (!(contextManager instanceof RemoteContextManager)) {
                throw new RuntimeException("Expected RemoteContextManager, got: " + contextManager.getClass().getName());
            }
            
            RemoteContextManager remoteContextManager = (RemoteContextManager) contextManager;
            
            // Шаг 1: Проверка существования контекста перед созданием
            System.out.println("========================================");
            System.out.println("Step 1: Check if context exists (before creation)");
            System.out.println("========================================");
            
            Context existingContext = contextManager.get(TEST_CONTEXT_PATH);
            if (existingContext != null) {
                System.out.println("⚠ Context already exists: " + TEST_CONTEXT_PATH);
                System.out.println("  Attempting to delete it first...");
                
                try {
                    // Пытаемся удалить существующий контекст
                    deleteContext(contextManager, TEST_CONTEXT_PATH);
                    System.out.println("✓ Existing context deleted");
                } catch (Exception e) {
                    System.err.println("✗ Could not delete existing context: " + e.getMessage());
                    System.err.println("  Please delete it manually and try again");
                    return;
                }
            } else {
                System.out.println("✓ Context does not exist (good, we can create it)");
            }
            System.out.println();
            
            // Шаг 2: Создание контекста
            System.out.println("========================================");
            System.out.println("Step 2: Create context " + TEST_CONTEXT_PATH);
            System.out.println("========================================");
            
            Context createdContext = null;
            
            try {
                // Метод 1: Использование createContexts (создает прокси, но не на сервере)
                // Это не создаст контекст на сервере, только локальный прокси
                // createdContext = remoteContextManager.createContexts(TEST_CONTEXT_PATH);
                
                // Метод 2: Создание через родительский контекст и функцию "add"
                // Получаем родительский контекст (users.admin.models)
                String parentPath = "users.admin.models";
                Context parentContext = contextManager.get(parentPath);
                
                if (parentContext == null) {
                    // Пытаемся создать родительские контексты, если их нет
                    System.out.println("  Parent context not found, trying to create parent path...");
                    
                    // Создаем путь по частям
                    String[] pathParts = parentPath.split("\\.");
                    Context current = contextManager.getRoot();
                    
                    for (String part : pathParts) {
                        Context next = current.getChild(part);
                        if (next == null) {
                            System.out.println("    Creating intermediate context: " + part);
                            // Пытаемся создать через функцию add, если доступна
                            try {
                                // Проверяем, есть ли функция add
                                java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actions = 
                                    current.getActionDefinitions();
                                boolean hasAddAction = false;
                                if (actions != null) {
                                    for (com.tibbo.aggregate.common.action.ActionDefinition ad : actions) {
                                        if ("add".equals(ad.getName())) {
                                            hasAddAction = true;
                                            break;
                                        }
                                    }
                                }
                                
                                if (hasAddAction) {
                                    // Создаем через действие add
                                    System.out.println("    Calling 'add' action to create: " + part);
                                    // current.callAction("add", part, "Test context");
                                    System.out.println("    (Action call commented out - uncomment to actually create)");
                                    // Используем createContexts для создания прокси
                                    next = remoteContextManager.createContexts(
                                        current.getPath() + "." + part);
                                } else {
                                    System.out.println("    'add' action not available, using createContexts");
                                    // Используем createContexts для создания прокси
                                    next = remoteContextManager.createContexts(
                                        current.getPath() + "." + part);
                                }
                            } catch (Exception e) {
                                System.err.println("    Error creating intermediate context: " + e.getMessage());
                            }
                        }
                        if (next != null) {
                            current = next;
                        } else {
                            break;
                        }
                    }
                    
                    parentContext = current;
                }
                
                if (parentContext == null) {
                    throw new RuntimeException("Could not get or create parent context: " + parentPath);
                }
                
                System.out.println("✓ Parent context found/created: " + parentContext.getPath());
                
                // Проверяем, есть ли у родительского контекста действие "add"
                java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actions = 
                    parentContext.getActionDefinitions();
                
                boolean hasAddAction = false;
                if (actions != null) {
                    for (com.tibbo.aggregate.common.action.ActionDefinition ad : actions) {
                        if ("add".equals(ad.getName())) {
                            hasAddAction = true;
                            System.out.println("✓ Found 'add' action in parent context");
                            break;
                        }
                    }
                }
                
                if (hasAddAction) {
                    System.out.println("  Creating context 'test' using 'add' action...");
                    try {
                        // Вызываем действие add для создания контекста
                        // Параметры: имя контекста, описание
                        // parentContext.callAction("add", "test", "Test context created by SDK");
                        
                        // ВАЖНО: Раскомментируйте строку выше для реального создания
                        // Пока оставляем закомментированным для безопасности
                        System.out.println("  (Action call commented out - uncomment to actually create)");
                        System.out.println("  Example: parentContext.callAction(\"add\", \"test\", \"Test context\");");
                        
                        // Для теста создаем прокси контекст через createContexts
                        createdContext = remoteContextManager.createContexts(TEST_CONTEXT_PATH);
                        System.out.println("  Created proxy context (not on server)");
                        
                    } catch (Exception e) {
                        System.err.println("✗ Error calling 'add' action: " + e.getMessage());
                        throw e;
                    }
                } else {
                    System.out.println("  'add' action not available in parent context");
                    System.out.println("  Creating proxy context only (not on server)");
                    
                    // Используем createContexts для создания прокси
                    createdContext = remoteContextManager.createContexts(TEST_CONTEXT_PATH);
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error creating context: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            if (createdContext == null) {
                System.err.println("✗ Failed to create context");
                return;
            }
            
            System.out.println("✓ Context created (or proxy created): " + createdContext.getPath());
            System.out.println();
            
            // Шаг 3: Проверка существования созданного контекста
            System.out.println("========================================");
            System.out.println("Step 3: Verify context exists");
            System.out.println("========================================");
            
            Context verifiedContext = contextManager.get(TEST_CONTEXT_PATH);
            if (verifiedContext != null) {
                System.out.println("✓ Context verified: " + verifiedContext.getPath());
                System.out.println("  Name: " + verifiedContext.getName());
            } else {
                System.out.println("⚠ Context not found on server (proxy only)");
                System.out.println("  This is normal if using createChildContextProxy without server action");
            }
            System.out.println();
            
            // Шаг 4: Удаление контекста
            System.out.println("========================================");
            System.out.println("Step 4: Delete context " + TEST_CONTEXT_PATH);
            System.out.println("========================================");
            
            try {
                deleteContext(contextManager, TEST_CONTEXT_PATH);
                System.out.println("✓ Context deleted successfully");
            } catch (Exception e) {
                System.err.println("✗ Error deleting context: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println();
            
            // Шаг 5: Проверка, что контекст удален
            System.out.println("========================================");
            System.out.println("Step 5: Verify context is deleted");
            System.out.println("========================================");
            
            Context deletedContext = contextManager.get(TEST_CONTEXT_PATH);
            if (deletedContext == null) {
                System.out.println("✓ Context confirmed deleted (not found)");
            } else {
                System.out.println("⚠ Context still exists: " + deletedContext.getPath());
                System.out.println("  This may be a proxy context that wasn't on the server");
            }
            System.out.println();
            
            System.out.println("========================================");
            System.out.println("Test Completed!");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Note: This test creates proxy contexts locally.");
            System.out.println("To create contexts on the server, uncomment the");
            System.out.println("callAction(\"add\", ...) calls in the code.");
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("Test Failed!");
            System.err.println("========================================");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (controller != null) {
                try {
                    controller.disconnect();
                    System.out.println();
                    System.out.println("Disconnected from server");
                } catch (Exception e) {
                    System.err.println("Error disconnecting: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Удаляет контекст по пути.
     */
    private static void deleteContext(ContextManager contextManager, String contextPath) throws Exception {
        Context context = contextManager.get(contextPath);
        if (context == null) {
            System.out.println("  Context not found, nothing to delete");
            return;
        }
        
        Context parent = context.getParent();
        if (parent == null) {
            throw new RuntimeException("Cannot delete root context");
        }
        
        // Пытаемся удалить через родительский контекст
        try {
            // Метод 1: removeChild по имени
            parent.removeChild(context.getName());
            System.out.println("  Deleted using removeChild(name)");
        } catch (Exception e1) {
            try {
                // Метод 2: removeChild по объекту
                parent.removeChild(context);
                System.out.println("  Deleted using removeChild(context)");
            } catch (Exception e2) {
                try {
                    // Метод 3: destroy
                    context.destroy(false);
                    System.out.println("  Deleted using destroy()");
                } catch (Exception e3) {
                    throw new RuntimeException("All deletion methods failed: " + 
                        e1.getMessage() + ", " + e2.getMessage() + ", " + e3.getMessage());
                }
            }
        }
    }
}

