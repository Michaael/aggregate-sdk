package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Тест для чтения контекстов users.admin.* с сервера AggreGate.
 * 
 * <p>Этот тест подключается к серверу и читает все дочерние контексты
 * из users.admin, выводя подробную информацию о каждом.</p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "examples;aggregate-api/build/libs/aggregate-api.jar;libs/*" examples.ReadUsersAdminContextsTest [host] [port] [username] [password]
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class ReadUsersAdminContextsTest {
    
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
        System.out.println("Reading users.admin.* contexts");
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
            
            // Получение контекста users.admin
            System.out.println("Looking for users.admin context...");
            Context usersAdminContext = contextManager.get("users.admin");
            
            if (usersAdminContext == null) {
                // Попробуем альтернативный путь
                System.out.println("  Trying alternative path...");
                Context rootContext = contextManager.getRoot();
                Context usersContext = rootContext.getChild("users");
                if (usersContext != null) {
                    usersAdminContext = usersContext.getChild("admin");
                }
            }
            
            if (usersAdminContext == null) {
                System.out.println("✗ users.admin context not found");
                System.out.println();
                System.out.println("This is normal if:");
                System.out.println("  - The server doesn't have this context structure");
                System.out.println("  - The user doesn't have permissions to access it");
                System.out.println("  - The context path is different on this server");
                return;
            }
            
            System.out.println("✓ Found users.admin context");
            System.out.println("  Path: " + usersAdminContext.getPath());
            System.out.println("  Name: " + usersAdminContext.getName());
            
            String description = usersAdminContext.getDescription();
            if (description != null && !description.isEmpty()) {
                System.out.println("  Description: " + description);
            }
            
            String type = usersAdminContext.getType();
            if (type != null && !type.isEmpty()) {
                System.out.println("  Type: " + type);
            }
            System.out.println();
            
            // Получение дочерних контекстов (users.admin.*)
            System.out.println("Reading child contexts (users.admin.*)...");
            System.out.println("----------------------------------------");
            
            try {
                java.util.List<Context> children = usersAdminContext.getChildren();
                
                if (children == null || children.isEmpty()) {
                    System.out.println("  No child contexts found in users.admin");
                    System.out.println();
                } else {
                    System.out.println("✓ Found " + children.size() + " child contexts:");
                    System.out.println();
                    
                    int index = 1;
                    int successCount = 0;
                    int errorCount = 0;
                    
                    for (Context child : children) {
                        try {
                            System.out.println("[" + index + "] " + child.getName());
                            System.out.println("    Path: " + child.getPath());
                            
                            // Безопасное получение описания
                            try {
                                String childDescription = child.getDescription();
                                if (childDescription != null && !childDescription.isEmpty()) {
                                    System.out.println("    Description: " + childDescription);
                                }
                            } catch (Exception e) {
                                System.out.println("    Description: (not available)");
                            }
                            
                            // Безопасное получение типа
                            try {
                                String childType = child.getType();
                                if (childType != null && !childType.isEmpty()) {
                                    System.out.println("    Type: " + childType);
                                }
                            } catch (Exception e) {
                                System.out.println("    Type: (not available)");
                            }
                            
                            // Безопасное получение группы
                            try {
                                String childGroup = child.getGroup();
                                if (childGroup != null && !childGroup.isEmpty()) {
                                    System.out.println("    Group: " + childGroup);
                                }
                            } catch (Exception e) {
                                // Игнорируем ошибки получения группы
                            }
                            
                            // Проверяем, есть ли у дочернего контекста свои дочерние контексты
                            try {
                                java.util.List<Context> grandChildren = child.getChildren();
                                if (grandChildren != null && !grandChildren.isEmpty()) {
                                    System.out.println("    Children: " + grandChildren.size());
                                }
                            } catch (Exception e) {
                                // Игнорируем ошибки при получении дочерних контекстов
                            }
                            
                            successCount++;
                            
                        } catch (Exception e) {
                            System.out.println("[" + index + "] " + child.getName() + " - ERROR: " + e.getMessage());
                            errorCount++;
                        }
                        
                        System.out.println();
                        index++;
                    }
                    
                    System.out.println("Summary:");
                    System.out.println("  Successfully read: " + successCount);
                    if (errorCount > 0) {
                        System.out.println("  Errors: " + errorCount);
                    }
                    
                    System.out.println("========================================");
                    System.out.println("Successfully read " + children.size() + " contexts from users.admin.*");
                    System.out.println("========================================");
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error reading child contexts: " + e.getMessage());
                e.printStackTrace();
            }
            
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

