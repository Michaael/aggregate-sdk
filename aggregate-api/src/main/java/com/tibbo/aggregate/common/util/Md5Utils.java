package com.tibbo.aggregate.common.util;

import java.security.*;

public class Md5Utils
{
  private static final String MD5 = "MD5";
  public final static int RESPONSE_LEN = 16;
  
  public static String hexHash(String source)
  {
    MessageDigest md = getMessageDigest();
    
    md.update(source.getBytes());
    
    byte[] md5 = md.digest();
    
    return hexRepresentation(md5);
  }
  
  public static String hexRepresentation(byte[] md5)
  {
    // Оптимизация: используем StringBuilder вместо StringBuffer
    StringBuilder result;
    result = new StringBuilder();
    for (int i = 0; i < RESPONSE_LEN; i++)
    {
      String str = StringUtils.byteToHexString(md5[i]);
      result.append(str);
    }
    
    return result.toString();
  }
  
  public static MessageDigest getMessageDigest()
  {
    MessageDigest md = null;
    try
    {
      md = MessageDigest.getInstance(MD5);
    }
    catch (NoSuchAlgorithmException ex)
    {
      throw new RuntimeException("Error creating MD5 hash", ex);
    }
    return md;
  }
}
