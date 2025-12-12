package com.tibbo.aggregate.common.security;

public class RoleRuleInfo
{
  private RoleRule roleRule;
  private String correspondingMask;
  private boolean hasRulesForEntities;
  private boolean ruleFound;
  
  public RoleRuleInfo()
  {
  }
  
  public RoleRule getRoleRule()
  {
    return roleRule;
  }
  
  public void setRoleRule(RoleRule roleRule)
  {
    if (roleRule == null)
      return;
    
    this.ruleFound = true;
    this.roleRule = roleRule;
  }
  
  public String getCorrespondingMask()
  {
    return correspondingMask;
  }
  
  public void setCorrespondingMask(String correspondingMask)
  {
    this.correspondingMask = correspondingMask;
  }
  
  public boolean hasRulesForEntities()
  {
    return hasRulesForEntities;
  }
  
  public void setHasRulesForEntities(boolean hasRulesForEntities)
  {
    this.hasRulesForEntities = hasRulesForEntities;
  }
  
  public boolean ruleFound()
  {
    return ruleFound;
  }
  
}
