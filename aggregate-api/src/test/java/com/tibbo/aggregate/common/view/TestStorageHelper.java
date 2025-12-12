package com.tibbo.aggregate.common.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tibbo.aggregate.common.context.FunctionDefinition;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Тесты для класса StorageHelper.
 * 
 * <p>Этот класс содержит тесты для утилитных методов и констант StorageHelper,
 * используемых для работы с хранилищем данных в AggreGate.</p>
 * 
 * @author AggreGate SDK
 * @version 1.3.5
 */
@DisplayName("StorageHelper Tests")
class TestStorageHelper {

    @Test
    @DisplayName("Test constants - CLASS_FIELD constants")
    void testClassFieldConstants() {
        assertEquals("instance_id", StorageHelper.CLASS_FIELD_INSTANCE_ID);
        assertEquals("author", StorageHelper.CLASS_FIELD_AUTHOR);
        assertEquals("creation_time", StorageHelper.CLASS_FIELD_CREATION_TIME);
        assertEquals("update_time", StorageHelper.CLASS_FIELD_UPDATE_TIME);
    }

    @Test
    @DisplayName("Test constants - SORT constants")
    void testSortConstants() {
        assertEquals(0, StorageHelper.SORT_ASCENDING);
        assertEquals(1, StorageHelper.SORT_DESCENDING);
    }

    @Test
    @DisplayName("Test constants - VISIBILITY constants")
    void testVisibilityConstants() {
        assertEquals(0, StorageHelper.VISIBILITY_DISABLED);
        assertEquals(1, StorageHelper.VISIBILITY_VISIBLE);
        assertEquals(2, StorageHelper.VISIBILITY_HIDDEN);
    }

    @Test
    @DisplayName("Test constants - SESSION_ID")
    void testSessionIdConstant() {
        assertEquals("id", StorageHelper.SESSION_ID);
    }

    @Test
    @DisplayName("Test constants - Function name constants")
    void testFunctionNameConstants() {
        assertEquals("storageOpen", StorageHelper.F_STORAGE_OPEN);
        assertEquals("storageClose", StorageHelper.F_STORAGE_CLOSE);
        assertEquals("storageGet", StorageHelper.F_STORAGE_GET);
        assertEquals("storageUpdate", StorageHelper.F_STORAGE_UPDATE);
        assertEquals("storageDelete", StorageHelper.F_STORAGE_DELETE);
        assertEquals("storageInsert", StorageHelper.F_STORAGE_INSERT);
    }

    @Test
    @DisplayName("Test generateViewSessionId")
    void testGenerateViewSessionId() {
        // Генерируем несколько ID и проверяем, что они положительные
        for (int i = 0; i < 10; i++) {
            long sessionId = StorageHelper.generateViewSessionId();
            assertTrue(sessionId > 0, "Session ID should be positive: " + sessionId);
        }
        
        // Проверяем, что ID уникальны (с высокой вероятностью)
        long id1 = StorageHelper.generateViewSessionId();
        long id2 = StorageHelper.generateViewSessionId();
        // В большинстве случаев ID должны быть разными
        // Но не гарантируем, так как это случайные числа
    }

    @Test
    @DisplayName("Test generateViewSessionId returns positive values")
    void testGenerateViewSessionIdPositive() {
        long sessionId = StorageHelper.generateViewSessionId();
        assertTrue(sessionId > 0, "Session ID should always be positive");
        assertTrue(sessionId <= Long.MAX_VALUE, "Session ID should be within long range");
    }

    @Test
    @DisplayName("Test getBaseFunctionDefinitions")
    void testGetBaseFunctionDefinitions() {
        List<FunctionDefinition> definitions = StorageHelper.getBaseFunctionDefinitions();
        
        assertNotNull(definitions, "Function definitions list should not be null");
        assertTrue(definitions.size() > 0, "Function definitions list should not be empty");
        
        // Проверяем, что список содержит основные функции
        boolean hasOpen = false;
        boolean hasClose = false;
        boolean hasGet = false;
        boolean hasUpdate = false;
        boolean hasDelete = false;
        boolean hasInsert = false;
        
        for (FunctionDefinition fd : definitions) {
            if (StorageHelper.F_STORAGE_OPEN.equals(fd.getName())) {
                hasOpen = true;
            }
            if (StorageHelper.F_STORAGE_CLOSE.equals(fd.getName())) {
                hasClose = true;
            }
            if (StorageHelper.F_STORAGE_GET.equals(fd.getName())) {
                hasGet = true;
            }
            if (StorageHelper.F_STORAGE_UPDATE.equals(fd.getName())) {
                hasUpdate = true;
            }
            if (StorageHelper.F_STORAGE_DELETE.equals(fd.getName())) {
                hasDelete = true;
            }
            if (StorageHelper.F_STORAGE_INSERT.equals(fd.getName())) {
                hasInsert = true;
            }
        }
        
        assertTrue(hasOpen, "Should contain storageOpen function");
        assertTrue(hasClose, "Should contain storageClose function");
        assertTrue(hasGet, "Should contain storageGet function");
        assertTrue(hasUpdate, "Should contain storageUpdate function");
        assertTrue(hasDelete, "Should contain storageDelete function");
        assertTrue(hasInsert, "Should contain storageInsert function");
    }

    @Test
    @DisplayName("Test makeExpression")
    void testMakeExpression() {
        String field = "testField";
        String expression = StorageHelper.makeExpression(field);
        
        assertNotNull(expression, "Expression should not be null");
        assertTrue(expression.contains(field), "Expression should contain field name");
        assertTrue(expression.contains("env"), "Expression should contain env reference");
    }

    @Test
    @DisplayName("Test makeExpression with different fields")
    void testMakeExpressionDifferentFields() {
        String[] fields = {"field1", "field2", "instanceId", "author"};
        
        for (String field : fields) {
            String expression = StorageHelper.makeExpression(field);
            assertNotNull(expression, "Expression should not be null for field: " + field);
            assertTrue(expression.contains(field), "Expression should contain field: " + field);
        }
    }

    @Test
    @DisplayName("Test constants - MANY_TO_MANY constants")
    void testManyToManyConstants() {
        assertEquals("relation_id", StorageHelper.MANY_TO_MANY_FIELD_RELATION_ID);
        assertEquals("left_id", StorageHelper.MANY_TO_MANY_FIELD_LEFT_ID);
        assertEquals("right_id", StorageHelper.MANY_TO_MANY_FIELD_RIGTH_ID);
        assertEquals("rel_", StorageHelper.MANY_TO_MANY_TABLE_PREFIX);
    }

    @Test
    @DisplayName("Test constants - Event constants")
    void testEventConstants() {
        assertEquals("classInstanceCreated", StorageHelper.E_CLASS_INSTANCE_CREATED);
        assertEquals("classInstanceChanged", StorageHelper.E_CLASS_INSTANCE_CHANGED);
        assertEquals("classInstanceDeleted", StorageHelper.E_CLASS_INSTANCE_DELETED);
        assertEquals("classInstanceCommented", StorageHelper.E_CLASS_INSTANCE_COMMENTED);
    }

    @Test
    @DisplayName("Test constants - Event field constants")
    void testEventFieldConstants() {
        assertEquals("instanceId", StorageHelper.FIELD_EVENT_INSTANCE_ID);
        assertEquals("instanceDescription", StorageHelper.FIELD_EVENT_INSTANCE_DESCRIPTION);
        assertEquals("fieldName", StorageHelper.FIELD_EVENT_FIELD_NAME);
        assertEquals("oldValue", StorageHelper.FIELD_EVENT_OLD_VALUE);
        assertEquals("newValue", StorageHelper.FIELD_EVENT_NEW_VALUE);
    }
}

