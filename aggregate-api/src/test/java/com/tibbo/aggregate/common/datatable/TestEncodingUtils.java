package com.tibbo.aggregate.common.datatable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.datatable.field.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.tests.*;
import com.tibbo.aggregate.common.util.*;
import org.junit.jupiter.api.Test;
import org.w3c.dom.*;

public class TestEncodingUtils extends CommonsTestCase
{
  private static final String STRING_FIELD_VALUE = "test77NNxxG0";
  private static final int INT_FIELD_VALUE = 2156;
  private static final float FLOAT_FIELD_VALUE = 12.78f;
  private static final long LONG_VALUE = 12l;
  private static final double DOUBLE_VALUE = 12.56;
  
  private static final String STRING_FIELD = "string";
  private static final String BOOLEAN_TRUE_DESCRIPTION = "not false";
  private static final String BOOLEAN_FALSE_DESCRIPTION = "not true";
  
  public static final TableFormat TEST_TABLE_FORMAT;
  
  static
  {
    TEST_TABLE_FORMAT = new TableFormat(1, 100);
    FieldFormat<String> strFF = FieldFormat.create("<" + STRING_FIELD + "><" + FieldFormat.STRING_FIELD + "><F=N>");
    strFF.setDefault("zzz");
    strFF.setKeyField(true);
    strFF.setDescription("Description for this field");
    strFF.setHelp("This is help containing some tags </help>");
    FieldFormat<Integer> intFF = FieldFormat.create("<intField><" + FieldFormat.INTEGER_FIELD + ">");
    intFF.addValidator(new LimitsValidator(1, INT_FIELD_VALUE + 1));
    intFF.setKeyField(true);
    Map<Integer, String> selectionValues = new LinkedHashMap<>();
    for (int i = 1; i < 10; i++)
    {
      selectionValues.put(i, Integer.toString(i));
    }
    intFF.setSelectionValues(selectionValues);
    intFF.setExtendableSelectionValues(true);
    FieldFormat floatFF = FieldFormat.create("<floatField><" + FieldFormat.FLOAT_FIELD + ">");
    floatFF.setInlineData(true);
    FieldFormat dataTableFF = FieldFormat.create("<table><" + FieldFormat.DATATABLE_FIELD + ">");
    dataTableFF.addValidator(new NonNullValidator());
    FieldFormat<Boolean> booleanFF = FieldFormat.create("<booleanField><" + FieldFormat.BOOLEAN_FIELD + ">");
    Map<Boolean, String> bValues = new LinkedHashMap<>();
    bValues.put(true, BOOLEAN_TRUE_DESCRIPTION);
    bValues.put(false, BOOLEAN_FALSE_DESCRIPTION);
    booleanFF.setSelectionValues(bValues);
    booleanFF.setReadonly(true);
    FieldFormat longFF = FieldFormat.create("<longField><" + FieldFormat.LONG_FIELD + ">");
    longFF.setOptional(true);
    FieldFormat doubleFF = FieldFormat.create("<doubleField><" + FieldFormat.DOUBLE_FIELD + ">");
    doubleFF.setNotReplicated(true);
    FieldFormat<Date> dateFF = FieldFormat.create("<date><" + FieldFormat.DATE_FIELD + "><F=N>");
    dateFF.setDefault(new Date(11));
    dateFF.setGroup("date");
    dateFF.setEditorOptions("editor options with invalid html markup <tab <ff gg=\"'\"\"\">");
    FieldFormat colorFF = FieldFormat.create("<color><" + FieldFormat.COLOR_FIELD + ">");
    colorFF.setHidden(true);
    FieldFormat dataFF = FieldFormat.create("<data><" + FieldFormat.DATA_FIELD + ">");
    dataFF.setEditor(DataFieldFormat.EDITOR_SOUND);
    dataFF.setIcon(Icons.ST_ALERT);
    
    TEST_TABLE_FORMAT.addField(strFF);
    TEST_TABLE_FORMAT.addField(intFF);
    TEST_TABLE_FORMAT.addField(floatFF);
    TEST_TABLE_FORMAT.addField(dataTableFF);
    TEST_TABLE_FORMAT.addField(booleanFF);
    TEST_TABLE_FORMAT.addField(longFF);
    TEST_TABLE_FORMAT.addField(doubleFF);
    TEST_TABLE_FORMAT.addField(dateFF);
    TEST_TABLE_FORMAT.addField(colorFF);
    TEST_TABLE_FORMAT.addField(dataFF);
    
    TEST_TABLE_FORMAT.addTableValidator(new TableKeyFieldsValidator());
    TEST_TABLE_FORMAT.addTableValidator(new TableExpressionValidator("2 >= 1 ? null : 'Validation failed'"));
  }
  
  public static DataTable createTestDataTable(boolean defaultStringValue, int recordCount)
  {
    DataTable dt = new SimpleDataTable(TEST_TABLE_FORMAT);
    DataRecord rec;
    for (int i = 0; i < recordCount; i++)
    {
      rec = dt.addRecord();
      fulfillRecord(defaultStringValue, rec);
    }
    
    return dt;
  }
  
  private static void fulfillRecord(boolean defaultStringValue, DataRecord rec)
  {
    if (defaultStringValue)
    {
      FieldFormat ff = rec.getFormat().getField(STRING_FIELD);
      // Setting string field to default value
      rec.setValue(STRING_FIELD, ff.getDefaultValue());
    }
    else
    {
      rec.setValue(STRING_FIELD, STRING_FIELD_VALUE);
    }
    
    rec.setValue("intField", INT_FIELD_VALUE);
    rec.setValue("floatField", FLOAT_FIELD_VALUE);
    rec.setValue("table", new SimpleDataTable(new TableFormat(1, 1)));
    rec.setValue("booleanField", true);
    rec.setValue("longField", LONG_VALUE);
    rec.setValue("doubleField", DOUBLE_VALUE);
    rec.setValue("date", null);
    rec.setValue("color", Color.BLUE);
    Data value = new Data(new byte[] { 65, 66, 67 });
    value.setName("zzzz");
    rec.setValue("data", value);
  }
  
  @Test
  public void testDataTableEncodingAccuracy() throws Exception
  {
    DataTable dt = createTestDataTable(false, 7);
    CharArrayWriter writer = new CharArrayWriter();
    XMLEncodingSettings settings = new XMLEncodingSettings(true, null, false, null);
    XMLEncodingHelper.encodeToXML(dt, null, null, settings, writer);
    
    String xml = new String(writer.toCharArray());
    DataTable decoded = XMLEncodingHelper.decodeFromXML(xml, StringUtils.UTF8_CHARSET.name(), null);
    assertEquals(decoded, dt);
  }
  
  @Test
  public void testXSLTTransformResult() throws Exception
  {
    DataTable dt = createTestDataTable(false, 7);
    CharArrayWriter writer = new CharArrayWriter();
    XMLEncodingSettings settings = new XMLEncodingSettings(true, null, false, null);
    XMLEncodingHelper.encodeToXML(dt, null, null, settings, writer);
    
    String htmlTransform = StringUtils.streamToString(TestEncodingUtils.class.getResourceAsStream("dataTable.xslt"), Charset.defaultCharset().name());
    String xmlTransform = htmlTransform.replace("html", "xml");
    Source xsltSource = new StreamSource(new ByteArrayInputStream(xmlTransform.getBytes()));
    TransformerFactory transFact = TransformerFactory.newInstance();
    Transformer trans = transFact.newTransformer(xsltSource);
    
    // Testing dataTable integrity after encoding/decoding
    CharArrayReader charArrayReader = new CharArrayReader(writer.toCharArray());
    StreamSource xmlSource = new StreamSource(charArrayReader);
    
    ByteArrayOutputStream htmlOut = new ByteArrayOutputStream();
    trans.transform(xmlSource, new StreamResult(htmlOut));
    
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Turn on validation, and turn off namespaces
    factory.setValidating(false);
    factory.setNamespaceAware(false);
    
    factory.setIgnoringElementContentWhitespace(true);
    factory.setCoalescing(true);
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(new ByteArrayInputStream(htmlOut.toByteArray()));
    
    // Checking fields are safe
    NodeList foundNodes = (NodeList) XMLEncodingHelper.evaluateXPathExpression(doc, "table/tr[position()=2]/td[position()=1]/text()", XPathConstants.NODESET);
    String stringF = foundNodes.item(0).getTextContent();
    assertEquals(STRING_FIELD_VALUE, stringF);
    
    foundNodes = (NodeList) XMLEncodingHelper.evaluateXPathExpression(doc, "table/tr[position()=2]/td[position()=2]/text()", XPathConstants.NODESET);
    int intF = Integer.parseInt(foundNodes.item(0).getTextContent());
    assertEquals(INT_FIELD_VALUE, intF);
    
    foundNodes = (NodeList) XMLEncodingHelper.evaluateXPathExpression(doc, "table/tr[position()=2]/td[position()=3]/text()", XPathConstants.NODESET);
    float floatF = Float.parseFloat(foundNodes.item(0).getTextContent());
    assertEquals(FLOAT_FIELD_VALUE, floatF);
    
    // Checking that data table field value encoded as table
    foundNodes = (NodeList) XMLEncodingHelper.evaluateXPathExpression(doc, "table/tr[position()=2]/td[position()=4]/table", XPathConstants.NODESET);
    assertTrue(foundNodes.getLength() > 0);
    foundNodes = (NodeList) XMLEncodingHelper.evaluateXPathExpression(doc, "table/tr[position()=2]/td[position()=5]/text()", XPathConstants.NODESET);
    assertEquals(BOOLEAN_TRUE_DESCRIPTION, foundNodes.item(0).getTextContent());
  }
  
  @Test
  public void testEncodedDataTableNodeStructure() throws Exception
  {
    DataTable dt = createTestDataTable(true, 10);
    dt.rec().setValue(STRING_FIELD, null);
    
    // encodeFormat=true
    XMLEncodingSettings settings = new XMLEncodingSettings(true, null, false, null);
    settings.setAllFields(true);
    Document doc = encodeDataTableToDocument(dt, settings);
    
    Node table = doc.getDocumentElement();
    Node formatNode = table.getChildNodes().item(0);
    assertEquals(XMLEncodingHelper.ELEMENT_FORMAT, formatNode.getNodeName());
    Node recordsNode = table.getChildNodes().item(1);
    assertEquals(XMLEncodingHelper.ELEMENT_RECORDS, recordsNode.getNodeName());
    Node recordNode = recordsNode.getChildNodes().item(0);
    assertEquals(XMLEncodingHelper.ELEMENT_RECORD, recordNode.getNodeName());
    NodeList fields = recordNode.getChildNodes();
    assertEquals(dt.getFormat().getFieldCount(), fields.getLength());
  }
  
  @Test
  public void testEncodedDataTableWithoutFormatAndDefaultFields() throws Exception
  {
    DataTable dt = createTestDataTable(true, 5);
    
    // encodeFormat=false
    XMLEncodingSettings settings1 = new XMLEncodingSettings(false, null, false, null);
    // Testing encoded table without format node and skipping default field values (allFields=false)
    Document doc = encodeDataTableToDocument(dt, settings1);
    Node table = doc.getDocumentElement();
    Node recordsNode = table.getChildNodes().item(0);
    assertEquals(XMLEncodingHelper.ELEMENT_RECORDS, recordsNode.getNodeName());
    Node recordNode = recordsNode.getChildNodes().item(0);
    NodeList fields = recordNode.getChildNodes();
    int expectedFieldCount = dt.getFormat().getFieldCount() - 1;// without 'string' field which has default value
    assertEquals(expectedFieldCount, fields.getLength());
  }
  
  private Document encodeDataTableToDocument(DataTable dt, XMLEncodingSettings settings) throws ParserConfigurationException, DOMException, ContextException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    doc.appendChild(XMLEncodingHelper.createDataTableNode(dt, doc, null, null, settings));
    return doc;
  }
}
