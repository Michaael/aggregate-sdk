package com.tibbo.aggregate.common.util;

import java.io.*;
import java.nio.charset.*;
import java.security.spec.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class DesEncrypter
{
  public static final String ENC_KEY = "AGG_ENC_KEY";

  private Cipher ecipher;
  private Cipher dcipher;
  
  private final byte[] salt = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03 };
  
  private final int iterationCount = 19;
  
  private final byte[] buf = new byte[1024];

  public static DesEncrypter GLOBAL_ENCRYPTER;

  static
  {
    try
    {
      String p = System.getenv(DesEncrypter.ENC_KEY);

      GLOBAL_ENCRYPTER = p == null ? null : new DesEncrypter(p);
    }
    catch (Throwable e)
    {
      GLOBAL_ENCRYPTER = null;
    }
  }

  public DesEncrypter(String passPhrase) throws InvalidKeySpecException
  {
    try
    {
      KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
      SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
      ecipher = Cipher.getInstance(key.getAlgorithm());
      dcipher = Cipher.getInstance(key.getAlgorithm());
      
      AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
      
      ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
      dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }
    catch (InvalidKeySpecException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error creating DES encrypter: " + ex.getMessage(), ex);
    }
  }
  
  public OutputStream encryptedStream(OutputStream out) throws IOException
  {
    return new CipherOutputStream(out, ecipher);
  }
  
  public InputStream decryptedStream(InputStream in)
  {
    return new CipherInputStream(in, dcipher);
  }
  
  public void encrypt(InputStream in, OutputStream out) throws IOException
  {
    OutputStream os = encryptedStream(out);
    
    int numRead = 0;
    while ((numRead = in.read(buf)) >= 0)
    {
      os.write(buf, 0, numRead);
    }
    
    os.close();
  }
  
  public void decrypt(InputStream in, OutputStream out) throws IOException
  {
    InputStream is = decryptedStream(in);
    
    int numRead = 0;
    while ((numRead = is.read(buf)) >= 0)
    {
      out.write(buf, 0, numRead);
    }
    
    out.close();
  }
  
  public String encrypt(String str) throws IOException, BadPaddingException, IllegalBlockSizeException
  {
    byte[] utf8 = str.getBytes(StandardCharsets.UTF_8);
    byte[] enc = ecipher.doFinal(utf8);
    return Base64.getEncoder().encodeToString(enc);
  }
  
  public String decrypt(String str) throws IOException, BadPaddingException, IllegalBlockSizeException
  {
    byte[] dec = Base64.getDecoder().decode(str);
    byte[] utf8 = dcipher.doFinal(dec);
    return new String(utf8, StandardCharsets.UTF_8);
  }
  
  public byte[] encrypt(byte[] data) throws IOException, BadPaddingException, IllegalBlockSizeException
  {
    return ecipher.doFinal(data);
  }
  
  public byte[] decrypt(byte[] data) throws IOException, BadPaddingException, IllegalBlockSizeException
  {
    return dcipher.doFinal(data);
  }
}
