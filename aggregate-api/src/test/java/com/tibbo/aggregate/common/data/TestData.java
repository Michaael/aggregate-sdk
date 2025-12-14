package com.tibbo.aggregate.common.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestData
{
  @Test
  public void testDataEncodingRestore() throws Exception
  {
    StringBuilder inputText = new StringBuilder();
    inputText.append(
        "Wikipedia (Listeni/ˌwɪkᵻˈpiːdiə/ or Listeni/ˌwɪkiˈpiːdiə/ wik-i-pee-dee-ə) is a free online encyclopedia that aims to allow anyone to edit articles.[3] Wikipedia is the largest and most popular general reference work on the Internet[4][5][6] and is ranked among the ten most popular websites.[7] Wikipedia is owned by the nonprofit Wikimedia Foundation.[8][9][10]");
    inputText.append(
        "Wikipedia was launched on January 15, 2001, by Jimmy Wales and Larry Sanger.[11] Sanger coined its name,[12][13] a portmanteau of wiki[notes 4] and encyclopedia. There was only the English language version initially, but it quickly developed similar versions in other languages, which differ in content and in editing practices. With 5,326,946 articles, the English");
    inputText.append(
        " Wikipedia is the largest of the more than 290 Wikipedia encyclopedias. Overall, Wikipedia consists of more than 40 million articles in more than 250 different languages[15] and as of February 2014, ");
    inputText.append("it had 18 billion page views and nearly 500 million unique visitors each month.[16]");
    inputText.append(
        "In 2005, Nature published a peer review comparing 42 science articles from Encyclopædia Britannica and Wikipedia, and found that Wikipedia's level of accuracy approached Encyclopædia Britannica's.[17] Criticism of Wikipedia includes claims that it exhibits systemic bias, presents a mixture of \"truths, half truths, and some falsehoods\",[18] and that in controversial topics, it is subject to manipulation and spin.[19]");
    
    Data data1 = new Data();
    data1.setData(inputText.toString().getBytes(Charset.forName("UTF-8")));
    StringBuilder encodeRes = data1.encode(new StringBuilder(), null, false, 0);
    
    Data data2 = new Data(encodeRes.toString());
    String outputText = new String(data2.getData(), Charset.forName("UTF-8"));
    
    assertEquals(inputText.toString(), outputText);
  }
  
  @Test
  public void testEmptyData() throws Exception
  {
    String text = "0/\u001A/\u001A/-1/0/";
    Data data1 = new Data(text);
    
    String outputText = new String(data1.getData(), Charset.forName("UTF-8"));
    assertEquals("", outputText);
  }
  
  @Test
  public void testDataConstructors() throws Exception
  {
    // Test default constructor
    Data data1 = new Data();
    assertNull(data1.getId());
    assertNull(data1.getName());
    assertNull(data1.getData());
    assertNull(data1.getPreview());
    
    // Test constructor with byte array
    byte[] testData = "test data".getBytes(StandardCharsets.UTF_8);
    Data data2 = new Data(testData);
    assertNull(data2.getId());
    assertNull(data2.getName());
    assertNotNull(data2.getData());
    assertEquals(testData.length, data2.getData().length);
    
    // Test constructor with name and byte array
    String name = "testName";
    Data data3 = new Data(name, testData);
    assertEquals(name, data3.getName());
    assertNotNull(data3.getData());
    assertEquals(testData.length, data3.getData().length);
  }
  
  @Test
  public void testDataSettersAndGetters() throws Exception
  {
    Data data = new Data();
    
    // Test ID
    Long id = 12345L;
    data.setId(id);
    assertEquals(id, data.getId());
    
    // Test name
    String name = "testName";
    data.setName(name);
    assertEquals(name, data.getName());
    
    // Test data
    byte[] testData = "test data".getBytes(StandardCharsets.UTF_8);
    data.setData(testData);
    assertNotNull(data.getData());
    assertEquals(testData.length, data.getData().length);
    
    // Test blob (should be same as data)
    byte[] blobData = "blob data".getBytes(StandardCharsets.UTF_8);
    data.setBlob(blobData);
    assertNotNull(data.getBlob());
    assertEquals(blobData.length, data.getBlob().length);
    assertEquals(blobData.length, data.getData().length);
    
    // Test preview
    byte[] previewData = "preview".getBytes(StandardCharsets.UTF_8);
    data.setPreview(previewData);
    assertNotNull(data.getPreview());
    assertEquals(previewData.length, data.getPreview().length);
    
    // Test shallow copy
    assertFalse(data.isShallowCopy());
    data.setShallowCopy(true);
    assertTrue(data.isShallowCopy());
  }
  
  @Test
  public void testDataClone() throws Exception
  {
    Data original = new Data();
    original.setId(123L);
    original.setName("test");
    original.setData("data".getBytes(StandardCharsets.UTF_8));
    original.setPreview("preview".getBytes(StandardCharsets.UTF_8));
    original.setShallowCopy(false);
    
    // Test deep clone (shallowCopy = false)
    Data cloned = original.clone();
    assertNotSame(original, cloned);
    assertEquals(original.getId(), cloned.getId());
    assertEquals(original.getName(), cloned.getName());
    assertNotNull(cloned.getData());
    assertNotNull(cloned.getPreview());
    assertNotSame(original.getData(), cloned.getData());
    assertNotSame(original.getPreview(), cloned.getPreview());
    
    // Test shallow clone (shallowCopy = true)
    original.setShallowCopy(true);
    Data shallowCloned = original.clone();
    assertNotSame(original, shallowCloned);
    assertEquals(original.getId(), shallowCloned.getId());
  }
  
  @Test
  public void testDataEqualsAndHashCode() throws Exception
  {
    Data data1 = new Data();
    data1.setId(123L);
    data1.setName("test");
    data1.setData("data".getBytes(StandardCharsets.UTF_8));
    data1.setPreview("preview".getBytes(StandardCharsets.UTF_8));
    
    Data data2 = new Data();
    data2.setId(123L);
    data2.setName("test");
    data2.setData("data".getBytes(StandardCharsets.UTF_8));
    data2.setPreview("preview".getBytes(StandardCharsets.UTF_8));
    
    // Test equals
    assertEquals(data1, data2);
    assertEquals(data1.hashCode(), data2.hashCode());
    
    // Test not equals with different ID
    data2.setId(456L);
    assertFalse(data1.equals(data2));
    
    // Test not equals with different name
    data2.setId(123L);
    data2.setName("different");
    assertFalse(data1.equals(data2));
    
    // Test not equals with null
    assertFalse(data1.equals(null));
    
    // Test not equals with different type
    assertFalse(data1.equals("not a Data object"));
  }
  
  @Test
  public void testDataAttachments() throws Exception
  {
    Data data = new Data();
    Map<String, Object> attachments = new HashMap<>();
    attachments.put("key1", "value1");
    attachments.put("key2", 123);
    
    data.setAttachments(attachments);
    assertNotNull(data.getAttachments());
    assertEquals(attachments.size(), data.getAttachments().size());
    assertEquals("value1", data.getAttachments().get("key1"));
    assertEquals(123, data.getAttachments().get("key2"));
  }
  
  @Test
  public void testDataToString() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    String str = data.toString();
    assertNotNull(str);
    assertTrue(str.contains("123"));
    assertTrue(str.contains("test"));
    assertTrue(str.contains("len="));
  }
  
  @Test
  public void testDataReleaseData() throws Exception
  {
    Data data = new Data();
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    data.setPreview("preview".getBytes(StandardCharsets.UTF_8));
    
    assertNotNull(data.getData());
    assertNotNull(data.getPreview());
    
    data.releaseData();
    
    assertNull(data.getData());
    assertNull(data.getPreview());
  }
  
  @Test
  public void testDataEncode() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    String encoded = data.encode();
    assertNotNull(encoded);
    assertTrue(encoded.length() > 0);
    
    // Test that encoded data can be decoded
    Data decoded = new Data(encoded);
    assertEquals(data.getId(), decoded.getId());
    assertEquals(data.getName(), decoded.getName());
  }
  
  @Test
  public void testDataJsonString() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    String json = data.toJsonString();
    assertNotNull(json);
    assertTrue(json.contains("\"id\""));
    assertTrue(json.contains("\"name\""));
    assertTrue(json.contains("\"data\""));
  }
  
  @Test
  public void testDataFromJsonString() throws Exception
  {
    String json = "{\"id\":\"123\",\"name\":\"test\",\"data\":\"ZGF0YQ==\",\"shallowCopy\":\"false\"}";
    Data data = new Data(json);
    
    assertNotNull(data);
    assertEquals(Long.valueOf(123L), data.getId());
    assertEquals("test", data.getName());
    assertNotNull(data.getData());
  }
  
  @Test
  public void testDataEmptyString() throws Exception
  {
    Data data = new Data("");
    assertNull(data.getId());
    assertNull(data.getName());
    assertNull(data.getData());
  }
  
  @Test
  public void testDataToDetailedString() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    String detailed = data.toDetailedString();
    assertNotNull(detailed);
    assertTrue(detailed.length() > 0);
    // Detailed string should contain more information than regular toString
    assertTrue(detailed.contains("123") || detailed.contains("test"));
  }
  
  @Test
  public void testDataToCleanString() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    String clean = data.toCleanString();
    assertNotNull(clean);
    assertTrue(clean.length() > 0);
  }
  
  @Test
  public void testDataEstimateDataSize() throws Exception
  {
    Data data = new Data();
    data.setData("test data".getBytes(StandardCharsets.UTF_8));
    data.setPreview("preview".getBytes(StandardCharsets.UTF_8));
    
    int estimatedSize = data.estimateDataSize();
    assertTrue(estimatedSize > 0);
    // Estimated size should be at least the size of data + preview
    assertTrue(estimatedSize >= data.getData().length + data.getPreview().length);
  }
  
  @Test
  public void testDataEncodeWithParameters() throws Exception
  {
    Data data = new Data();
    data.setId(123L);
    data.setName("test");
    data.setData("data".getBytes(StandardCharsets.UTF_8));
    
    StringBuilder sb = new StringBuilder();
    StringBuilder result = data.encode(sb, null, false, 0);
    
    assertNotNull(result);
    assertTrue(result.length() > 0);
    // Result should be the same StringBuilder instance
    assertSame(sb, result);
  }
  
  @Test
  public void testDataHashCodeConsistency() throws Exception
  {
    Data data1 = new Data();
    data1.setId(123L);
    data1.setName("test");
    data1.setData("data".getBytes(StandardCharsets.UTF_8));
    
    Data data2 = new Data();
    data2.setId(123L);
    data2.setName("test");
    data2.setData("data".getBytes(StandardCharsets.UTF_8));
    
    // Hash codes should be equal for equal objects
    assertEquals(data1.hashCode(), data2.hashCode());
    
    // Hash code should be consistent
    int hashCode1 = data1.hashCode();
    int hashCode2 = data1.hashCode();
    assertEquals(hashCode1, hashCode2);
  }
  
  @Test
  public void testDataNullValues() throws Exception
  {
    Data data = new Data();
    // All fields should be null initially
    assertNull(data.getId());
    assertNull(data.getName());
    assertNull(data.getData());
    assertNull(data.getPreview());
    assertNull(data.getAttachments());
    
    // Setting null values should work
    data.setId(null);
    data.setName(null);
    data.setData(null);
    data.setPreview(null);
    
    assertNull(data.getId());
    assertNull(data.getName());
    assertNull(data.getData());
    assertNull(data.getPreview());
  }
  
  @Test
  public void testDataBlobAndDataConsistency() throws Exception
  {
    Data data = new Data();
    byte[] testData = "test data".getBytes(StandardCharsets.UTF_8);
    
    // Setting blob should also set data
    data.setBlob(testData);
    assertNotNull(data.getBlob());
    assertNotNull(data.getData());
    assertEquals(testData.length, data.getBlob().length);
    assertEquals(testData.length, data.getData().length);
    
    // Setting data should also set blob
    byte[] newData = "new data".getBytes(StandardCharsets.UTF_8);
    data.setData(newData);
    assertNotNull(data.getBlob());
    assertNotNull(data.getData());
    assertEquals(newData.length, data.getBlob().length);
    assertEquals(newData.length, data.getData().length);
  }
}
