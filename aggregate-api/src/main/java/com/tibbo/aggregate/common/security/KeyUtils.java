package com.tibbo.aggregate.common.security;

import java.nio.charset.*;
import java.security.*;
import java.util.*;

import javax.crypto.*;
import javax.crypto.spec.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.util.*;

public class KeyUtils
{
  private static final String KEY_FACTORY = "PBKDF2WithHmacSHA512";
  private static final String KEY_SPEC = "AES";
  private static final String CIPHER = "AES/CBC/PKCS5Padding";
  private static final String SALT = "s011td";
  private static final String ENCRYPTION_DELIMITER = ":";
  private static final String INTERNAL_KEY = "iBohSh2ohl2i";
  public static final String REF_PHRASE = "reference";
  
  private static SecretKeySpec encryptKey;
  private static SecretKeySpec decryptKey;
  private static SecretKeySpec internalKey;
  
  static
  {
    try
    {
      internalKey = getSecretKeySpec(INTERNAL_KEY);
    }
    catch (Throwable e)
    {
      internalKey = null;
    }
  }
  
  private static SecretKeySpec getSecretKeySpec(String p) throws GeneralSecurityException
  {
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_FACTORY);
    PBEKeySpec keySpec = new PBEKeySpec(p.toCharArray(), SALT.getBytes(StandardCharsets.UTF_8), 65536, 256);
    SecretKey keyTmp = keyFactory.generateSecret(keySpec);
    return new SecretKeySpec(keyTmp.getEncoded(), KEY_SPEC);
  }
  
  public static String encryptByteArrayToString(byte[] string, SecretKeySpec keySpec) throws GeneralSecurityException
  {
    Cipher pbeCipher = Cipher.getInstance(CIPHER);
    pbeCipher.init(Cipher.ENCRYPT_MODE, keySpec);
    AlgorithmParameters parameters = pbeCipher.getParameters();
    IvParameterSpec ivParameterSpec = parameters.getParameterSpec(IvParameterSpec.class);
    byte[] cryptoText = pbeCipher.doFinal(string);
    byte[] iv = ivParameterSpec.getIV();
    return Base64.getEncoder().encodeToString(iv) + ENCRYPTION_DELIMITER + Base64.getEncoder().encodeToString(cryptoText);
  }
  
  public static byte[] decryptStringToByteArray(String string, SecretKeySpec keySpec) throws GeneralSecurityException
  {
    String iv = string.split(ENCRYPTION_DELIMITER)[0];
    String property = string.split(ENCRYPTION_DELIMITER)[1];
    Cipher pbeCipher = Cipher.getInstance(CIPHER);
    pbeCipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(Base64.getDecoder().decode(iv)));
    return pbeCipher.doFinal(Base64.getDecoder().decode(property));
  }
  
  public static String encryptStringBy(String string, SecretKeySpec key) throws GeneralSecurityException
  {
    if (key == null || StringUtils.isEmpty(string))
    {
      return string;
    }
    
    return encryptByteArrayToString(string.getBytes(StandardCharsets.UTF_8), key);
  }
  
  public static String decryptStringBy(String string, SecretKeySpec key) throws GeneralSecurityException
  {
    if (key == null || StringUtils.isEmpty(string))
    {
      return string;
    }
    
    return new String(decryptStringToByteArray(string, key), StandardCharsets.UTF_8);
  }
  
  public static String encryptString(String string) throws SecurityException
  {
    try
    {
      return encryptStringBy(string, getEncryptKey());
    }
    catch (Exception ex)
    {
      Log.SECURITY.fatal("Re-encryption failed: " + ex.getMessage(), ex);
      throw new SecurityException("Encryption failure", ex);
    }
  }
  
  public static String decryptString(String string) throws SecurityException
  {
    try
    {
      return decryptStringBy(string, getDecryptKey());
    }
    catch (Exception ex)
    {
      Log.SECURITY.fatal("Re-encryption failed: " + ex.getMessage(), ex);
      throw new SecurityException("Decryption failure", ex);
    }
  }
  
  public static SecretKeySpec getEncryptKey()
  {
    return encryptKey;
  }
  
  public static SecretKeySpec getDecryptKey()
  {
    return decryptKey;
  }
  
  public static SecretKeySpec getInternalKey()
  {
    return internalKey;
  }
  
  public static String getEncryptedInternalKey() throws SecurityException
  {
    return encryptKey(getInternalKey().getEncoded());
  }
  
  public static void setKeys(byte[] oldEncryptionKey, byte[] newEncryptionKey) throws SecurityException
  {
    try
    {
      if (newEncryptionKey != null)
        encryptKey = new SecretKeySpec(newEncryptionKey, KEY_SPEC);
      if (oldEncryptionKey != null)
        decryptKey = new SecretKeySpec(oldEncryptionKey, KEY_SPEC);
    }
    catch (Throwable ex)
    {
      throw new SecurityException("Invalid key", ex);
    }
  }
  
  public static void eraseKeys()
  {
    encryptKey = null;
    decryptKey = null;
  }
  
  public static byte[] getRandomKey()
  {
    try
    {
      KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyUtils.KEY_SPEC);
      keyGenerator.init(128);
      SecretKey secretKey = keyGenerator.generateKey();
      return secretKey.getEncoded();
    }
    catch (NoSuchAlgorithmException ex)
    {
      return null;
    }
  }
  
  public static String encryptKey(byte[] key) throws SecurityException
  {
    if (key == null)
    {
      return null;
    }
    
    if (getInternalKey() == null)
    {
      return Arrays.toString(key);
    }
    
    try
    {
      return encryptByteArrayToString(key, getInternalKey());
    }
    catch (Exception ex)
    {
      throw new SecurityException("Key encryption failure", ex);
    }
  }
  
  public static byte[] decryptKey(String key) throws SecurityException
  {
    if (key == null)
    {
      return null;
    }
    
    if (getInternalKey() == null)
    {
      return key.getBytes(StandardCharsets.UTF_8);
    }
    
    try
    {
      return decryptStringToByteArray(key, getInternalKey());
    }
    catch (Exception ex)
    {
      Log.SECURITY.debug("Key decryption failure " + key, ex);
      throw new SecurityException("Key decryption failure", ex);
    }
    
  }
  
  public static String encryptPw(String pw) throws SecurityException
  {
    try
    {
      return encryptStringBy(pw, getInternalKey());
    }
    catch (Exception ex)
    {
      Log.SECURITY.warn("Can't encrypt password", ex);
      throw new SecurityException("Password encryption failure", ex);
    }
  }
  
  public static String decryptPw(String pw) throws SecurityException
  {
    try
    {
      return decryptStringBy(pw, getInternalKey());
    }
    catch (Exception ex)
    {
      Log.SECURITY.warn("Can't decrypt password", ex);
      throw new SecurityException("Password decryption failure", ex);
    }
  }
}
