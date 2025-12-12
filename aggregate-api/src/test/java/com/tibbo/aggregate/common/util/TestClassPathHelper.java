package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link ClassPathHelper}.
 */
@DisplayName("ClassPathHelper Tests")
class TestClassPathHelper {

    @Test
    @DisplayName("Test listJars with non-existent directory")
    void testListJarsWithNonExistentDirectory() {
        File nonExistentDir = new File("non_existent_directory_12345");
        List<String> result = ClassPathHelper.listJars(nonExistentDir);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test listJars with file instead of directory")
    void testListJarsWithFile(@TempDir File tempDir) throws IOException {
        File file = new File(tempDir, "test.txt");
        file.createNewFile();
        
        List<String> result = ClassPathHelper.listJars(file);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test listJars with empty directory")
    void testListJarsWithEmptyDirectory(@TempDir File tempDir) {
        List<String> result = ClassPathHelper.listJars(tempDir);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test listJars with directory containing non-jar files")
    void testListJarsWithNonJarFiles(@TempDir File tempDir) throws IOException {
        // Create some non-jar files
        new File(tempDir, "test.txt").createNewFile();
        new File(tempDir, "test.xml").createNewFile();
        
        List<String> result = ClassPathHelper.listJars(tempDir);
        
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test listJars with directory containing jar files")
    void testListJarsWithJarFiles(@TempDir File tempDir) throws IOException {
        // Create some jar files
        File jar1 = new File(tempDir, "test1.jar");
        File jar2 = new File(tempDir, "test2.jar");
        jar1.createNewFile();
        jar2.createNewFile();
        
        List<String> result = ClassPathHelper.listJars(tempDir);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(path -> path.contains("test1.jar")));
        assertTrue(result.stream().anyMatch(path -> path.contains("test2.jar")));
    }

    @Test
    @DisplayName("Test listJars with nested directories")
    void testListJarsWithNestedDirectories(@TempDir File tempDir) throws IOException {
        // Create nested structure
        File subDir = new File(tempDir, "subdir");
        subDir.mkdirs();
        
        File jar1 = new File(tempDir, "test1.jar");
        File jar2 = new File(subDir, "test2.jar");
        jar1.createNewFile();
        jar2.createNewFile();
        
        List<String> result = ClassPathHelper.listJars(tempDir);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(path -> path.contains("test1.jar")));
        assertTrue(result.stream().anyMatch(path -> path.contains("test2.jar")));
    }

    @Test
    @DisplayName("Test listJars with mixed files")
    void testListJarsWithMixedFiles(@TempDir File tempDir) throws IOException {
        // Create mix of jar and non-jar files
        new File(tempDir, "test.jar").createNewFile();
        new File(tempDir, "test.txt").createNewFile();
        new File(tempDir, "test.xml").createNewFile();
        new File(tempDir, "another.jar").createNewFile();
        
        List<String> result = ClassPathHelper.listJars(tempDir);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(path -> path.contains("test.jar")));
        assertTrue(result.stream().anyMatch(path -> path.contains("another.jar")));
    }

    @Test
    @DisplayName("Test addToClassPath with invalid path")
    void testAddToClassPathWithInvalidPath() {
        // This will likely throw an exception due to reflection issues or invalid path
        // The exact behavior depends on the system classloader type
        assertThrows(
            Exception.class,
            () -> {
                ClassPathHelper.addToClassPath("invalid://path");
            });
    }

    @Test
    @DisplayName("Test addToLibraryPath with valid path")
    void testAddToLibraryPathWithValidPath() throws Exception {
        String originalPath = System.getProperty("java.library.path");
        try {
            String testPath = "/test/library/path";
            ClassPathHelper.addToLibraryPath(testPath);
            
            String newPath = System.getProperty("java.library.path");
            assertNotNull(newPath);
            assertTrue(newPath.contains(testPath));
        } finally {
            // Restore original path
            if (originalPath != null) {
                System.setProperty("java.library.path", originalPath);
            }
        }
    }

    @Test
    @DisplayName("Test addToLibraryPath preserves existing path")
    void testAddToLibraryPathPreservesExistingPath() throws Exception {
        String originalPath = System.getProperty("java.library.path");
        try {
            String testPath = "/test/library/path";
            ClassPathHelper.addToLibraryPath(testPath);
            
            String newPath = System.getProperty("java.library.path");
            assertNotNull(newPath);
            // Should contain both the new path and original path
            assertTrue(newPath.contains(testPath));
            if (originalPath != null && !originalPath.isEmpty()) {
                assertTrue(newPath.contains(originalPath));
            }
        } finally {
            // Restore original path
            if (originalPath != null) {
                System.setProperty("java.library.path", originalPath);
            }
        }
    }
}

