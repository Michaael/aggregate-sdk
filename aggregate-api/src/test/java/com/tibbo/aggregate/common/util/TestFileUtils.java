package com.tibbo.aggregate.common.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import junit.framework.TestCase;

public class TestFileUtils extends TestCase {
    public void testReadTextFileWithoutBOM() throws IOException, URISyntaxException {
        final char UTF8_BOM = '\uFEFF';
        URL resourceURL = getClass().getResource("/data/test_bom_file.csv");
        assertNotNull(resourceURL);
        String text = FileUtils.readTextFile(Paths.get(resourceURL.toURI()), StandardCharsets.UTF_8);
        assertTrue(text.charAt(0) != UTF8_BOM);
    }
}