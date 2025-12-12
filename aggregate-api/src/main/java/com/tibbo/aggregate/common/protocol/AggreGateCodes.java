package com.tibbo.aggregate.common.protocol;

/**
 * Defines AggreGate protocol message codes.
 * 
 * There are two code string syntax variations: (A) Global single-character codes; (B) Detailed codes in the form DOMAIN[.SUBDOMAIN[.SUBDOMAIN[...]]]-NUMBER where top-level domain is AG, e.g. AG-10000
 */
public class AggreGateCodes
{
  
  public static final String REPLY_CODE_OK = "A";
  public static final String REPLY_CODE_DENIED = "D";
  public static final String REPLY_CODE_ERROR = "E";
  public static final String REPLY_CODE_LOCKED = "L";
  
  public static final String REPLY_CODE_MAINTENANCE = "M";
  
  public static final String REPLY_CODE_PASSWORD_EXPIRED = "AG-10001";
}
