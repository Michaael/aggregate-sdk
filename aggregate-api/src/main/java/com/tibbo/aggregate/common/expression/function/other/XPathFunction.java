package com.tibbo.aggregate.common.expression.function.other;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.AbstractFunction;
import com.tibbo.aggregate.common.util.Util;

public class XPathFunction extends AbstractFunction
{
  public static final String FIELD_NODE_NAME = "nodeName";
  public static final String FIELD_NODE_CONTENT = "nodeContent";
  public static final String FIELD_NODE_CHILDREN = "nodeChildren";
  
  public XPathFunction()
  {
    super("xpath", Function.GROUP_OTHER, "String xml, String query [, Boolean table]", "Object", Cres.get().getString("fDescXPath"));
  }
  
  @Override
  public Object execute(Evaluator evaluator, EvaluationEnvironment environment, Object... parameters) throws EvaluationException
  {
    checkParameters(2, false, parameters);
    
    try
    {
      boolean toTable = parameters.length >= 3 ? Util.convertToBoolean(parameters[2], true, false) : false;
      
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      
      builder.setEntityResolver((publicId, systemId) -> {
        return new InputSource(new StringReader("")); // Returns a valid dummy source
      });
      
      Document doc = builder.parse(new InputSource(new StringReader(parameters[0].toString())));
      
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr = xpath.compile(parameters[1].toString());
      NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
      List<Node> nodes = nodesToList(nodeList);
      
      if (toTable)
      {
        return tableFromNodes(nodes);
      }
      else
      {
        return stringFromNodes(nodes);
      }
    }
    catch (Exception ex)
    {
      throw new EvaluationException(ex.getMessage(), ex);
    }
  }
  
  private String stringFromNodes(List<Node> nodes) throws TransformerFactoryConfigurationError, TransformerException
  {
    StringBuilder result = new StringBuilder();
    
    boolean appendLine = nodes.size() > 1;
    
    for (int i = 0; i < nodes.size(); i++)
    {
      Node node = nodes.get(i);
      if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      {
        result.append(node.getNodeValue());
        if (appendLine && i < (nodes.size() - 1))
          result.append(System.lineSeparator());
      }
      else
      {
        StringWriter buf = new StringWriter();
        Transformer xform = TransformerFactory.newInstance().newTransformer();
        xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        xform.setOutputProperty(OutputKeys.INDENT, "yes");
        xform.transform(new DOMSource(node), new StreamResult(buf));
        result.append(buf.toString());
      }
    }
    
    return result.toString();
  }
  
  private DataTable tableFromNodes(List<Node> nodes)
  {
    TableFormat format = formatFromNodes(nodes);
    
    DataTable result = new SimpleDataTable(format);
    
    for (Node node : nodes)
    {
      if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      {
        buildAttributeNode(node, result);
      }
      else if (node.getNodeType() == Node.ELEMENT_NODE)
      {
        buildElementNode(node, result);
      }
    }
    
    return result;
  }
  
  private void buildElementNode(Node node, DataTable result)
  {
    DataRecord rec = result.addRecord();
    
    rec.setValue(FIELD_NODE_NAME, node.getNodeName());
    rec.setValue(FIELD_NODE_CONTENT, node.getFirstChild() == null ? null : node.getFirstChild().getNodeValue());
    rec.setValue(FIELD_NODE_CHILDREN, tableFromNodes(nodesToList(node.getChildNodes())));
    
    NamedNodeMap attributes = node.getAttributes();
    if (attributes != null)
    {
      for (int i = 0; i < attributes.getLength(); i++)
      {
        Node attribute = attributes.item(i);
        
        rec.setValue(buildAttributeFieldName(attribute), attribute.getNodeValue());
      }
    }
  }
  
  private void buildAttributeNode(Node node, DataTable result)
  {
    DataRecord rec = result.addRecord();
    
    rec.setValue(FIELD_NODE_NAME, node.getNodeName());
    String nodeFormatName = Util.descriptionToName(node.getNodeName());
    rec.setValue(nodeFormatName, node.getNodeValue());
  }
  
  private TableFormat formatFromNodes(List<Node> nodes)
  {
    TableFormat format = new TableFormat();
    
    format.addField(FieldFormat.create("<" + FIELD_NODE_NAME + "><S><D=" + Cres.get().getString("name") + ">"));
    format.addField(FieldFormat.create("<" + FIELD_NODE_CONTENT + "><S><F=N><D=" + Cres.get().getString("content") + ">"));
    format.addField(FieldFormat.create("<" + FIELD_NODE_CHILDREN + "><T><D=" + Cres.get().getString("children") + ">"));
    
    for (Node node : nodes)
    {
      if (node.getNodeType() == Node.ATTRIBUTE_NODE)
      {
        String nodeName = Util.descriptionToName(node.getNodeName());
        if (!format.hasField(nodeName))
          format.addField(FieldFormat.create(Util.descriptionToName(node.getNodeName()), FieldFormat.STRING_FIELD));
      }
      else
      {
        NamedNodeMap attributes = node.getAttributes();
        
        if (attributes != null)
        {
          for (int i = 0; i < attributes.getLength(); i++)
          {
            Node attribute = attributes.item(i);
            
            FieldFormat attrField = FieldFormat.create(buildAttributeFieldName(attribute), FieldFormat.STRING_FIELD);
            
            attrField.setNullable(true);
            
            if (!format.hasField(attrField.getName()))
            {
              format.addField(attrField);
            }
          }
        }
      }
    }
    
    return format;
  }
  
  private List<Node> nodesToList(NodeList nodeList)
  {
    List<Node> nodes = new LinkedList<>();
    
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      nodes.add(nodeList.item(i));
    }
    return nodes;
  }
  
  private String buildAttributeFieldName(Node attribute)
  {
    return Util.descriptionToName(attribute.getNodeName());
  }
}
