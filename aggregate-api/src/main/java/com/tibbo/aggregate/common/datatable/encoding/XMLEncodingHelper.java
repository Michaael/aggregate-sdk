package com.tibbo.aggregate.common.datatable.encoding;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.xml.utils.*;
import org.w3c.dom.*;
import org.w3c.dom.Element;
import org.w3c.dom.ls.*;
import org.xml.sax.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import com.tibbo.aggregate.common.expression.*;
import com.tibbo.aggregate.common.util.*;

public class XMLEncodingHelper
{
  public static final String CONTENT_TYPE = "contentType";
  public static final String CONTENT_TYPE_BASE64 = "base64";
  
  public static final String ELEMENT_DATA_TABLE = "table";
  public static final String ELEMENT_FORMAT = "format";
  public static final String ELEMENT_FIELDS = "fields";
  public static final String ELEMENT_BINDING = "binding";
  public static final String ELEMENT_BINDINGS = "bindings";
  public static final String ELEMENT_REFERENCE = "reference";
  public static final String ELEMENT_EXPRESSION = "expression";
  public static final String ELEMENT_FIELD = "field";
  public static final String ELEMENT_SELECTION_VALUES = "selectionValues";
  public static final String ELEMENT_HELP = "help";
  public static final String ELEMENT_GROUP = "group";
  public static final String ELEMENT_OPTION = "option";
  public static final String ELEMENT_RECORDS = "records";
  public static final String ELEMENT_RECORD = "record";
  public static final String ELEMENT_DEFAULT_VALUE = "defaultValue";
  public static final String ELEMENT_DATA = "data";
  public static final String ELEMENT_NULL_VALUE = "nullValue";
  public static final String ELEMENT_PREVIEW = "preview";
  public static final String ELEMENT_FIELD_VALUE = "value";
  
  public static final String ATTRIBUTE_ID = "id";
  public static final String ATTRIBUTE_MINIMUM_RECORDS = "minRecords";
  public static final String ATTRIBUTE_MAXIMUM_RECORDS = "maxRecords";
  public static final String ATTRIBUTE_REORDERABLE = "reorderable";
  public static final String ATTRIBUTE_UNRESIZABLE = "unresizable";
  public static final String ATTRIBUTE_NAME = "name";
  public static final String ATTRIBUTE_TYPE = "type";
  public static final String ATTRIBUTE_DESCRIPTION = "description";
  public static final String ATTRIBUTE_NULLABLE = "nullable";
  public static final String ATTRIBUTE_OPTIONAL = "optional";
  public static final String ATTRIBUTE_ICON = "icon";
  public static final String ATTRIBUTE_EXTENDABLE_SELECTION_VALUES = "extendableSelectionValues";
  public static final String ATTRIBUTE_READONLY = "readonly";
  public static final String ATTRIBUTE_NOT_REPLICATED = "notReplicated";
  public static final String ATTRIBUTE_HIDDEN = "hidden";
  public static final String ATTRIBUTE_INLINE = "inline";
  public static final String ATTRIBUTE_ADVANCED = "advanced";
  public static final String ATTRIBUTE_KEY_FIELD = "keyfield";
  public static final String ATTRIBUTE_EDITOR = "editor";
  public static final String ELEMENT_EDITOR_OPTIONS = "editorOptions";
  public static final String ELEMENT_VALIDATORS = "validators";
  
  public static final Pattern RESOURCE_PATTERN = Pattern.compile("\\$R\\{[\\w_]*\\}");
  
  // In old versions editorOptions was encoded as attribute. We still support it for compatibility.
  public static final String ATTRIBUTE_EDITOR_OPTIONS = "editorOptions";
  private static final ClassicEncodingSettings ENCODE_VALIDATORS_SETTINGS = new ClassicEncodingSettings(false);
  
  public static void encodeToXML(DataTable dt, CallerController cc, ContextManager cm, XMLEncodingSettings settings, Writer writer) throws ParserConfigurationException, IOException, ContextException,
      DOMException
  {
    Document doc = encodeToDocument(dt, cc, cm, settings);
    DOMImplementationLS domImplLS = (DOMImplementationLS) doc.getImplementation();
    LSOutput lsOutput = domImplLS.createLSOutput();
    lsOutput.setEncoding(StandardCharsets.UTF_8.name());
    lsOutput.setCharacterStream(writer);
    LSSerializer serializer = domImplLS.createLSSerializer();
    serializer.getDomConfig().setParameter("format-pretty-print", true);
    serializer.write(doc, lsOutput);
  }
  
  public static Document encodeToDocument(DataTable dt, CallerController cc, ContextManager cm, XMLEncodingSettings settings) throws ParserConfigurationException, IOException, ContextException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.newDocument();
    
    doc.appendChild(createDataTableNode(dt, doc, cc, cm, settings));
    return doc;
  }
  
  public static DataTable decodeFromXML(String from, String encoding, TableFormat givenFormat) throws IllegalArgumentException, IOException, SAXException, ParserConfigurationException
  {
    return decodeFromXML(new ByteArrayInputStream(from.getBytes(encoding)), givenFormat);
  }
  
  public static DataTable decodeFromXML(InputStream from, TableFormat givenFormat) throws IllegalArgumentException, IOException, SAXException, ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // Turn on validation, and turn off namespaces
    factory.setValidating(false);
    factory.setNamespaceAware(false);
    
    factory.setIgnoringElementContentWhitespace(true);
    factory.setCoalescing(true);
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(from);
    return decodeFromDocument(doc, givenFormat);
  }
  
  public static DataTable decodeFromDocument(Document doc, TableFormat givenFormat) throws IOException
  {
    Element root = doc.getDocumentElement();
    
    if (!root.getNodeName().equals(ELEMENT_DATA_TABLE))
    {
      throw new IOException("Incorrect data table document format. It must have single instance of '" + ELEMENT_DATA_TABLE + "' element.");
    }
    
    return readDataTableFromNode(root, new XMLEncodingSettings(false, givenFormat, false, null));
  }
  
  private static FieldFormat readFieldFormatFromNode(Node fn) throws IOException
  {
    NamedNodeMap fAttrs = fn.getAttributes();
    String name = null;
    String type = null;
    // Trying to get name and type of field
    for (int i = 0; i < fAttrs.getLength(); i++)
    {
      if (fAttrs.item(i).getNodeName().equals(ATTRIBUTE_NAME))
      {
        name = fAttrs.item(i).getNodeValue();
      }
      else if (fAttrs.item(i).getNodeName().equals(ATTRIBUTE_TYPE))
      {
        type = fAttrs.item(i).getNodeValue();
      }
    }
    if (name == null || type == null)
    {
      throw new IOException("Incorrect data table document format. Field element should have '" + ATTRIBUTE_NAME + "' attribute and '" + ATTRIBUTE_TYPE + "' attribute.");
    }
    FieldFormat ff = FieldFormat.create(name, type.charAt(0));
    String editorOptions = ff.getEditorOptions();
    // Processing attributes of field format node
    for (int i = 0; i < fAttrs.getLength(); i++)
    {
      String nodeName = fAttrs.item(i).getNodeName();
      String nodeValue = fAttrs.item(i).getNodeValue();
      if (nodeName.equals(ATTRIBUTE_DESCRIPTION))
      {
        ff.setDescription(nodeValue);
      }
      else if (nodeName.equals(ATTRIBUTE_NULLABLE))
      {
        ff.setNullable(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_OPTIONAL))
      {
        ff.setOptional(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_ICON))
      {
        ff.setIcon(nodeValue);
      }
      else if (nodeName.equals(ATTRIBUTE_EXTENDABLE_SELECTION_VALUES))
      {
        ff.setExtendableSelectionValues(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_READONLY))
      {
        ff.setReadonly(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_NOT_REPLICATED))
      {
        ff.setNotReplicated(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_HIDDEN))
      {
        ff.setHidden(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_INLINE))
      {
        ff.setInlineData(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_ADVANCED))
      {
        ff.setAdvanced(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_KEY_FIELD))
      {
        ff.setKeyField(Boolean.parseBoolean(nodeValue));
      }
      else if (nodeName.equals(ATTRIBUTE_EDITOR))
      {
        ff.setEditor(nodeValue);
      }
      else if (nodeName.equals(ATTRIBUTE_EDITOR_OPTIONS))
      {
        editorOptions = nodeValue;
      }
    }
    NodeList fnc = fn.getChildNodes();
    // Looking for default value element. Processing it if found.
    for (int i = 0; i < fnc.getLength(); i++)
    {
      Node item = fnc.item(i);
      if (item.getNodeName().equals(ELEMENT_DEFAULT_VALUE))
      {
        Object defaultVal = decodeFieldValueFromXML(ff, item, null);
        setDefaultValueIfPossible(defaultVal, ff);
      }
      else if (item.getNodeName().equals(ELEMENT_SELECTION_VALUES))
      {
        Map<Object, String> selVals = new HashMap<Object, String>();
        NodeList sVals = item.getChildNodes();
        for (int j = 0; j < sVals.getLength(); j++)
        {
          if (ELEMENT_OPTION.equals(sVals.item(j).getNodeName()))
          {
            NamedNodeMap attrs = sVals.item(j).getAttributes();
            String description = null;
            for (int k = 0; k < sVals.getLength(); k++)
            {
              Node attrItem = attrs.item(k);
              if (ATTRIBUTE_DESCRIPTION.equals(attrItem.getNodeName()))
              {
                description = attrItem.getNodeValue();
                break;
              }
            }
            if (description == null)
            {
              throw new IOException("Incorrect selection values format. Each '" + ELEMENT_OPTION + "' ' element should have '" + ATTRIBUTE_DESCRIPTION + "' attribute.");
            }
            selVals.put(decodeFieldValueFromXML(ff, sVals.item(j), null), description);
          }
        }
        ff.setSelectionValues(selVals);
      }
      else if (ELEMENT_HELP.equals(item.getNodeName()))
      {
        ff.setHelp(item.getTextContent());
      }
      else if (ELEMENT_GROUP.equals(item.getNodeName()))
      {
        ff.setGroup(item.getTextContent());
      }
      else if (ELEMENT_EDITOR_OPTIONS.equals(item.getNodeName()) && editorOptions == null)
      {
        editorOptions = getElementContentWithUnsafetyText(item, null, false);
      }
      else if (ELEMENT_VALIDATORS.equals(item.getNodeName()))
      {
        String vals = getElementContentWithUnsafetyText(item, null, false);
        
        try
        {
          ff.createValidators(vals, new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(vals)));
        }
        catch (SyntaxErrorException ex)
        {
          // vals == null, do nothing
        }
      }
    }
    ff.setEditorOptions(editorOptions);
    
    return ff;
  }
  
  private static void setDefaultValueIfPossible(Object defaultValue, FieldFormat ff)
  {
    // There are some situations when FieldFormat's validator can resist setting its own NotNullDefault
    if (ff.isNullable() || !ff.getNotNullDefault().equals(defaultValue))
    {
      if (defaultValue == null)
      {
        List<FieldValidator> validators = ff.getValidators();
        for (FieldValidator validator : validators)
        {
          if (validator.getType() != null && validator.getType().equals(FieldFormat.VALIDATOR_NON_NULL))
            return;
        }
      }
      ff.setDefault(defaultValue);
    }
  }
  
  private static void setElementContentWithUnsafetyText(Element el, byte[] content)
  {
    el.setAttribute(CONTENT_TYPE, CONTENT_TYPE_BASE64);
    el.setTextContent(Base64.getEncoder().encodeToString(content));
  }
  
  /**
   * Common way to save any XML Element Text Content. Desired Element text content can contain non Valid XML chars. In this case text content is encoded with base64 and element receive corresponding
   * attribute value.
   *
   * @param el
   *          Element
   * @param content
   *          String
   */
  public static void setElementContentWithUnsafetyText(Element el, String content)
  {
    boolean valid = true;
    for (int i = 0; i < content.length(); i++)
    {
      if (!XMLChar.isValid(content.charAt(i)))
      {
        valid = false;
        break;
      }
    }
    if (valid)
    {
      el.setTextContent(content);
    }
    else
    {
      el.setAttribute(CONTENT_TYPE, CONTENT_TYPE_BASE64);
      el.setTextContent(Base64.getEncoder().encodeToString(content.getBytes(StringUtils.UTF8_CHARSET)));
    }
  }
  
  private static byte[] getBinaryElementContentWithUnsafetyText(Node el)
  {
    if (el == null)
    {
      return null;
    }
    if (el.getAttributes().getNamedItem(CONTENT_TYPE) != null)
    {
      String cType = el.getAttributes().getNamedItem(CONTENT_TYPE).getTextContent();
      if (cType != null && cType.equals(CONTENT_TYPE_BASE64))
      {
        return Base64.getDecoder().decode(getOwnTextContent(el));
      }
    }
    
    return el.getTextContent().getBytes();
  }
  
  /**
   * Returns text content of provided node omitting text content of wrapped child nodes
   */
  private static String getOwnTextContent(Node node)
  {
    Node clone = node.cloneNode(true);
    NodeList children = clone.getChildNodes();
    List<Node> toRemove = new LinkedList<Node>();
    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() != Node.TEXT_NODE)
      {
        toRemove.add(child);
      }
    }
    for (Node removed : toRemove)
    {
      clone.removeChild(removed);
    }
    return clone.getTextContent();
  }
  
  /**
   * Common way to read text content of XML Element. If it has attribute 'contentType' with value 'base64' then text content is being decoded from base64 encoding.
   *
   * @param el
   *          Node
   * @return String
   * @see #setElementContentWithUnsafetyText
   */
  public static String getElementContentWithUnsafetyText(Node el, ResourceBundle bundle, boolean transferEncode)
  {
    if (el == null)
    {
      return null;
    }
    String value = null;
    if (el.getAttributes().getNamedItem(CONTENT_TYPE) != null)
    {
      String cType = el.getAttributes().getNamedItem(CONTENT_TYPE).getTextContent();
      if (cType != null && cType.equals(CONTENT_TYPE_BASE64))
      {
        value = new String(Base64.getDecoder().decode(el.getTextContent()), StringUtils.UTF8_CHARSET);
      }
    }
    
    if (value == null)
    {
      value = el.getTextContent();
    }
    if (bundle != null)
    {
      value = parseBundles(value, bundle, transferEncode);
    }
    return value;
  }
  
  public static String parseBundles(String value, ResourceBundle bundle, boolean transferEncode)
  {
    Matcher matcher = RESOURCE_PATTERN.matcher(value);
    while (matcher.find())
    {
      try
      {
        final String resourceName = matcher.group().substring(3, matcher.group().length() - 1);
        final String resourceString = bundle.getString(resourceName);
        
        String replacement = transferEncode ? TransferEncodingHelper.encode(resourceString) : resourceString;
        
        // Escaping dollars and backslashes, since Matcher.replateFirst() has special treatment for them
        replacement = replacement.replace("\\", "\\\\").replace("$", "\\$");
        
        value = matcher.replaceFirst(replacement);
        matcher.reset(value);
      }
      catch (Exception ex)
      {
        Log.WIDGETS.error("Error processing string constant for i18n: " + value, ex);
      }
    }
    return value;
  }
  
  private static void encodeFieldValueToXML(FieldFormat ff, Object value, Document doc, Element targetNode, CallerController cc, ContextManager cm, XMLEncodingSettings settings) throws IOException,
      ParserConfigurationException, ContextException
  {
    if (value == null)
    {
      Element nve = doc.createElement(ELEMENT_NULL_VALUE);
      targetNode.appendChild(nve);
      return;
    }
    switch (ff.getType())
    {
      case FieldFormat.DATATABLE_FIELD: // DataTable field
        settings.setFieldFormat(ff);
        targetNode.appendChild(createDataTableNode((DataTable) value, doc, cc, cm, settings));
        return;
      case FieldFormat.DATA_FIELD: // Data field
        Element dataNode = doc.createElement(ELEMENT_DATA);
        Data iData = (Data) value;
        
        if (iData.getName() != null)
        {
          dataNode.setAttribute(ATTRIBUTE_NAME, iData.getName());
        }
        
        byte[] bts = iData.getData();
        if (bts == null && cm != null)
        {
          bts = iData.fetchData(cm, cc);
        }
        if (bts != null)
        {
          Element data = doc.createElement(ELEMENT_DATA);
          setElementContentWithUnsafetyText(data, bts);
          dataNode.appendChild(data);
        }
        else
        {
          dataNode.appendChild(doc.createElement(ELEMENT_NULL_VALUE));
        }
        targetNode.appendChild(dataNode);
        bts = iData.getPreview();
        if (bts != null)
        {
          Element prev = doc.createElement(ELEMENT_PREVIEW);
          setElementContentWithUnsafetyText(prev, bts);
          dataNode.appendChild(prev);
        }
        if (iData.getId() != null)
        {
          dataNode.setAttribute(ATTRIBUTE_ID, iData.getId().toString());
        }
        
        return;
    }
    
    setElementContentWithUnsafetyText(targetNode, ff.valueToString(value));
  }
  
  private static Object decodeFieldValueFromXML(FieldFormat ff, Node valueNode, ResourceBundle bundle) throws IOException
  {
    Node nve = getNamedNodeChild(valueNode, ELEMENT_NULL_VALUE);
    if (nve != null)
    {
      return null;
    }
    
    switch (ff.getType())
    {
      case FieldFormat.DATATABLE_FIELD: // DataTable field
        Node dtNode = getNamedNodeChild(valueNode, ELEMENT_DATA_TABLE);
        if (dtNode == null)
        {
          throw new IOException("Incorrect field value node. It has data table type and must have single instance of '" + ELEMENT_DATA_TABLE + "' element.");
        }
        
        return readDataTableFromNode(dtNode, new XMLEncodingSettings(false, null, true, ff, bundle));
      case FieldFormat.DATA_FIELD: // Data field
        Node dataNode = getNamedNodeChild(valueNode, ELEMENT_DATA);
        if (dataNode == null)
        {
          throw new IOException("Incorrect field value node. It has data type and must have single instance of '" + ELEMENT_DATA + "' element.");
        }
        Data d = new Data();
        NamedNodeMap attrs = dataNode.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++)
        {
          String attrName = attrs.item(i).getNodeName();
          String attrVal = attrs.item(i).getNodeValue();
          if (attrName.equals(ATTRIBUTE_NAME))
          {
            d.setName(attrVal);
          }
          else if (attrName.equals(ATTRIBUTE_ID))
          {
            d.setId(Long.parseLong(attrVal));
          }
        }
        Node prev = getNamedNodeChild(dataNode, ELEMENT_PREVIEW);
        if (prev != null)
        {
          d.setPreview(getBinaryElementContentWithUnsafetyText(prev));
        }
        Node data = getNamedNodeChild(dataNode, ELEMENT_DATA);
        if (data == null || getNamedNodeChild(data, ELEMENT_NULL_VALUE) != null)
        {
          d.setData(null);
        }
        else
        {
          d.setData(getBinaryElementContentWithUnsafetyText(data));
        }
        
        return d;
    }
    
    return ff.valueFromString(getElementContentWithUnsafetyText(valueNode, bundle, false));
  }
  
  /**
   * Creates a DOM node for provided Data Table
   */
  public static Node createDataTableNode(DataTable dt, Document doc, CallerController cc, ContextManager cm, XMLEncodingSettings settings) throws ParserConfigurationException, IOException,
      ContextException
  {
    if (!dt.isSimple())
    {
      dt = dt.clone();
    }

    Element dtNode = doc.createElement(ELEMENT_DATA_TABLE);
    if (dt.getId() != null)
    {
      dtNode.setAttribute(ATTRIBUTE_ID, dt.getId().toString());
    }
    
    TableFormat rf = dt.getFormat();
    if (rf != null)
    {
      if (settings.isEncodeFormat() || (settings.isGetFormatFromDefaultValue() && settings.getFieldFormat() != null && settings.getFieldFormat().getDefaultValue() == null))
      {
        // Building format department
        Element format = doc.createElement(ELEMENT_FORMAT);
        
        if (rf.getMinRecords() != TableFormat.DEFAULT_MIN_RECORDS)
        {
          format.setAttribute(ATTRIBUTE_MINIMUM_RECORDS, Integer.toString(rf.getMinRecords()));
        }
        if (rf.getMinRecords() != TableFormat.DEFAULT_MAX_RECORDS)
        {
          format.setAttribute(ATTRIBUTE_MAXIMUM_RECORDS, Integer.toString(rf.getMaxRecords()));
        }
        if (rf.isReorderable())
        {
          format.setAttribute(ATTRIBUTE_REORDERABLE, Boolean.toString(rf.isReorderable()));
        }
        if (rf.isUnresizable())
        {
          format.setAttribute(ATTRIBUTE_UNRESIZABLE, Boolean.toString(rf.isUnresizable()));
        }
        if (rf.getFields().size() > 0)
        {
          Element fields = doc.createElement(ELEMENT_FIELDS);
          for (FieldFormat ff : rf.getFields())
          {
            Element field = doc.createElement(ELEMENT_FIELD);
            field.setAttribute(ATTRIBUTE_NAME, ff.getName());
            field.setAttribute(ATTRIBUTE_TYPE, String.valueOf(ff.getType()));
            if (ff.hasDescription())
            {
              field.setAttribute(ATTRIBUTE_DESCRIPTION, ff.getDescription());
            }
            
            if (ff.isNullable())
            {
              field.setAttribute(ATTRIBUTE_NULLABLE, Boolean.toString(ff.isNullable()));
            }
            if (ff.isOptional())
            {
              field.setAttribute(ATTRIBUTE_OPTIONAL, Boolean.toString(ff.isOptional()));
            }
            if (ff.getIcon() != null)
            {
              field.setAttribute(ATTRIBUTE_ICON, ff.getIcon());
            }
            if (ff.isExtendableSelectionValues())
            {
              field.setAttribute(ATTRIBUTE_EXTENDABLE_SELECTION_VALUES, Boolean.toString(ff.isExtendableSelectionValues()));
            }
            if (ff.isReadonly())
            {
              field.setAttribute(ATTRIBUTE_READONLY, Boolean.toString(ff.isReadonly()));
            }
            if (ff.isNotReplicated())
            {
              field.setAttribute(ATTRIBUTE_NOT_REPLICATED, Boolean.toString(ff.isNotReplicated()));
            }
            if (ff.isHidden())
            {
              field.setAttribute(ATTRIBUTE_HIDDEN, Boolean.toString(ff.isHidden()));
            }
            if (ff.isInlineData())
            {
              field.setAttribute(ATTRIBUTE_INLINE, Boolean.toString(ff.isInlineData()));
            }
            if (ff.isAdvanced())
            {
              field.setAttribute(ATTRIBUTE_ADVANCED, Boolean.toString(ff.isAdvanced()));
            }
            if (ff.isKeyField())
            {
              field.setAttribute(ATTRIBUTE_KEY_FIELD, Boolean.toString(ff.isKeyField()));
            }
            if (ff.getEditor() != null)
            {
              field.setAttribute(ATTRIBUTE_EDITOR, ff.getEditor());
            }
            if (ff.getEditorOptions() != null)
            {
              Element eo = doc.createElement(ELEMENT_EDITOR_OPTIONS);
              setElementContentWithUnsafetyText(eo, ff.getEditorOptions());
              field.appendChild(eo);
            }
            
            String vals = ff.getEncodedValidators(ENCODE_VALIDATORS_SETTINGS);
            if (vals != null)
            {
              Element valsNode = doc.createElement(ELEMENT_VALIDATORS);
              setElementContentWithUnsafetyText(valsNode, vals);
              field.appendChild(valsNode);
            }
            
            // Adding selection values
            Map<Object, String> sv = ff.getSelectionValues();
            if (sv != null && sv.size() > 0)
            {
              Element selVals = doc.createElement(ELEMENT_SELECTION_VALUES);
              for (Object key : sv.keySet())
              {
                Element val = doc.createElement(ELEMENT_OPTION);
                val.setAttribute(ATTRIBUTE_DESCRIPTION, sv.get(key));
                encodeFieldValueToXML(ff, key, doc, val, cc, cm, settings);
                selVals.appendChild(val);
              }
              field.appendChild(selVals);
            }
            if (ff.getHelp() != null)
            {
              Element help = doc.createElement(ELEMENT_HELP);
              help.setTextContent(ff.getHelp());
              field.appendChild(help);
            }
            if (ff.getGroup() != null)
            {
              Element group = doc.createElement(ELEMENT_GROUP);
              group.setTextContent(ff.getGroup());
              field.appendChild(group);
            }
            
            Element dv = doc.createElement(ELEMENT_DEFAULT_VALUE);
            if (ff.getType() == FieldFormat.DATATABLE_FIELD && ff.getDefaultValue() != null
                && !AbstractDataTable.DEFAULT_FORMAT.equals(((DataTable) ff.getDefaultValue()).getFormat()))
            {
              encodeFieldValueToXML(ff, ff.getDefaultValue(), doc, dv, cc, cm,
                  new XMLEncodingSettings(true, settings.getFormat(), settings.isGetFormatFromDefaultValue(), settings.getFieldFormat()));
            }
            else
            {
              encodeFieldValueToXML(ff, ff.getDefaultValue(), doc, dv, cc, cm, settings);
            }
            field.appendChild(dv);
            
            fields.appendChild(field);
          }
          format.appendChild(fields);
        }
        
        if (!rf.getBindings().isEmpty())
        {
          Element bindings = createAndAppendChild(format, ELEMENT_BINDINGS);
          
          for (Binding b : rf.getBindings())
          {
            Element binding = createAndAppendChild(bindings, ELEMENT_BINDING);
            {
              Element reference = createAndAppendChild(binding, ELEMENT_REFERENCE);
              setElementContentWithUnsafetyText(reference, b.getTarget().toString());
              
              Element expression = createAndAppendChild(binding, ELEMENT_EXPRESSION);
              setElementContentWithUnsafetyText(expression, b.getExpression().toString());
            }
          }
        }
        
        String validators = rf.getEncodedTableValidators(ENCODE_VALIDATORS_SETTINGS);
        if (!StringUtils.isEmpty(validators))
        {
          Element validatorsNode = createAndAppendChild(format, ELEMENT_VALIDATORS);
          setElementContentWithUnsafetyText(validatorsNode, validators);
        }
        
        dtNode.appendChild(format);
      }
      
      // Building records part of document
      if (dt.getRecordCount() > 0)
      {
        Element recs = doc.createElement(ELEMENT_RECORDS);
        // Adding record node
        for (DataRecord dr : dt)
        {
          Element rec = doc.createElement(ELEMENT_RECORD);
          for (FieldFormat ff : rf.getFields())
          {
            Object fValue = dr.getValue(ff.getName());
            if (ff.getDefaultValue() == null && fValue == null && !settings.isAllFields())
            {
              continue;
            }
            if (settings.isAllFields() || ff.getDefaultValue() == null || !ff.getDefaultValue().equals(fValue))
            {
              Element field = doc.createElement(ELEMENT_FIELD_VALUE);
              field.setAttribute(ATTRIBUTE_NAME, ff.getName());
              if (ff.getType() == FieldFormat.DATATABLE_FIELD && (ff.getDefaultValue() == null || AbstractDataTable.DEFAULT_FORMAT.equals(((DataTable) ff.getDefaultValue()).getFormat())))
              {
                XMLEncodingSettings newSettings = new XMLEncodingSettings(true, settings.getFormat(), settings.isGetFormatFromDefaultValue(), settings.getFieldFormat(),
                    settings.getBundle());
                encodeFieldValueToXML(ff, fValue, doc, field, cc, cm, newSettings);
              }
              else
              {
                encodeFieldValueToXML(ff, fValue, doc, field, cc, cm, settings);
              }
              rec.appendChild(field);
            }
          }
          recs.appendChild(rec);
        }
        dtNode.appendChild(recs);
      }
    }
    
    return dtNode;
  }
  
  private static Element createAndAppendChild(Element parentElement, String elementName)
  {
    Element reference = parentElement.getOwnerDocument().createElement(elementName);
    parentElement.appendChild(reference);
    return reference;
  }
  
  /**
   * Reading DataTable from DOM node object. If DOM node contains no 'format' section then resulting TableFormat should be set to <code>givenFormat</code> if it is not null. Otherwise exception will
   * be thrown.
   *
   * @param dtNode
   *          Node
   * @param settings
   *          XMLEncodingSettings
   * @return DataTable
   * @throws java.io.IOException
   */
  public static DataTable readDataTableFromNode(Node dtNode, XMLEncodingSettings settings) throws IOException
  {
    NodeList children = dtNode.getChildNodes();
    Node format = null;
    Node records = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      if (children.item(i).getNodeName().equals(ELEMENT_FORMAT))
      {
        format = children.item(i);
      }
      else if (children.item(i).getNodeName().equals(ELEMENT_RECORDS))
      {
        records = children.item(i);
      }
    }
    // Obtaining DataTable format
    TableFormat rf;
    if (format == null)
    {
      if (settings.getFormat() == null)
      {
        if (settings.isGetFormatFromDefaultValue() && settings.getFieldFormat() != null && settings.getFieldFormat().getDefaultValue() != null)
        {
          rf = ((DataTable) settings.getFieldFormat().getDefaultValue()).getFormat();
        }
        else
        {
          throw new IOException("Cannot obtain format for this DataTable");
        }
      }
      else
      {
        rf = settings.getFormat();
      }
    }
    else
    {
      // Constructing TableFormat from format node
      rf = new TableFormat();
      NamedNodeMap formatAttr = format.getAttributes();
      for (int i = 0; i < formatAttr.getLength(); i++)
      {
        Node attr = formatAttr.item(i);
        if (attr.getNodeName().equals(ATTRIBUTE_MINIMUM_RECORDS))
        {
          rf.setMinRecords(Integer.valueOf(attr.getNodeValue()));
        }
        else if (attr.getNodeName().equals(ATTRIBUTE_MAXIMUM_RECORDS))
        {
          rf.setMaxRecords(Integer.valueOf(attr.getNodeValue()));
        }
        else if (attr.getNodeName().equals(ATTRIBUTE_REORDERABLE))
        {
          rf.setReorderable(Boolean.valueOf(attr.getNodeValue()));
        }
        else if (attr.getNodeName().equals(ATTRIBUTE_UNRESIZABLE))
        {
          rf.setUnresizable(Boolean.valueOf(attr.getNodeValue()));
        }
      }
      
      Node bindings = getNamedNodeChild(format, ELEMENT_BINDINGS);
      if (bindings != null)
      {
        final NodeList bindingNodes = ((Element) bindings).getElementsByTagName(ELEMENT_BINDING);
        for (int i = 0; i < bindingNodes.getLength(); i++)
        {
          final Element bindingElement = (Element) bindingNodes.item(i);
          final Node referenceNode = getNamedNodeChild(bindingElement, ELEMENT_REFERENCE);
          final Node expressionNode = getNamedNodeChild(bindingElement, ELEMENT_EXPRESSION);
          
          rf.addBinding(referenceNode.getTextContent(), expressionNode.getTextContent());
        }
      }
      
      Node validators = getNamedNodeChild(format, ELEMENT_VALIDATORS);
      if (validators != null)
      {
        String vals = getElementContentWithUnsafetyText(validators, null, false);
        try
        {
          rf.createTableValidators(vals, new ClassicEncodingSettings(ExpressionUtils.useVisibleSeparators(vals)));
        }
        catch (SyntaxErrorException ex)
        {
          // vals == null, do nothing
        }
      }
      
      Node fields = getNamedNodeChild(format, ELEMENT_FIELDS);
      if (fields != null)
      {
        NodeList fs = fields.getChildNodes();
        for (int i = 0; i < fs.getLength(); i++)
        {
          if (fs.item(i).getNodeName().equals(ELEMENT_FIELD))
          {
            rf.addField(readFieldFormatFromNode(fs.item(i)));
          }
        }
      }
    }
    
    DataTable dt = new SimpleDataTable(rf);
    
    // Processing id attribute if exists
    Node id = dtNode.getAttributes().getNamedItem(ATTRIBUTE_ID);
    if (id != null)
    {
      dt.setId(Long.valueOf(id.getNodeValue()));
    }
    
    // Processing records element
    if (records != null)
    {
      NodeList recs = records.getChildNodes();
      for (int i = 0; i < recs.getLength(); i++)
      {
        if (recs.item(i).getNodeName().equals(ELEMENT_RECORD))
        {
          DataRecord dr = dt.addRecord();
          NodeList rFieldValues = recs.item(i).getChildNodes();
          // Processing field values for current record
          for (int j = 0; j < rFieldValues.getLength(); j++)
          {
            Node f = rFieldValues.item(j);
            if (f.getNodeName().equals(ELEMENT_FIELD_VALUE))
            {
              Node fName = f.getAttributes().getNamedItem(ATTRIBUTE_NAME);
              if (fName != null)
              {
                FieldFormat cff = rf.getField(fName.getTextContent());
                if (cff != null)
                {
                  dr.setValue(cff.getName(), decodeFieldValueFromXML(cff, f, settings.getBundle()));
                }
              }
            }
          }
        }
      }
    }
    return dt;
  }
  
  private static Node getNamedNodeChild(Node node, String child)
  {
    NodeList children = node.getChildNodes();
    Node childNode = null;
    for (int i = 0; i < children.getLength(); i++)
    {
      if (children.item(i).getNodeName().equals(child))
      {
        childNode = children.item(i);
        break;
      }
    }
    return childNode;
  }
  
  /**
   * Returns new {@link Document} object. Created to simplify new DOM {@link Document} object creation process.
   *
   * @return Document
   * @throws ParserConfigurationException
   */
  public static Document createNewDocument() throws ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.newDocument();
  }
  
  /**
   * Compiles XPath query and evaluates it on provided Document object. Returns result of this query.
   *
   * @param doc
   * @param expression
   * @param resultType
   * @throws XPathExpressionException
   */
  public static Object evaluateXPathExpression(Document doc, String expression, QName resultType) throws XPathExpressionException
  {
    XPathFactory factory = XPathFactory.newInstance();
    XPath xpath = factory.newXPath();
    
    XPathExpression expr = xpath.compile(expression);
    
    return expr.evaluate(doc, resultType);
  }
}
