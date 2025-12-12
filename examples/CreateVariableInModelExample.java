package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.server.ModelContextConstants;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;

/**
 * Пример создания переменных в контексте модели через Remote API.
 * 
 * <p>Этот пример демонстрирует:
 * <ul>
 *   <li>Подключение к серверу AggreGate</li>
 *   <li>Получение контекста модели</li>
 *   <li>Работу с переменной V_MODEL_VARIABLES</li>
 *   <li>Создание нового определения переменной</li>
 *   <li>Сохранение изменений</li>
 * </ul>
 * </p>
 * 
 * <p>ВАЖНО: Для работы с моделью используется переменная V_MODEL_VARIABLES,
 * а не стандартная V_VARIABLES, так как для контекста модели стандартная
 * переменная может быть недоступна через Remote API.</p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "examples;aggregate-api/build/libs/aggregate-api.jar;libs/*" examples.CreateVariableInModelExample [host] [port] [username] [password] [modelPath]
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.6
 */
public class CreateVariableInModelExample {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6460;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    private static final String DEFAULT_MODEL_PATH = "users.admin.models.test";
    
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        String username = args.length > 2 ? args[2] : DEFAULT_USERNAME;
        String password = args.length > 3 ? args[3] : DEFAULT_PASSWORD;
        String modelPath = args.length > 4 ? args[4] : DEFAULT_MODEL_PATH;
        
        System.out.println("========================================");
        System.out.println("Create Variable in Model Example");
        System.out.println("========================================");
        System.out.println("Server: " + host + ":" + port);
        System.out.println("Username: " + username);
        System.out.println("Model Path: " + modelPath);
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
            
            // Получение контекста модели
            System.out.println("Getting model context: " + modelPath);
            Context modelContext = contextManager.get(modelPath);
            
            if (modelContext == null) {
                System.err.println("✗ Model context not found: " + modelPath);
                System.err.println("  Please check that:");
                System.err.println("    - The model exists");
                System.err.println("    - You have permissions to access it");
                System.err.println("    - The path is correct");
                return;
            }
            
            System.out.println("✓ Model context found: " + modelContext.getPath());
            System.out.println();
            
            // Получение текущих определений переменных
            System.out.println("Getting variable definitions from V_MODEL_VARIABLES...");
            DataTable modelVariables;
            
            try {
                modelVariables = modelContext.getVariable(ModelContextConstants.V_MODEL_VARIABLES);
                System.out.println("✓ Got " + modelVariables.getRecordCount() + " variable definition(s)");
            } catch (ContextException e) {
                if (e.getMessage() != null && e.getMessage().contains("not available")) {
                    System.err.println("✗ V_MODEL_VARIABLES is not available");
                    System.err.println("  This might mean:");
                    System.err.println("    - The context is not a model");
                    System.err.println("    - The model is not fully initialized");
                    System.err.println("  Error: " + e.getMessage());
                    return;
                }
                throw e;
            }
            
            // Вывод текущих переменных
            if (modelVariables.getRecordCount() > 0) {
                System.out.println("\nCurrent variable definitions:");
                for (DataRecord rec : modelVariables) {
                    String name = rec.getString("name");
                    String desc = rec.getString("description");
                    if (desc == null) desc = "";
                    System.out.println("  - " + name + (desc.isEmpty() ? "" : " (" + desc + ")"));
                }
            } else {
                System.out.println("  (No variables defined yet)");
            }
            System.out.println();
            
            // Создание нового определения переменной
            String newVariableName = "testVariable_" + System.currentTimeMillis();
            System.out.println("Creating new variable: " + newVariableName);
            
            // Проверка, не существует ли уже переменная с таким именем
            DataRecord existing = modelVariables.select("name", newVariableName);
            if (existing != null) {
                System.out.println("⚠ Variable '" + newVariableName + "' already exists, skipping creation");
            } else {
                // Создаем новую запись с определением переменной
                DataRecord newVariableDef = modelVariables.addRecord();
                
                // Устанавливаем поля определения переменной
                newVariableDef.setValue("name", newVariableName);
                newVariableDef.setValue("format", "<value><S><D=Value>");  // Простой формат: одно строковое поле
                newVariableDef.setValue("readable", true);
                newVariableDef.setValue("writable", true);
                newVariableDef.setValue("description", "Test Variable created via Remote API");
                newVariableDef.setValue("hidden", false);
                
                // Опциональные поля
                // newVariableDef.setValue("group", "Test Group");
                // newVariableDef.setValue("readPermissions", null);
                // newVariableDef.setValue("writePermissions", null);
                
                System.out.println("  Name: " + newVariableName);
                System.out.println("  Format: <value><S><D=Value>");
                System.out.println("  Readable: true");
                System.out.println("  Writable: true");
                System.out.println("  Description: Test Variable created via Remote API");
                
                // Сохранение обновленных определений
                System.out.println("\nSaving variable definition...");
                modelContext.setVariable(ModelContextConstants.V_MODEL_VARIABLES, modelVariables);
                System.out.println("✓ Variable '" + newVariableName + "' successfully created in model");
                
                // Проверка, что переменная действительно создана
                System.out.println("\nVerifying variable creation...");
                try {
                    DataTable verification = modelContext.getVariable(ModelContextConstants.V_MODEL_VARIABLES);
                    DataRecord created = verification.select("name", newVariableName);
                    if (created != null) {
                        System.out.println("✓ Variable verified: " + created.getString("name"));
                    } else {
                        System.out.println("⚠ Variable not found after creation (might need refresh)");
                    }
                } catch (Exception e) {
                    System.out.println("⚠ Could not verify variable: " + e.getMessage());
                }
            }
            
            System.out.println("\n✓ Example completed successfully!");
            
        } catch (ContextException e) {
            System.err.println("\n✗ Context error:");
            System.err.println("  " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n✗ Unexpected error:");
            System.err.println("  " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Отключение от сервера
            if (controller != null) {
                try {
                    if (controller.isConnected()) {
                        System.out.println("\nDisconnecting...");
                        controller.disconnect();
                        System.out.println("✓ Disconnected");
                    }
                } catch (Exception e) {
                    System.err.println("⚠ Warning during disconnect: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Вспомогательный метод для создания переменной с более сложным форматом.
     * 
     * @param modelContext контекст модели
     * @param variableName имя переменной
     * @param formatString строка формата (например, "<id><I><D=ID>, <name><S><D=Name>")
     * @param description описание переменной
     * @param readable доступна ли для чтения
     * @param writable доступна ли для записи
     * @throws ContextException если произошла ошибка
     */
    public static void createVariableWithFormat(
            Context modelContext,
            String variableName,
            String formatString,
            String description,
            boolean readable,
            boolean writable) throws ContextException {
        
        // Получаем текущие определения
        DataTable modelVariables = modelContext.getVariableClone(ModelContextConstants.V_MODEL_VARIABLES);
        
        // Проверяем, не существует ли уже переменная
        DataRecord existing = modelVariables.select("name", variableName);
        if (existing != null) {
            throw new ContextException("Variable '" + variableName + "' already exists");
        }
        
        // Создаем новое определение
        DataRecord newVariableDef = modelVariables.addRecord();
        newVariableDef.setValue("name", variableName);
        newVariableDef.setValue("format", formatString);
        newVariableDef.setValue("readable", readable);
        newVariableDef.setValue("writable", writable);
        newVariableDef.setValue("description", description);
        newVariableDef.setValue("hidden", false);
        
        // Сохраняем
        modelContext.setVariable(ModelContextConstants.V_MODEL_VARIABLES, modelVariables);
    }
}

