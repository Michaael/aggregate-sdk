package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.protocol.*;

public class ClassicEncodingSettings extends EncodingSettings
{
  private boolean useVisibleSeparators;
  private FormatCache formatCache;
  private KnownFormatCollector knownFormatCollector;
  
  private boolean encodeDefaultValues = true;
  private boolean encodeFieldNames = false;
  private ProtocolVersion protocolVersion;
  private boolean encryptedPasswords = false;
  
  public ClassicEncodingSettings(boolean useVisibleSeparators)
  {
    super(true, null);
    this.useVisibleSeparators = useVisibleSeparators;
  }
  
  public ClassicEncodingSettings(boolean useVisibleSeparators, boolean encodeFieldNames)
  {
    this(useVisibleSeparators);
    this.encodeFieldNames = encodeFieldNames;
  }
  
  public ClassicEncodingSettings(boolean useVisibleSeparators, TableFormat format)
  {
    super(true, format);
    this.useVisibleSeparators = useVisibleSeparators;
  }
  
  public boolean isEncodeDefaultValues()
  {
    return encodeDefaultValues;
  }
  
  public void setEncodeDefaultValues(boolean encodeDefaultValues)
  {
    this.encodeDefaultValues = encodeDefaultValues;
  }
  
  public boolean isUseVisibleSeparators()
  {
    return useVisibleSeparators;
  }
  
  public FormatCache getFormatCache()
  {
    return formatCache;
  }
  
  public KnownFormatCollector getKnownFormatCollector()
  {
    return knownFormatCollector;
  }
  
  public void setUseVisibleSeparators(boolean useVisibleSeparators)
  {
    this.useVisibleSeparators = useVisibleSeparators;
  }
  
  public void setFormatCache(FormatCache formatCache)
  {
    this.formatCache = formatCache;
  }
  
  public void setKnownFormatCollector(KnownFormatCollector knownFormatCollector)
  {
    this.knownFormatCollector = knownFormatCollector;
  }
  
  public boolean isEncodeFieldNames()
  {
    return encodeFieldNames;
  }
  
  public void setEncodeFieldNames(boolean encodeFieldNames)
  {
    this.encodeFieldNames = encodeFieldNames;
  }
  
  public void setProtocolVersion(ProtocolVersion protocolVersion)
  {
    this.protocolVersion = protocolVersion;
  }
  
  public ProtocolVersion getProtocolVersion()
  {
    return protocolVersion;
  }

  public boolean isEncryptedPasswords()
  {
    return encryptedPasswords;
  }
  
  public void setEncryptedPasswords(boolean encryptedPasswords)
  {
    this.encryptedPasswords = encryptedPasswords;
  }
}
