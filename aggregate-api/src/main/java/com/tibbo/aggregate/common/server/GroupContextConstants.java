package com.tibbo.aggregate.common.server;

public interface GroupContextConstants
{
  public static final String V_REPLICATION = "replication";
  public static final String V_MEMBERS = "members";
  public static final String V_GROUP_INFO = "groupInfo";
  public static final String V_GROUP_STATUS = "groupStatus";
  public static final String V_STATIC_MEMBERS = "staticMembers";
  public static final String V_MEMBER_STATUS = "memberStatus";
  public static final String V_GROUP_STATUS_VALUE = "groupStatusValue";
  
  public static final String F_CALL = "call";
  public static final String F_REMOVE = "remove";
  public static final String F_ADD = "add";
  
  public static final String A_CREATE_NESTED_GROUP = "createNestedGroup";
  public static final String A_REPLICATE_OR_ADD = "replicateOrAdd";
  public static final String A_PRO_REMOVE_FROM_GROUP = "proRemoveFromGroup";
  public static final String A_FILTER = "filter";
  public static final String A_MULTIPLE_RESOURCE_ADDING = "multipleResourceAddingAction";
  public static final String A_DND_RESOURCE_ADDING = "dndResourceAddingAction";
  public static final String A_CONVERT_TO_STATIC = "convertToStatic";
  
  public static final String VF_REPLICATION_VARIABLE = "variable";
  public static final String VF_REPLICATION_DESCRIPTION = "description";
  public static final String VF_REPLICATION_REPLICATE = "replicate";
  public static final String VF_REPLICATION_USE_MASTER = "useMaster";
  public static final String VF_REPLICATION_MASTER = "master";
  
  public static final String VF_STATIC_MEMBERS_CONTEXT = "context";
  
  public static final String VF_MEMBERS_PATH = "path";
  
  public static final String VF_GROUP_INFO_MEMBER_TYPE = "memberType";
  
  public static final String VF_GROUP_STATUS_VALUE_STATUS = "status";
  
  public static final String FIF_REMOVE_CONTEXT = "context";
  public static final String FIF_ADD_CONTEXT = "context";
  
  public static final String FIF_CALL_FUNCTION = "function";
  public static final String FIF_CALL_PARAMETERS = "parameters";
}
