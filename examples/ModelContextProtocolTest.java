package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Комплексный тест протокола для работы с контекстом model.
 * 
 * <p>Этот тест проверяет:
 * <ul>
 *   <li>Чтение переменных из контекста model</li>
 *   <li>Запись переменных в контекст model</li>
 *   <li>Получение списка переменных, действий, событий</li>
 *   <li>Работу с дочерними контекстами</li>
 * </ul>
 * </p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "examples;aggregate-api/build/libs/aggregate-api.jar;libs/*" examples.ModelContextProtocolTest [host] [port] [username] [password]
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class ModelContextProtocolTest {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6460;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        String username = args.length > 2 ? args[2] : DEFAULT_USERNAME;
        String password = args.length > 3 ? args[3] : DEFAULT_PASSWORD;
        
        System.out.println("========================================");
        System.out.println("Model Context Protocol Test");
        System.out.println("========================================");
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Username: " + username);
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
            
            // Получение контекста model (или любого другого доступного)
            System.out.println("Looking for test context...");
            Context modelContext = null;
            
            // Пробуем разные возможные пути
            String[] possiblePaths = {"model", "system.model", "root.model", "system"};
            
            for (String path : possiblePaths) {
                try {
                    modelContext = contextManager.get(path);
                    if (modelContext != null) {
                        System.out.println("  Found context at path: " + path);
                        break;
                    }
                } catch (Exception e) {
                    // Продолжаем поиск
                }
            }
            
            // Если не нашли, пробуем через корневой контекст
            if (modelContext == null) {
                Context rootContext = contextManager.getRoot();
                for (String name : new String[]{"model", "system"}) {
                    try {
                        modelContext = rootContext.getChild(name);
                        if (modelContext != null) {
                            System.out.println("  Found context as child: " + name);
                            break;
                        }
                    } catch (Exception e) {
                        // Продолжаем поиск
                    }
                }
            }
            
            // Если все еще не нашли, используем корневой контекст
            if (modelContext == null) {
                System.out.println("  model/system context not found, using root context for testing");
                modelContext = contextManager.getRoot();
            }
            
            System.out.println("✓ Found model context");
            System.out.println("  Path: " + modelContext.getPath());
            System.out.println("  Name: " + modelContext.getName());
            System.out.println();
            
            // Тест 1: Получение списка переменных
            System.out.println("========================================");
            System.out.println("Test 1: Getting Variable Definitions");
            System.out.println("========================================");
            
            try {
                java.util.List<com.tibbo.aggregate.common.context.VariableDefinition> variableDefinitions = 
                    modelContext.getVariableDefinitions();
                
                if (variableDefinitions == null) {
                    System.out.println("  No variable definitions available");
                } else {
                    System.out.println("✓ Found " + variableDefinitions.size() + " variable definitions");
                    System.out.println();
                    
                    int readableCount = 0;
                    int writableCount = 0;
                    
                    System.out.println("  Sample variables (first 10):");
                    int count = Math.min(10, variableDefinitions.size());
                    for (int i = 0; i < count; i++) {
                        com.tibbo.aggregate.common.context.VariableDefinition vd = variableDefinitions.get(i);
                        String flags = "";
                        if (vd.isReadable()) {
                            flags += "R";
                            readableCount++;
                        }
                        if (vd.isWritable()) {
                            flags += "W";
                            writableCount++;
                        }
                        System.out.println("    [" + flags + "] " + vd.getName());
                    }
                    
                    if (variableDefinitions.size() > count) {
                        System.out.println("    ... and " + (variableDefinitions.size() - count) + " more");
                    }
                    
                    System.out.println();
                    System.out.println("  Summary:");
                    System.out.println("    Total: " + variableDefinitions.size());
                    System.out.println("    Readable: " + readableCount);
                    System.out.println("    Writable: " + writableCount);
                }
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("✗ Error getting variable definitions: " + e.getMessage());
                e.printStackTrace();
                System.out.println();
            }
            
            // Тест 2: Чтение переменных
            System.out.println("========================================");
            System.out.println("Test 2: Reading Variables");
            System.out.println("========================================");
            
            String[] standardVars = {
                AbstractContext.V_INFO,
                AbstractContext.V_VARIABLES,
                AbstractContext.V_CHILDREN,
                AbstractContext.V_ACTIONS,
                AbstractContext.V_EVENTS
            };
            
            int readSuccessCount = 0;
            int readFailCount = 0;
            
            for (String varName : standardVars) {
                try {
                    DataTable varData = modelContext.getVariable(varName);
                    if (varData != null) {
                        readSuccessCount++;
                        System.out.println("✓ " + varName);
                        System.out.println("    Records: " + varData.getRecordCount());
                        System.out.println("    Fields: " + varData.getFieldCount());
                    } else {
                        readFailCount++;
                        System.out.println("  " + varName + " - null");
                    }
                } catch (Exception e) {
                    readFailCount++;
                    System.out.println("  " + varName + " - ERROR: " + e.getMessage());
                }
            }
            
            System.out.println();
            System.out.println("  Summary:");
            System.out.println("    Successfully read: " + readSuccessCount);
            System.out.println("    Failed/not available: " + readFailCount);
            System.out.println();
            
            // Тест 3: Запись переменных (только демонстрация, без реальной записи)
            System.out.println("========================================");
            System.out.println("Test 3: Writing Variables (Demo)");
            System.out.println("========================================");
            
            try {
                java.util.List<com.tibbo.aggregate.common.context.VariableDefinition> writableVars = 
                    new java.util.ArrayList<>();
                
                java.util.List<com.tibbo.aggregate.common.context.VariableDefinition> allVars = 
                    modelContext.getVariableDefinitions();
                
                if (allVars != null) {
                    for (com.tibbo.aggregate.common.context.VariableDefinition vd : allVars) {
                        if (vd.isWritable()) {
                            writableVars.add(vd);
                        }
                    }
                }
                
                if (writableVars.isEmpty()) {
                    System.out.println("  No writable variables found");
                    System.out.println("  This is normal - many contexts have read-only variables");
                } else {
                    System.out.println("✓ Found " + writableVars.size() + " writable variables");
                    System.out.println();
                    System.out.println("  Sample writable variables (first 5):");
                    int count = Math.min(5, writableVars.size());
                    for (int i = 0; i < count; i++) {
                        System.out.println("    - " + writableVars.get(i).getName());
                    }
                    System.out.println();
                    System.out.println("  Note: Skipping actual write to avoid modifying server state");
                    System.out.println("  Example write syntax:");
                    System.out.println("    DataTable value = new SimpleDataTable(format);");
                    System.out.println("    value.addRecord().setValue(\"fieldName\", \"value\");");
                    System.out.println("    context.setVariable(\"varName\", value, null);");
                }
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("✗ Error checking writable variables: " + e.getMessage());
                System.out.println();
            }
            
            // Тест 4: Получение действий
            System.out.println("========================================");
            System.out.println("Test 4: Getting Actions");
            System.out.println("========================================");
            
            try {
                java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actions = 
                    modelContext.getActionDefinitions();
                
                if (actions == null || actions.isEmpty()) {
                    System.out.println("  No actions available");
                } else {
                    System.out.println("✓ Found " + actions.size() + " actions");
                    System.out.println();
                    System.out.println("  Sample actions (first 5):");
                    int count = Math.min(5, actions.size());
                    for (int i = 0; i < count; i++) {
                        com.tibbo.aggregate.common.action.ActionDefinition ad = actions.get(i);
                        System.out.println("    - " + ad.getName());
                    }
                    if (actions.size() > count) {
                        System.out.println("    ... and " + (actions.size() - count) + " more");
                    }
                }
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("✗ Error getting actions: " + e.getMessage());
                System.out.println();
            }
            
            // Тест 5: Получение событий
            System.out.println("========================================");
            System.out.println("Test 5: Getting Events");
            System.out.println("========================================");
            
            try {
                java.util.List<com.tibbo.aggregate.common.context.EventDefinition> events = 
                    modelContext.getEventDefinitions();
                
                if (events == null || events.isEmpty()) {
                    System.out.println("  No events available");
                } else {
                    System.out.println("✓ Found " + events.size() + " events");
                    System.out.println();
                    System.out.println("  Sample events (first 5):");
                    int count = Math.min(5, events.size());
                    for (int i = 0; i < count; i++) {
                        com.tibbo.aggregate.common.context.EventDefinition ed = events.get(i);
                        System.out.println("    - " + ed.getName());
                    }
                    if (events.size() > count) {
                        System.out.println("    ... and " + (events.size() - count) + " more");
                    }
                }
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("✗ Error getting events: " + e.getMessage());
                System.out.println();
            }
            
            // Тест 6: Получение дочерних контекстов
            System.out.println("========================================");
            System.out.println("Test 6: Getting Children Contexts");
            System.out.println("========================================");
            
            try {
                java.util.List<Context> children = modelContext.getChildren();
                
                if (children == null || children.isEmpty()) {
                    System.out.println("  No child contexts found");
                } else {
                    System.out.println("✓ Found " + children.size() + " child contexts");
                    System.out.println();
                    System.out.println("  Sample children (first 10):");
                    int count = Math.min(10, children.size());
                    for (int i = 0; i < count; i++) {
                        Context child = children.get(i);
                        System.out.println("    - " + child.getName() + " (" + child.getPath() + ")");
                    }
                    if (children.size() > count) {
                        System.out.println("    ... and " + (children.size() - count) + " more");
                    }
                }
                System.out.println();
                
            } catch (Exception e) {
                System.err.println("✗ Error getting children: " + e.getMessage());
                System.out.println();
            }
            
            System.out.println("========================================");
            System.out.println("All Tests Completed Successfully!");
            System.out.println("========================================");
            
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
}

