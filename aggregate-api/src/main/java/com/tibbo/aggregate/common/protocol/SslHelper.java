package com.tibbo.aggregate.common.protocol;

import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.PKIXRevocationChecker.*;
import java.util.*;

import javax.net.ssl.*;

import com.tibbo.aggregate.common.*;

public class SslHelper
{
  private static SSLContext SSL_CONTEXT;
  
  static
  {
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager()
    {
      @Override
      public java.security.cert.X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }
      
      @Override
      public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }
      
      @Override
      public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
      {
      }
    } };
    
    try
    {
      SSL_CONTEXT = SSLContext.getInstance("SSL");
      
      SSL_CONTEXT.init(null, trustAllCerts, new java.security.SecureRandom());
    }
    catch (Exception ex)
    {
      Log.CORE.fatal("Error initializing SSL context", ex);
    }
  }
  
  public static void initTwoWaySSLContext(SSLContext context, Set<Option> revocationCheckerOptions) throws NoSuchAlgorithmException, KeyStoreException, FileNotFoundException, IOException,
      CertificateException, UnrecoverableKeyException, KeyManagementException, InvalidAlgorithmParameterException
  {
    // These properties are set only on the server. On the client, this approach will not work.
    char[] passphrase = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
    String keystoreFilename = System.getProperty("javax.net.ssl.keyStore");
    
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(new FileInputStream(keystoreFilename), passphrase); // stream will be closed
    
    KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX"); // DefaultAlgorithm is not suitable for RevocationChecker
    
    kmf.init(keyStore, passphrase);
    configureRevocationChecker(keyStore, tmf, revocationCheckerOptions);
    
    // Initializing TrustManagerFactory without RevocationChecker
    // tmf.init(keyStore);
    
    context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
  }
  
  public static void configureRevocationChecker(KeyStore trustStore, TrustManagerFactory trustManagerFactory, Set<Option> options)
      throws NoSuchAlgorithmException, KeyStoreException, InvalidAlgorithmParameterException
  {
    CertPathBuilder cpb = CertPathBuilder.getInstance("PKIX");
    PKIXRevocationChecker revocationChecker = (PKIXRevocationChecker) cpb.getRevocationChecker();
    
    revocationChecker.setOptions(options);
    
    PKIXBuilderParameters pkixParameters = new PKIXBuilderParameters(trustStore, new X509CertSelector());
    pkixParameters.addCertPathChecker(revocationChecker);
    
    CertPathTrustManagerParameters certPathTrustManagerParameters = new CertPathTrustManagerParameters(pkixParameters);
    
    trustManagerFactory.init(certPathTrustManagerParameters);
  }
  
  public static Set<Option> getRevocationCheckerOptions(boolean preferCrls, boolean onlyEndEntity, boolean noFallback, boolean softFail)
  {
    Set<Option> options = new HashSet();
    
    if (preferCrls)
      options.add(PKIXRevocationChecker.Option.PREFER_CRLS);
    
    if (onlyEndEntity)
      options.add(PKIXRevocationChecker.Option.ONLY_END_ENTITY);
    
    if (noFallback) // Don't fall back to CRL (or OCSP) checking
      options.add(PKIXRevocationChecker.Option.NO_FALLBACK);
    
    if (softFail)
      options.add(PKIXRevocationChecker.Option.SOFT_FAIL);
    
    return options;
  }
  
  public static SSLSocketFactory getTrustedSocketFactory()
  {
    return SSL_CONTEXT.getSocketFactory();
  }
  
  public static SSLSocketFactory getTrustedSocketFactory(boolean trustAll, boolean preferCrls, boolean onlyEndEntity, boolean noFallback, boolean softFail)
  {
    if (trustAll)
      return getTrustedSocketFactory();
    
    try
    {
      SSLContext sslContext = SSLContext.getInstance("SSL");
      Set<Option> options = getRevocationCheckerOptions(preferCrls, onlyEndEntity, noFallback, softFail);
      
      initTwoWaySSLContext(sslContext, options);
      
      return sslContext.getSocketFactory();
    }
    catch (Exception ex)
    {
      Log.CORE.fatal("Error initializing Two-Way SSL context", ex);
      return null;
    }
  }
}
