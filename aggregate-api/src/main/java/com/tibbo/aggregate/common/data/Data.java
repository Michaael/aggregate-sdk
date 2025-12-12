package com.tibbo.aggregate.common.data;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.net.util.Base64;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.base.Splitter;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.StringEncodable;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

public class Data implements Cloneable, PublicCloneable, StringEncodable
{
  private static final int TRANSCODER_VERSION = 0;
  public static final Float BUFFER_MULTIPLIER = new Float(1.15);
  
  private static final char SEPARATOR = '/';
  private static final String NULL = "null";
  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String PREVIEW = "preview";
  private static final String DATA = "data";
  private static final String SHALLOW_COPY = "shallowCopy";
  
  private Long id;
  private String name;
  private byte[] preview;
  private byte[] data;
  private boolean shallowCopy = false;
  
  private Map<String, Object> attachments = new HashMap();
  
  public Data()
  {
    super();
  }
  
  public Data(byte[] data)
  {
    super();
    this.data = data;
  }
  
  public Data(String name, byte[] data)
  {
    this(data);
    this.name = name;
  }
  
  public Data(String value)
  {
    
    if (value.length() == 0)
    {
      return;
    }
    
    if (isJson(value))
    {
      fromJsonString(value);
      return;
    }
    
    List<String> parts = Splitter.on(SEPARATOR).limit(6).splitToList(value);
    
    // parts.get(0) will return transcoder version, currently ignored
    
    if (!parts.get(1).equals(DataTableUtils.DATA_TABLE_NULL))
    {
      setId(Long.valueOf(parts.get(1)));
    }
    
    if (!parts.get(2).equals(DataTableUtils.DATA_TABLE_NULL))
    {
      setName(parts.get(2));
    }
    
    int previewLen = Integer.valueOf(parts.get(3));
    
    if (previewLen != -1)
    {
      setPreview(parts.get(5).substring(0, previewLen).getBytes(StringUtils.ASCII_CHARSET));
    }
    
    int dataLen = Integer.valueOf(parts.get(4));
    
    if (dataLen != -1)
    {
      setData(parts.get(5).substring(previewLen <= 0 ? 0 : previewLen).getBytes(StringUtils.ASCII_CHARSET));
    }
  }
  
  private boolean isJson(String value)
  {
    try
    {
      JSONParser parser = new JSONParser();
      parser.parse(value);
    }
    catch (ParseException ex)
    {
      return false;
    }
    return true;
  }
  
  private void fromJsonString(String json)
  {
    Map<String, String> dataMap = new HashMap<>();
    String formattedString = json.replace("{", "")
        .replace("}", "")
        .replace("\"", "")
        .replace(" ", "");
    List<String> dataList = Arrays.asList(formattedString.split(","));
    dataList.forEach(data -> {
      String[] pair = data.split(":");
      if (pair.length == 2)
      {
        dataMap.put(pair[0], pair[1]);
      }
    });
    
    if (dataMap.containsKey(ID))
    {
      String id = dataMap.get(ID);
      if (!id.equals(NULL))
      {
        setId(Long.parseLong(id));
      }
    }
    if (dataMap.containsKey(NAME))
    {
      String name = dataMap.get(NAME);
      if (!name.equals(NULL))
      {
        setName(dataMap.get(NAME));
      }
    }
    if (dataMap.containsKey(PREVIEW))
    {
      String preview = dataMap.get(PREVIEW);
      if (!preview.equals(NULL))
      {
        setPreview(dataMap.get(PREVIEW).getBytes());
      }
    }
    if (dataMap.containsKey(DATA))
    {
      String data = dataMap.get(DATA);
      if (!data.equals(NULL))
      {
        if (Base64.isArrayByteBase64(data.getBytes()))
        {
          setData(Base64.decodeBase64(dataMap.get(DATA)));
        }
      }
    }
    if (dataMap.containsKey(SHALLOW_COPY))
    {
      String data = dataMap.get(SHALLOW_COPY);
      if (!data.equals(NULL))
      {
        setShallowCopy(Boolean.parseBoolean(dataMap.get(SHALLOW_COPY)));
      }
    }
  }
  
  public void setPreview(byte[] preview)
  {
    this.preview = preview;
  }
  
  public void setId(Long id)
  {
    this.id = id;
  }
  
  public void setData(byte[] data)
  {
    this.data = data;
  }
  
  public void setBlob(byte[] blob)
  {
    this.data = blob;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public byte[] getPreview()
  {
    return preview;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Long getId()
  {
    return id;
  }
  
  public byte[] getData()
  {
    return data;
  }
  
  public byte[] getBlob()
  {
    return data;
  }
  
  public Map<String, Object> getAttachments()
  {
    return attachments;
  }
  
  public void setShallowCopy(boolean shallowCopy)
  {
    this.shallowCopy = shallowCopy;
  }
  
  public boolean isShallowCopy()
  {
    return shallowCopy;
  }
  
  public byte[] fetchData(ContextManager cm, CallerController cc) throws ContextException
  {
    if (getData() != null)
    {
      return getData();
    }
    
    if (getId() == null)
    {
      return null;
    }
    
    if (cm == null)
    {
      return null;
    }
    
    DataTable dt = cm.get(Contexts.CTX_UTILITIES, cc).callFunction(UtilitiesContextConstants.F_GET_DATA, cc, getId());
    
    byte[] receivedData = dt.rec().getData(UtilitiesContextConstants.FOF_GET_DATA_DATA).getData();
    
    setData(receivedData);
    
    return receivedData;
  }
  
  public byte[] fetchPreviewData(ContextManager cm, CallerController cc) throws ContextException
  {
    if (getPreview() != null)
    {
      return getPreview();
    }
    
    if (getId() == null)
    {
      return null;
    }
    
    if (cm == null)
    {
      return null;
    }
    
    DataTable dt = cm.get(Contexts.CTX_UTILITIES, cc).callFunction(UtilitiesContextConstants.F_GET_DATA, cc, getId());
    
    byte[] receivedData = dt.rec().getData(UtilitiesContextConstants.FOF_GET_DATA_DATA).getPreview();
    
    setPreview(receivedData);
    
    return receivedData;
  }
  
  private int checksum(byte[] bytes)
  {
    int sum = 0;
    for (byte b : bytes)
    {
      sum += b;
    }
    return sum;
  }
  
  public String toDetailedString()
  {
    return "Data [id: " + (id != null ? id : "null") + ", name: " + (name != null ? id : "null") + ", preview: "
        + (preview != null ? "len=" + preview.length + " checksum=" + checksum(preview) : "null") + ", data: " + (data != null ? "len=" + data.length + " checksum=" + checksum(data) : "null") + "]";
  }
  
  @Override
  public String toString()
  {
    // return toDetailedString();
    return "Data [id: " + (id != null ? id : "null") + ", name: " + (name != null ? name : "null") + ", preview: " + (preview != null ? "len=" + preview.length : "null") + ", data: "
        + (data != null ? "len=" + data.length : "null" + ", shallow copy: " + shallowCopy) + "]";
  }
  
  public String toJsonString()
  {
    return "{\"id\": " + (id != null ? "\"" + id + "\"" : "null") + ", \"name\": " + (name != null ? "\"" + name + "\"" : "null") + ", \"preview\": " + "\""
        + (preview != null ? "len=" + preview.length : "null")
        + "\"" + ", \"data\": "
        + (data != null ? "\"" + Base64.encodeBase64StringUnChunked(data) + "\"" : "null") + ", \"shallow copy\": " + shallowCopy + "}";
  }
  
  public String toCleanString()
  {
    return data != null ? new String(data, StandardCharsets.ISO_8859_1) : "null";
  }
  
  @Override
  public Data clone()
  {
    Data cl = null;
    
    try
    {
      cl = (Data) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    if (!isShallowCopy())
    {
      cl.preview = (byte[]) CloneUtils.deepClone(preview);
      cl.data = (byte[]) CloneUtils.deepClone(data);
    }
    return cl;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Data)
    {
      Data od = (Data) obj;
      return Util.equals(this.id, od.id) && Util.equals(this.name, od.name) && Arrays.equals(od.preview, this.preview) && Arrays.equals(this.data, od.data);
    }
    return false;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
    result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
    result = prime * result + ((this.preview == null) ? 0 : this.preview.hashCode());
    result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
    return result;
  }
  
  public void setAttachments(Map<String, Object> attachments)
  {
    this.attachments = attachments;
  }
  
  public String encode()
  {
    return encode(new StringBuilder(), null, false, 0).toString();
  }
  
  @Override
  public StringBuilder encode(StringBuilder sb, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (sb.length() + (estimateDataSize() * BUFFER_MULTIPLIER) > sb.capacity())
      sb.ensureCapacity((int) (sb.capacity() + (estimateDataSize() * BUFFER_MULTIPLIER)));
    
    StringBuilder tempSB = new StringBuilder();
    
    tempSB.append(TRANSCODER_VERSION);
    
    tempSB.append(SEPARATOR);
    
    tempSB.append(getId() != null ? String.valueOf(getId()) : DataTableUtils.DATA_TABLE_NULL);
    
    tempSB.append(SEPARATOR);
    
    tempSB.append(getName() != null ? getName() : DataTableUtils.DATA_TABLE_NULL);
    
    tempSB.append(SEPARATOR);
    
    tempSB.append(getPreview() != null ? String.valueOf(getPreview().length) : String.valueOf(-1));
    
    tempSB.append(SEPARATOR);
    
    tempSB.append(getData() != null ? String.valueOf(getData().length) : String.valueOf(-1));
    
    tempSB.append(SEPARATOR);
    
    if (isTransferEncode)
      sb.append(DataTableUtils.transferEncode(tempSB.toString()));
    else
      sb.append(tempSB);
    
    appendBytes(sb, getPreview(), isTransferEncode, encodeLevel);
    appendBytes(sb, getData(), isTransferEncode, encodeLevel);
    
    return sb;
  }
  
  public int estimateDataSize()
  {
    Integer size = String.valueOf(Long.MAX_VALUE).length();
    if (getName() != null)
      size += getName().length();
    if (getPreview() != null)
      size += getPreview().length + String.valueOf(getPreview().length).length();
    if (getData() != null)
      size += getData().length + String.valueOf(getData().length).length();
    return size;
  }
  
  private void appendBytes(StringBuilder sb, byte[] data, boolean isTransferEncode, Integer encodeLevel)
  {
    if (data != null)
    {
      Integer newLength = (int) ((data.length + sb.length()) * BUFFER_MULTIPLIER);
      
      if (Log.DATATABLE.isDebugEnabled())
      {
        Log.DATATABLE.debug("Data length: " + data.length);
        Log.DATATABLE.debug("Buffer length: " + sb.length());
        Log.DATATABLE.debug("Buffer new length: " + newLength);
      }
      
      sb.ensureCapacity(newLength);
      
      // Оптимизация: исправлена ошибка в условии цикла (было i <= data.length)
      // Используем ArrayUtils.subarray для эффективного создания подмассива
      for (int i = 0; i < data.length; i += TransferEncodingHelper.LARGE_DATA_SIZE)
      {
        int end = Math.min(i + TransferEncodingHelper.LARGE_DATA_SIZE, data.length);
        
        // Используем ArrayUtils.subarray вместо создания нового массива
        String tempString = new String(ArrayUtils.subarray(data, i, end), StringUtils.ASCII_CHARSET);
        if (isTransferEncode)
          TransferEncodingHelper.encode(tempString, sb, encodeLevel);
        else
          sb.append(tempString);
      }
    }
  }
  
  public void releaseData()
  {
    data = null;
    preview = null;
  }
}