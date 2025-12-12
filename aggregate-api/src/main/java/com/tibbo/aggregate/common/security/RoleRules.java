package com.tibbo.aggregate.common.security;

import java.util.*;

public class RoleRules
{
  LinkedList<RoleRule> permittingRXRules = new LinkedList<RoleRule>();
  LinkedList<RoleRule> permittingRWXRules = new LinkedList<RoleRule>();
  LinkedList<RoleRule> prohibitiveRules = new LinkedList<RoleRule>();
  
  public LinkedList<RoleRule> getPermittingRXRules()
  {
    return permittingRXRules;
  }
  
  public LinkedList<RoleRule> getPermittingRWXRules()
  {
    return permittingRWXRules;
  }
  
  public LinkedList<RoleRule> getProhibitiveRules()
  {
    return prohibitiveRules;
  }
  
  public void addAll(RoleRules roleRules)
  {
    permittingRXRules.addAll(roleRules.getPermittingRXRules());
    permittingRWXRules.addAll(roleRules.getPermittingRWXRules());
    prohibitiveRules.addAll(roleRules.getProhibitiveRules());
  }
  
  public void clear()
  {
    permittingRXRules.clear();
    permittingRWXRules.clear();
    prohibitiveRules.clear();
  }
}
