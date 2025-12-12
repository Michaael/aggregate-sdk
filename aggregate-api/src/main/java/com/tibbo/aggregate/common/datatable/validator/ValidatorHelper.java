package com.tibbo.aggregate.common.datatable.validator;

import com.tibbo.aggregate.common.*;

public class ValidatorHelper
{
  public static final int MIN_NAME_LENGTH = 1;
  public static final int MAX_NAME_LENGTH = 50;
  
  public static final int MIN_TYPE_LENGTH = 1;
  public static final int MAX_TYPE_LENGTH = 50;
  
  public static final int MIN_DESCRIPTION_LENGTH = 0;
  public static final int MAX_DESCRIPTION_LENGTH = 100;
  
  public static final FieldValidator NAME_SYNTAX_VALIDATOR = new RegexValidator("\\w+", Cres.get().getString("dtInvalidName"));
  public static final FieldValidator NAME_LENGTH_VALIDATOR = new LimitsValidator(MIN_NAME_LENGTH, MAX_NAME_LENGTH);
  
  public static final FieldValidator DESCRIPTION_SYNTAX_VALIDATOR = new RegexValidator("[^\\p{Cntrl}]*", Cres.get().getString("dtInvalidDescr"));
  public static final FieldValidator DESCRIPTION_LENGTH_VALIDATOR = new LimitsValidator(MIN_DESCRIPTION_LENGTH, MAX_DESCRIPTION_LENGTH);

  public static final FieldValidator TYPE_SYNTAX_VALIDATOR = new RegexValidator("\\w+", Cres.get().getString("dtInvalidType"));
  public static final FieldValidator TYPE_LENGTH_VALIDATOR = new LimitsValidator(MIN_TYPE_LENGTH, MAX_TYPE_LENGTH);
  
  private static final String IP_PART = "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
  public static final FieldValidator IP_ADDRESS_VALIDATOR = new RegexValidator("^(?:" + IP_PART + "\\.){3}" + IP_PART + "|$", Cres.get().getString("dtInvalidIp"));
  
  public static final FieldValidator PORT_VALIDATOR = new LimitsValidator(1, 65535);
  
  public static final FieldValidator EMAIL_VALIDATOR = new RegexValidator("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[_A-Za-z0-9-]+)^^");
  
  public static final FieldValidator NON_ZERO_LENGTH_VALIDATOR = new LimitsValidator(1, Integer.MAX_VALUE);
}
