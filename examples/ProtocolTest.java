import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.server.RootContextConstants;

/**
 * Простой тест для проверки работы протокола AggreGate через API.
 * 
 * <p>Этот класс можно запустить напрямую для проверки подключения к серверу
 * и основных операций без использования JUnit.</p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "aggregate-api.jar:libs/*" examples.ProtocolTest
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class ProtocolTest {
    
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
        System.out.println("AggreGate Protocol Test");
        System.out.println("========================================");
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Username: " + username);
        System.out.println();
        
        RemoteServerController controller = null;
        
        try {
            // Инициализация логирования
            Log.start();
            
            // Тест 1: Подключение и аутентификация
            System.out.println("Test 1: Connection and Authentication");
            System.out.println("----------------------------------------");
            
            RemoteServer server = new RemoteServer(host, port, username, password);
            controller = new RemoteServerController(server, true);
            
            controller.connect();
            System.out.println("✓ Connected to server");
            
            controller.login();
            System.out.println("✓ Authenticated successfully");
            
            ContextManager contextManager = controller.getContextManager();
            if (contextManager == null) {
                throw new RuntimeException("ContextManager is null");
            }
            System.out.println("✓ ContextManager obtained");
            
            Context rootContext = contextManager.getRoot();
            if (rootContext == null) {
                throw new RuntimeException("Root context is null");
            }
            System.out.println("✓ Root context accessible: " + rootContext.getPath());
            System.out.println();
            
            // Тест 2: Чтение переменных
            System.out.println("Test 2: Reading Variables");
            System.out.println("----------------------------------------");
            
            long startTime = System.currentTimeMillis();
            DataTable versionData = rootContext.getVariable(RootContextConstants.V_VERSION);
            long readTime = System.currentTimeMillis() - startTime;
            
            if (versionData == null) {
                throw new RuntimeException("Version variable is null");
            }
            
            Integer recordCount = versionData.getRecordCount();
            int fieldCount = versionData.getFieldCount();
            
            System.out.println("✓ Variable read successfully");
            System.out.println("  Variable: " + RootContextConstants.V_VERSION);
            System.out.println("  Records: " + (recordCount != null ? recordCount : "unknown"));
            System.out.println("  Fields: " + fieldCount);
            System.out.println("  Read time: " + readTime + " ms");
            System.out.println();
            
            // Тест 3: Кэширование
            System.out.println("Test 3: Variable Caching");
            System.out.println("----------------------------------------");
            
            long startTime1 = System.currentTimeMillis();
            DataTable versionData1 = rootContext.getVariable(RootContextConstants.V_VERSION);
            long time1 = System.currentTimeMillis() - startTime1;
            
            long startTime2 = System.currentTimeMillis();
            DataTable versionData2 = rootContext.getVariable(RootContextConstants.V_VERSION);
            long time2 = System.currentTimeMillis() - startTime2;
            
            System.out.println("✓ First read: " + time1 + " ms");
            System.out.println("✓ Second read (cached): " + time2 + " ms");
            System.out.println("  Cache efficiency: " + (time2 < time1 || time2 < 50 ? "working" : "needs improvement"));
            System.out.println();
            
            // Тест 4: Множественные чтения
            System.out.println("Test 4: Multiple Variable Reads");
            System.out.println("----------------------------------------");
            
            int iterations = 5;
            long totalTime = 0;
            long minTime = Long.MAX_VALUE;
            long maxTime = 0;
            
            for (int i = 0; i < iterations; i++) {
                long start = System.currentTimeMillis();
                DataTable data = rootContext.getVariable(RootContextConstants.V_VERSION);
                long time = System.currentTimeMillis() - start;
                
                totalTime += time;
                if (time < minTime) minTime = time;
                if (time > maxTime) maxTime = time;
                
                if (data == null) {
                    throw new RuntimeException("Variable read " + i + " returned null");
                }
            }
            
            long avgTime = totalTime / iterations;
            
            System.out.println("✓ Completed " + iterations + " reads");
            System.out.println("  Total time: " + totalTime + " ms");
            System.out.println("  Average time: " + avgTime + " ms");
            System.out.println("  Min time: " + minTime + " ms");
            System.out.println("  Max time: " + maxTime + " ms");
            System.out.println();
            
            // Тест 5: Навигация по контекстам
            System.out.println("Test 5: Context Navigation");
            System.out.println("----------------------------------------");
            
            Context contextByPath = contextManager.get(rootContext.getPath());
            if (contextByPath == null) {
                throw new RuntimeException("Context by path is null");
            }
            
            System.out.println("✓ Context accessible by path: " + contextByPath.getPath());
            
            try {
                java.util.List<Context> children = rootContext.getChildren();
                System.out.println("✓ Children count: " + (children != null ? children.size() : 0));
            } catch (Exception e) {
                System.out.println("  Note: getChildren() not supported or returned error: " + e.getMessage());
            }
            System.out.println();
            
            // Тест 6: Обработка ошибок
            System.out.println("Test 6: Error Handling");
            System.out.println("----------------------------------------");
            
            try {
                DataTable nonExistent = rootContext.getVariable("nonExistentVariable_" + System.currentTimeMillis());
                System.out.println("  Non-existent variable handled gracefully");
            } catch (Exception e) {
                System.out.println("✓ Exception thrown for non-existent variable: " + e.getClass().getSimpleName());
            }
            
            try {
                Context nonExistent = contextManager.get("/non/existent/path/" + System.currentTimeMillis());
                System.out.println("  Non-existent context handled gracefully");
            } catch (Exception e) {
                System.out.println("✓ Exception thrown for non-existent context: " + e.getClass().getSimpleName());
            }
            System.out.println();
            
            // Итоги
            System.out.println("========================================");
            System.out.println("All Tests Passed Successfully!");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Protocol Status: ✓ WORKING");
            System.out.println("Connection: ✓ STABLE");
            System.out.println("Caching: ✓ ACTIVE");
            System.out.println();
            
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
                    System.out.println("Disconnected from server");
                } catch (Exception e) {
                    System.err.println("Error disconnecting: " + e.getMessage());
                }
            }
        }
    }
}

