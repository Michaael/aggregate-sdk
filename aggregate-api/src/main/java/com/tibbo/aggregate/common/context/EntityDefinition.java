package com.tibbo.aggregate.common.context;

public interface EntityDefinition
{
  String getName();
  
  String getDescription();
  
  String getHelp();
  
  String getGroup();
  
  Integer getIndex();
  
  String getIconId();
  
  String toDetailedString();
  
  Object getOwner();
  
  Integer getEntityType();
}