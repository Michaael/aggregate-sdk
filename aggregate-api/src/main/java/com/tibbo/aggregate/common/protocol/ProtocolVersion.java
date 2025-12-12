package com.tibbo.aggregate.common.protocol;

import java.util.*;

public enum ProtocolVersion
{
  V2("2"),
  V3("3"), // Length-byte added to commands. Compression can be used.
  V4("4"); // Embedded DataFable fields escaping reduced. Add/remove listener commands extended by fingerprint parameter (AGG-8259)
  
  private static Map<String, ProtocolVersion> MAPPING = new HashMap<>();
  static
  {
    for (ProtocolVersion version : ProtocolVersion.values())
    {
      MAPPING.put(version.notation, version);
    }
  }
  
  public static ProtocolVersion byNotation(String notation)
  {
    return MAPPING.get(notation);
  }
  
  private String notation;
  
  ProtocolVersion(String notation)
  {
    this.notation = notation;
  }
  
  public String notation()
  {
    return notation;
  }
}
