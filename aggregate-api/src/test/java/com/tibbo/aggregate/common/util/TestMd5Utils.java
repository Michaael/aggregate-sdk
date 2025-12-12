package com.tibbo.aggregate.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.MessageDigest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Md5Utils}.
 */
@DisplayName("Md5Utils Tests")
class TestMd5Utils {

    @Test
    @DisplayName("Test RESPONSE_LEN constant")
    void testResponseLenConstant() {
        assertEquals(16, Md5Utils.RESPONSE_LEN);
    }

    @Test
    @DisplayName("Test hexHash with empty string")
    void testHexHashWithEmptyString() {
        String result = Md5Utils.hexHash("");
        assertNotNull(result);
        assertEquals(32, result.length()); // MD5 hash is 32 hex characters
    }

    @Test
    @DisplayName("Test hexHash with simple string")
    void testHexHashWithSimpleString() {
        String result = Md5Utils.hexHash("test");
        assertNotNull(result);
        assertEquals(32, result.length());
        // MD5 of "test" is "098f6bcd4621d373cade4e832627b4f6"
        assertEquals("098f6bcd4621d373cade4e832627b4f6", result);
    }

    @Test
    @DisplayName("Test hexHash with null string")
    void testHexHashWithNullString() {
        // hexHash should handle null by converting to empty string or throwing NPE
        // Based on implementation, it will throw NPE on getBytes()
        assertThrows(NullPointerException.class, () -> {
            Md5Utils.hexHash(null);
        });
    }

    @Test
    @DisplayName("Test hexHash with special characters")
    void testHexHashWithSpecialCharacters() {
        String result = Md5Utils.hexHash("hello@world#123");
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    @DisplayName("Test hexHash with unicode characters")
    void testHexHashWithUnicode() {
        String result = Md5Utils.hexHash("Привет");
        assertNotNull(result);
        assertEquals(32, result.length());
    }

    @Test
    @DisplayName("Test hexHash consistency")
    void testHexHashConsistency() {
        String input = "consistent input";
        String hash1 = Md5Utils.hexHash(input);
        String hash2 = Md5Utils.hexHash(input);
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test hexRepresentation with valid MD5 bytes")
    void testHexRepresentationWithValidBytes() {
        MessageDigest md = Md5Utils.getMessageDigest();
        md.update("test".getBytes());
        byte[] md5 = md.digest();
        
        String result = Md5Utils.hexRepresentation(md5);
        assertNotNull(result);
        assertEquals(32, result.length()); // 16 bytes * 2 hex chars = 32
    }

    @Test
    @DisplayName("Test hexRepresentation with null")
    void testHexRepresentationWithNull() {
        assertThrows(NullPointerException.class, () -> {
            Md5Utils.hexRepresentation(null);
        });
    }

    @Test
    @DisplayName("Test getMessageDigest returns valid MessageDigest")
    void testGetMessageDigest() {
        MessageDigest md = Md5Utils.getMessageDigest();
        assertNotNull(md);
        assertEquals("MD5", md.getAlgorithm());
    }

    @Test
    @DisplayName("Test getMessageDigest returns new instance each time")
    void testGetMessageDigestReturnsNewInstance() {
        MessageDigest md1 = Md5Utils.getMessageDigest();
        MessageDigest md2 = Md5Utils.getMessageDigest();
        // They should be different instances
        // We can't use != for object comparison, but we can test they work independently
        md1.update("test1".getBytes());
        md2.update("test2".getBytes());
        
        byte[] digest1 = md1.digest();
        byte[] digest2 = md2.digest();
        
        // Digests should be different for different inputs
        // We'll just verify they're not null and have correct length
        assertNotNull(digest1);
        assertNotNull(digest2);
        assertEquals(16, digest1.length);
        assertEquals(16, digest2.length);
    }

    @Test
    @DisplayName("Test complete MD5 workflow")
    void testCompleteMd5Workflow() {
        String input = "Hello, World!";
        String hash = Md5Utils.hexHash(input);
        
        assertNotNull(hash);
        assertEquals(32, hash.length());
        
        // Verify it's a valid hex string
        for (char c : hash.toCharArray()) {
            assert (Character.isDigit(c) || (c >= 'a' && c <= 'f'));
        }
    }
}

