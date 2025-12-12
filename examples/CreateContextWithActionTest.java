package examples;

import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.action.ActionExecutionMode;
import com.tibbo.aggregate.common.action.ActionIdentifier;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.action.GenericActionResponse;
import com.tibbo.aggregate.common.action.command.EditData;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.protocol.RemoteServer;
import com.tibbo.aggregate.common.protocol.RemoteServerController;
import com.tibbo.aggregate.common.action.ServerActionInput;

/**
 * Тест для создания контекста users.admin.models.test через вызов действия "create".
 * 
 * <p>Этот тест:
 * <ul>
 *   <li>Подключается к серверу</li>
 *   <li>Находит родительский контекст users.admin.models</li>
 *   <li>Вызывает действие "create" (или "add") для создания контекста "test"</li>
 *   <li>Обрабатывает интерактивные команды от действия (EditData, Confirm и т.д.)</li>
 *   <li>Проверяет создание контекста</li>
 *   <li>Удаляет созданный контекст</li>
 * </ul>
 * </p>
 * 
 * <p>Примечание: Действие может требовать интерактивного ввода или иметь ограничения
 * по размеру данных, что может привести к ошибкам при выполнении.</p>
 * 
 * <p>Использование:
 * <pre>
 * java -cp "examples;aggregate-api/build/libs/aggregate-api.jar;libs/*" examples.CreateContextWithActionTest [host] [port] [username] [password]
 * </pre>
 * </p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
public class CreateContextWithActionTest {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6460;
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";
    
    private static final String TEST_CONTEXT_PATH = "users.admin.models.test";
    private static final String PARENT_CONTEXT_PATH = "users.admin.models";
    private static final String CONTEXT_NAME = "test";
    
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        String username = args.length > 2 ? args[2] : DEFAULT_USERNAME;
        String password = args.length > 3 ? args[3] : DEFAULT_PASSWORD;
        
        System.out.println("========================================");
        System.out.println("Create Context with Action Test");
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
            
            // Шаг 1: Проверка существования контекста перед созданием
            System.out.println("========================================");
            System.out.println("Step 1: Check if context exists (before creation)");
            System.out.println("========================================");
            
            Context existingContext = contextManager.get(TEST_CONTEXT_PATH);
            if (existingContext != null) {
                System.out.println("⚠ Context already exists, attempting to delete it first...");
                try {
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
            
            // Шаг 2: Получение родительского контекста
            System.out.println("========================================");
            System.out.println("Step 2: Get parent context " + PARENT_CONTEXT_PATH);
            System.out.println("========================================");
            
            Context parentContext = contextManager.get(PARENT_CONTEXT_PATH);
            if (parentContext == null) {
                System.err.println("✗ Parent context not found: " + PARENT_CONTEXT_PATH);
                System.err.println("  Cannot create test context without parent");
                return;
            }
            
            System.out.println("✓ Parent context found: " + parentContext.getPath());
            
            // Проверяем наличие действия "create" (или "add")
            java.util.List<com.tibbo.aggregate.common.action.ActionDefinition> actions = 
                parentContext.getActionDefinitions();
            
            String actionName = null;
            if (actions != null) {
                for (com.tibbo.aggregate.common.action.ActionDefinition ad : actions) {
                    String name = ad.getName();
                    if ("create".equals(name) || "add".equals(name)) {
                        actionName = name;
                        System.out.println("✓ Found '" + actionName + "' action in parent context");
                        break;
                    }
                }
            }
            
            if (actionName == null) {
                System.err.println("✗ 'create' or 'add' action not found in parent context");
                System.err.println("  Available actions:");
                if (actions != null) {
                    int count = Math.min(10, actions.size());
                    for (int i = 0; i < count; i++) {
                        System.err.println("    - " + actions.get(i).getName());
                    }
                }
                return;
            }
            System.out.println();
            
            // Шаг 3: Вызов действия для создания контекста
            System.out.println("========================================");
            System.out.println("Step 3: Call '" + actionName + "' action to create context");
            System.out.println("========================================");
            
            try {
                // Инициализация действия
                System.out.println("  Initializing '" + actionName + "' action...");
                ActionIdentifier actionId = ActionUtils.initAction(
                    parentContext,
                    actionName,
                    new ServerActionInput(),
                    null, // inputData
                    new ActionExecutionMode(ActionExecutionMode.HEADLESS),
                    null  // caller
                );
                
                System.out.println("✓ Action initialized: " + actionId);
                
                // Выполнение действия пошагово
                GenericActionResponse actionResponse = null;
                int stepCount = 0;
                final int MAX_STEPS = 10; // Ограничение на количество шагов
                
                while (stepCount < MAX_STEPS) {
                    stepCount++;
                    System.out.println("  Step " + stepCount + ": Executing action...");
                    
                    // Получение следующей команды от действия
                    GenericActionCommand cmd = ActionUtils.stepAction(
                        parentContext,
                        actionId,
                        actionResponse,
                        null // caller
                    );
                    
                    if (cmd == null) {
                        System.out.println("  ✓ Action completed (no more commands)");
                        break;
                    }
                    
                    System.out.println("    Command type: " + cmd.getType());
                    System.out.println("    Request ID: " + cmd.getRequestId());
                    
                    // Обработка команды EditData (заполнение формы)
                    if (ActionUtils.CMD_EDIT_DATA.equals(cmd.getType())) {
                        System.out.println("    Processing EditData command...");
                        
                        DataTable parameters = cmd.getParameters();
                        if (parameters != null && parameters.getRecordCount() > 0) {
                            DataTable data = parameters.rec().getDataTable(EditData.CF_DATA);
                            
                            if (data != null) {
                                // Заполняем форму создания контекста
                                // Имя контекста
                                if (data.hasField("name")) {
                                    data.rec().setValue("name", CONTEXT_NAME);
                                    System.out.println("      Set name: " + CONTEXT_NAME);
                                }
                                
                                // Описание (если есть поле)
                                if (data.hasField("description")) {
                                    data.rec().setValue("description", "Test context created by SDK");
                                    System.out.println("      Set description: Test context created by SDK");
                                }
                                
                                // Возвращаем заполненные данные
                                actionResponse = new GenericActionResponse(data);
                                actionResponse.setRequestId(cmd.getRequestId());
                            } else {
                                // Если нет данных для редактирования, возвращаем пустой ответ
                                actionResponse = new GenericActionResponse(new SimpleDataTable());
                                actionResponse.setRequestId(cmd.getRequestId());
                            }
                        } else {
                            actionResponse = new GenericActionResponse(new SimpleDataTable());
                            actionResponse.setRequestId(cmd.getRequestId());
                        }
                    }
                    // Обработка команды Confirm (подтверждение)
                    else if (ActionUtils.CMD_CONFIRM.equals(cmd.getType())) {
                        System.out.println("    Processing Confirm command...");
                        // Подтверждаем действие
                        actionResponse = new GenericActionResponse(
                            new DataRecord(com.tibbo.aggregate.common.action.command.Confirm.RFT_CONFIRM, 
                                ActionUtils.YES_OPTION).wrap()
                        );
                        actionResponse.setRequestId(cmd.getRequestId());
                    }
                    // Обработка других типов команд
                    else {
                        System.out.println("    Unhandled command type: " + cmd.getType());
                        // Для неизвестных команд возвращаем пустой ответ
                        actionResponse = new GenericActionResponse(new SimpleDataTable());
                        actionResponse.setRequestId(cmd.getRequestId());
                    }
                    
                    if (cmd.isLast()) {
                        System.out.println("  ✓ Action completed (last command)");
                        break;
                    }
                }
                
                if (stepCount >= MAX_STEPS) {
                    System.out.println("  ⚠ Reached maximum step count, stopping");
                }
                
            } catch (Exception e) {
                System.err.println("✗ Error executing 'add' action: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            
            System.out.println();
            
            // Шаг 4: Проверка создания контекста
            System.out.println("========================================");
            System.out.println("Step 4: Verify context was created");
            System.out.println("========================================");
            
            // Небольшая задержка для завершения создания на сервере
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Игнорируем
            }
            
            Context createdContext = contextManager.get(TEST_CONTEXT_PATH);
            if (createdContext != null) {
                System.out.println("✓ Context successfully created!");
                System.out.println("  Path: " + createdContext.getPath());
                System.out.println("  Name: " + createdContext.getName());
            } else {
                System.out.println("⚠ Context not found (may need to refresh or check server)");
            }
            System.out.println();
            
            // Шаг 5: Удаление контекста
            System.out.println("========================================");
            System.out.println("Step 5: Delete created context");
            System.out.println("========================================");
            
            if (createdContext != null) {
                try {
                    deleteContext(contextManager, TEST_CONTEXT_PATH);
                    System.out.println("✓ Context deleted successfully");
                } catch (Exception e) {
                    System.err.println("✗ Error deleting context: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("  Nothing to delete (context not found)");
            }
            System.out.println();
            
            System.out.println("========================================");
            System.out.println("Test Completed!");
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
            parent.removeChild(context.getName());
        } catch (Exception e) {
            try {
                parent.removeChild(context);
            } catch (Exception e2) {
                try {
                    context.destroy(false);
                } catch (Exception e3) {
                    throw new RuntimeException("All deletion methods failed: " + 
                        e.getMessage() + ", " + e2.getMessage() + ", " + e3.getMessage());
                }
            }
        }
    }
}

