package com.tibbo.aggregate.common.resource;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.security.cert.*;
import java.util.*;
import java.util.List;
import java.util.Map.*;
import java.util.jar.*;

import javax.swing.*;

import org.apache.commons.lang3.*;

import com.tibbo.aggregate.common.*;

public class ResourceManager
{
  private static final String CERTIFICATE_FILE_NAME = "customization.cer";
  
  public final static String ICON_FILE_EXTENSION = "png";
  
  public final static String CUSTOMIZATION_RESOURCES_FILENAME = "custom.jar";
  
  public final static String CLIENT_CUSTOMIZATION_RESOURCES_FILENAME = "custom-client.jar";
  
  private final static String REPLACEMENTS_RESOURCE_NAME = "replacements.txt";
  
  private static Class RESOURCE_CLASS;
  
  private static Map<String, byte[]> RESOURCES;
  
  private static Map<String, String> REPLACEMENTS = new LinkedHashMap();
  
  private static Map<String, ImageIcon> ICON_CACHE = new HashMap();
  
  private static List<WrappingResourceBundle> BUNDLES = new LinkedList();
  
  private static Locale LOCALE = Locale.ENGLISH;
  
  static
  {
    try
    {
      String language = System.getProperty("user.language");
      if (language != null)
      {
        LOCALE = LocaleUtils.toLocale(language);
      }
    }
    catch (Exception ex)
    {
      Log.CORE.error("Error setting up locale", ex);
    }
  }
  
  public static void initialize(Class resClass)
  {
    
    RESOURCE_CLASS = resClass;
    
    RESOURCES = new Hashtable();
  }
  
  public static void initialize(URI customizationUrl, Class resClass)
  {
    initialize(resClass);
    
    Log.CORE.debug("Starting resource manager, customization file " + customizationUrl);
    
    try
    {
      processCustomizationFile(customizationUrl);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Error processing customization file '" + customizationUrl + "': ", ex);
    }
    
    for (WrappingResourceBundle bundle : BUNDLES)
    {
      process(bundle);
    }
  }
  
  private static void processCustomizationFile(URI customizationUrl) throws IOException, CertificateException
  {
    try
    {
      if (customizationUrl == null)
      {
        return;
      }
      
      InputStream is = customizationUrl.toURL().openStream();
      BufferedInputStream bis = new BufferedInputStream(is);
      JarInputStream jis = new JarInputStream(bis, true);
      JarEntry je = null;
      
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
      X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(ResourceManager.class.getResourceAsStream(CERTIFICATE_FILE_NAME));
      
      Manifest man = jis.getManifest();
      if (man == null)
      {
        jis.close();
        throw new SecurityException("The customization file is not signed");
      }
      
      while ((je = jis.getNextJarEntry()) != null)
      {
        if (je.isDirectory())
        {
          continue;
        }
        
        byte[] bytes = new byte[1024];
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        int read = -1;
        while ((read = jis.read(bytes)) != -1)
        {
          baos.write(bytes, 0, read);
        }
        
        Log.RESOURCE.debug("Found customization package entry: " + je.getName() + "");
        
        JarVerifier.verify(je, new X509Certificate[] { certificate });
        
        if (je.getName().equals(REPLACEMENTS_RESOURCE_NAME))
        {
          Log.RESOURCE.debug("Found string resource replacements");
          
          BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), StandardCharsets.UTF_8));
          String sub;
          
          while ((sub = br.readLine()) != null)
          {
            try
            {
              String[] parts = sub.split("=", 2);
              REPLACEMENTS.put(parts[0].trim(), parts[1].trim());
              Log.RESOURCE.debug("Found string replacement: '" + parts[0] + "' => '" + parts[1] + "'");
            }
            catch (Exception ex1)
            {
              Log.RESOURCE.error("Error processing replacement: " + sub, ex1);
            }
          }
          
          continue;
        }
        
        RESOURCES.put(je.getName(), baos.toByteArray());
      }
      
      jis.close();
    }
    catch (FileNotFoundException ex)
    {
      // Ignoring - the only situation that we should not react for is the absence of customization file
    }
  }
  
  public static ImageIcon getImageIcon(String id)
  {
    if (id == null)
    {
      return null;
    }
    
    return getImageIcon(id, ICON_FILE_EXTENSION);
  }
  
  private static synchronized ImageIcon getImageIcon(String id, String extension)
  {
    String name = id + "." + extension;
    
    try
    {
      ImageIcon cached = ICON_CACHE.get(name);
      
      if (cached != null)
      {
        return cached;
      }
      
      byte[] res = getResource(name);
      
      if (res != null)
      {
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(res));
        ICON_CACHE.put(name, icon);
        return icon;
      }
      
      URL url = RESOURCE_CLASS != null ? RESOURCE_CLASS.getResource(name) : null;
      
      if (url == null)
      {
        return null;
      }
      
      ImageIcon icon = new ImageIcon(url);
      
      ICON_CACHE.put(name, icon);
      
      return icon;
    }
    catch (Exception ex)
    {
      Log.RESOURCE.error("Error getting icon: " + name, ex);
      return null;
    }
  }
  
  public static byte[] getResource(String name)
  {
    return RESOURCES != null ? RESOURCES.get(name) : null;
  }
  
  public static void process(WrappingResourceBundle bundle)
  {
    for (Entry<String, String> entry : REPLACEMENTS.entrySet())
    {
      bundle.addReplacement(entry.getKey(), entry.getValue());
    }
  }
  
  public static void add(WrappingResourceBundle bundle)
  {
    BUNDLES.add(bundle);
  }
  
  public static Locale getLocale()
  {
    return LOCALE;
  }
  
  public static void putIconToCache(String name, ImageIcon icon)
  {
    ICON_CACHE.put(name, icon);
  }
}
