package com.tibbo.aggregate.common.util;

import java.awt.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.Map.*;

import com.google.common.base.Splitter;
import com.tibbo.aggregate.common.datatable.*;

import static com.google.common.collect.Lists.newArrayList;

public class StringUtils
{
  public final static Charset UTF8_CHARSET = Charset.forName("UTF-8");
  public final static Charset ASCII_CHARSET = Charset.forName("ISO-8859-1");
  public static final Charset WINDOWS_1251_CHARSET = Charset.forName("windows-1251");
  
  public final static String DEFAULT_COLLECTION_PRINT_SEPARATOR = ", ";
  public final static String DEFAULT_MAP_KEY_VALUE_SEPARATOR = ": ";
  
  public static String truncate(String str, int maxLength)
  {
    return truncate(str, maxLength, null);
  }
  
  public static String truncate(String str, int maxLength, String suffix)
  {
    String res = str.length() <= maxLength ? str : str.substring(0, maxLength);
    if (suffix != null && str.length() > maxLength)
    {
      res += suffix;
    }
    return res;
  }
  
  public static String byteToHexString(int i)
  {
    String str = Integer.toHexString(i & 0xFF).toUpperCase(Locale.ENGLISH);
    if (str.length() == 1)
    {
      str = "0" + str;
    }
    return str;
  }
  
  public static String colorToString(Color color)
  {
    if (color == null)
    {
      return null;
    }
    
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder s = new StringBuilder("#");
    
    s.append(byteToHexString(Integer.valueOf(color.getRed()).byteValue()));
    s.append(byteToHexString(Integer.valueOf(color.getGreen()).byteValue()));
    s.append(byteToHexString(Integer.valueOf(color.getBlue()).byteValue()));
    s.append(byteToHexString(Integer.valueOf(color.getAlpha()).byteValue()));
    
    return s.toString();
  }
  
  public static ElementList elements(String source, boolean useVisibleSeparators)
  {
    ElementList res = new ElementList();
    
    final char elStart = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_START : DataTableUtils.ELEMENT_START;
    final char elEnd = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_END : DataTableUtils.ELEMENT_END;
    final char elNameValSep = useVisibleSeparators ? DataTableUtils.ELEMENT_VISIBLE_NAME_VALUE_SEPARATOR : DataTableUtils.ELEMENT_NAME_VALUE_SEPARATOR;
    
    int depth = 0;
    int startPos = -1;
    int nameValSepPos = -1;
    
    int len = source.length();
    
    for (int i = 0; i < len; i++)
    {
      char c = source.charAt(i);
      
      if (c == elStart)
      {
        depth++;
        
        if (depth == 1)
        {
          startPos = i;
        }
      }
      
      if (c == elNameValSep)
      {
        if (depth == 1 && nameValSepPos == -1)
        {
          nameValSepPos = i;
        }
      }
      
      if (c == elEnd)
      {
        depth--;
        
        if (depth < 0)
        {
          int min = Math.max(0, i - 10);
          throw new IllegalArgumentException("Invalid closing element at position " + i + " (" + source.substring(min, i) + ")");
        }
        
        if (depth == 0)
        {
          String name = null;
          String value = null;
          
          if (nameValSepPos == -1)
          {
            value = source.substring(startPos + 1, i);
          }
          else
          {
            name = source.substring(startPos + 1, nameValSepPos);
            value = source.substring(nameValSepPos + 1, i);
          }
          
          res.add(new Element(name, value));
          
          nameValSepPos = -1;
        }
      }
    }
    
    if (depth >= 1)
    {
      throw new IllegalArgumentException(
          "Missing closing element(s): " + (source.length() > 1000 ? source.substring(0, 500) + "....." + source.substring(source.length() - 501, source.length() - 1) : source));
    }
    
    return res;
  }
  
  public static String escapeHtml(String text)
  {
    if (text == null)
    {
      return null;
    }
    
    final StringBuilder result = new StringBuilder();
    final StringCharacterIterator iterator = new StringCharacterIterator(text);
    char character = iterator.current();
    while (character != CharacterIterator.DONE)
    {
      if (character == '<')
      {
        result.append("&lt;");
      }
      else if (character == '>')
      {
        result.append("&gt;");
      }
      else if (character == '&')
      {
        result.append("&amp;");
      }
      else if (character == '\"')
      {
        result.append("&quot;");
      }
      else if (character == '\'')
      {
        result.append("&#039;");
      }
      else if (character == '(')
      {
        result.append("&#040;");
      }
      else if (character == ')')
      {
        result.append("&#041;");
      }
      else if (character == '#')
      {
        result.append("&#035;");
      }
      else if (character == '%')
      {
        result.append("&#037;");
      }
      else if (character == ';')
      {
        result.append("&#059;");
      }
      else if (character == '+')
      {
        result.append("&#043;");
      }
      else if (character == '-')
      {
        result.append("&#045;");
      }
      else
      {
        // the char is not a special one
        // add it to the result as is
        result.append(character);
      }
      
      character = iterator.next();
    }
    return result.toString();
  }
  
  public static String createMaskedPasswordString(int length)
  {
    StringBuilder buf = new StringBuilder(length);
    
    for (int i = 0; i < length; ++i)
    {
      buf.append('\u2022');
    }
    
    return buf.toString();
  }
  
  public static String print(Collection col)
  {
    return print(col, DEFAULT_COLLECTION_PRINT_SEPARATOR, false);
  }
  
  public static String print(Collection col, String separator)
  {
    return print(col, separator, false);
  }
  
  public static String print(Collection col, String separator, boolean skipNullElements)
  {
    return print(col, separator, null, skipNullElements);
  }
  
  public static String print(Collection col, String separator, String escaper, boolean skipNullElements)
  {
    if (col == null)
    {
      return "null";
    }
    
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder res = new StringBuilder();
    int i = 0;
    
    for (Object elem : col)
    {
      if (elem == null && skipNullElements)
      {
        continue;
      }
      
      if (i > 0)
      {
        res.append(separator);
      }
      
      i++;
      
      res.append(elem != null ? escaper != null ? escaper + elem.toString() + escaper : elem.toString() : "null");
    }
    
    return res.toString();
  }
  
  public static String print(Map map)
  {
    return print(map, DEFAULT_COLLECTION_PRINT_SEPARATOR, DEFAULT_MAP_KEY_VALUE_SEPARATOR, false);
  }
  
  public static String print(Map<Object, Object> map, String separator, String keyValueSeparator, boolean skipNullKeys)
  {
    if (map == null)
    {
      return "null";
    }
    
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder res = new StringBuilder();
    int i = 0;
    
    for (Entry entry : map.entrySet())
    {
      if (entry.getKey() == null && skipNullKeys)
      {
        continue;
      }
      
      if (i > 0)
      {
        res.append(separator);
      }
      
      i++;
      
      res.append((entry.getKey() != null ? entry.getKey().toString() : "null") + keyValueSeparator + (entry.getValue() != null ? entry.getValue().toString() : "null"));
    }
    
    return res.toString();
  }
  
  public static String print(Object[] array)
  {
    return print(Arrays.asList(array), DEFAULT_COLLECTION_PRINT_SEPARATOR);
  }
  
  public static String print(Object[] array, String separator)
  {
    return print(Arrays.asList(array), separator);
  }
  
  public static byte[] removeBOM(byte[] utf8ByteArray)
  {
    if (utf8ByteArray != null && utf8ByteArray.length >= 3 && utf8ByteArray[0] == (byte) 0xEF && utf8ByteArray[1] == (byte) 0xBB && utf8ByteArray[2] == (byte) 0xBF)
    {
      utf8ByteArray = Arrays.copyOfRange(utf8ByteArray, 3, utf8ByteArray.length);
    }
    
    return utf8ByteArray;
  }
  
  public static String remoteNonDigits(String src)
  {
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder buf = new StringBuilder();
    
    for (int i = 0; i < src.length(); i++)
    {
      char ch = src.charAt(i);
      if (Character.isDigit(ch))
      {
        buf.append(ch);
      }
    }
    
    return buf.toString();
  }
  
  public static String removeSuffix(String src, String suffix)
  {
    if (src.endsWith(suffix))
    {
      return src.substring(0, src.lastIndexOf(suffix));
    }
    
    return null;
  }
  
  public static List<String> split(String str, char ch)
  {
    return newArrayList(Splitter.on(ch).splitToList(str));
  }
  
  public static String streamToString(InputStream is, String charsetName) throws IOException
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, charsetName));
    StringBuilder sb = new StringBuilder();
    
    String line = null;
    try
    {
      while ((line = reader.readLine()) != null)
      {
        sb.append(line + "\n");
      }
    }
    finally
    {
      is.close();
    }
    
    return sb.toString();
  }
  
  public static List<String> wrapText(String text, int len)
  {
    // return empty array for null text
    if (text == null)
    {
      return new ArrayList<String>();
    }
    
    // return text if len is zero or less
    if (len <= 0)
    {
      List<String> lines = new ArrayList<String>(1);
      lines.add(text);
      return lines;
    }
    
    // return text if less than length
    if (text.length() <= len)
    {
      List<String> lines = new ArrayList<String>(1);
      lines.add(text);
      return lines;
    }
    
    char[] chars = text.toCharArray();
    // Оптимизация: используем ArrayList вместо LinkedList
    // Оцениваем примерное количество строк: text.length() / len
    int estimatedLines = Math.max(1, text.length() / len + 1);
    List<String> lines = new ArrayList<String>(estimatedLines);
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder line = new StringBuilder();
    StringBuilder word = new StringBuilder();
    
    for (char c : chars)
    {
      word.append(c);
      
      if (c == ' ')
      {
        if ((line.length() + word.length()) > len)
        {
          lines.add(line.toString());
          line.delete(0, line.length());
        }
        
        line.append(word);
        word.delete(0, word.length());
      }
    }
    
    // handle any extra chars in current word
    
    if (word.length() > 0)
    {
      if ((line.length() + word.length()) > len)
      {
        lines.add(line.toString());
        line.delete(0, line.length());
      }
      line.append(word);
    }
    
    // handle extra line
    if (line.length() > 0)
    {
      lines.add(line.toString());
    }
    
    return lines;
  }
  
  public static String wrapText(String text, int len, String separator)
  {
    List<String> lines = wrapText(text, len);
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.size(); i++)
    {
      if (i > 0)
      {
        sb.append(separator);
      }
      sb.append(lines.get(i));
    }
    return sb.toString();
  }
  
  public static void appendLine(StringBuilder sb, String s)
  {
    sb.append(s);
    sb.append("\n");
  }
  
  public static String makeName(String source, int maxLength)
  {
    if (source == null)
    {
      return null;
    }
    
    return source.substring(0, Math.min(source.length(), maxLength));
  }
  
  public static boolean isPureAscii(String v)
  {
    byte bytearray[] = v.getBytes();
    CharsetDecoder d = ASCII_CHARSET.newDecoder();
    try
    {
      CharBuffer r = d.decode(ByteBuffer.wrap(bytearray));
      r.toString();
    }
    catch (CharacterCodingException e)
    {
      return false;
    }
    return true;
  }
  
  public static boolean isEmpty(String text)
  {
    return text == null || text.isEmpty();
  }
  
  public static Map<String, String> getEncodings()
  {
    SortedMap map = new TreeMap();
    SortedMap charsets = Charset.availableCharsets();
    
    Iterator i = charsets.keySet().iterator();
    
    while (i.hasNext())
    {
      String canonicalName = (String) i.next();
      Charset chr = (Charset) charsets.get(canonicalName);
      String displayName = chr.displayName();
      
      map.put(canonicalName, displayName);
    }
    
    return map;
  }
  
  public static String splitCapitalLetters(String str)
  {
    return str.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
  }
  
  public static String toHexString(byte[] byteArray)
  {
    return toHexString(byteArray, " ");
  }
  
  public static String toHexString(byte[] byteArray, String separator)
  {
    if (byteArray == null)
      return "null";
    
    StringBuilder sb = new StringBuilder(byteArray.length * 3);
    String format = "%02X" + separator;
    
    for (byte b : byteArray)
      sb.append(String.format(format, b));
    
    if (sb.length() > 0)
      sb.delete(sb.length() - 1, sb.length());
    
    return sb.toString();
  }
  
  public static String fromHexString(String hexString, String separator)
  {
    String[] str = hexString.split(separator);
    ByteBuffer bb = ByteBuffer.allocate(str.length);
    
    try
    {
      for (String s : str)
      {
        Integer i = Integer.valueOf(s, 16);
        bb.put((byte) (int) i);
      }
    }
    catch (NumberFormatException ex)
    {
      return null;
    }
    
    return new String(bb.array(), Charset.forName("UTF-8"));
  }
}
