package com.tibbo.aggregate.common.device;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;

public class DeviceUtils
{
  
  public static final String FIELD_DEVICE_DRIVER = "driver";
  public static final String FIELD_DEVICE_DESCRIPTION = "description";
  public static final String FIELD_DEVICE_NAME = "name";
  
  public static final FieldFormat NAME_FIELD;
  public static final FieldFormat DESCRIPTION_FIELD;

  static
  {
    NAME_FIELD = FieldFormat.create("<" + DeviceUtils.FIELD_DEVICE_NAME + "><S><D=" + Cres.get().getString("devDeviceName") + "><H=" + Cres.get().getString("devDeviceNameHelp") + ">");
    NAME_FIELD.getValidators().add(new IdValidator());
    NAME_FIELD.getValidators().add(ValidatorHelper.NAME_LENGTH_VALIDATOR);
    NAME_FIELD.getValidators().add(ValidatorHelper.NAME_SYNTAX_VALIDATOR);
    
    DESCRIPTION_FIELD = FieldFormat.create("<" + DeviceUtils.FIELD_DEVICE_DESCRIPTION + "><S><D=" + Cres.get().getString("devDeviceDesc") + "><H=" + Cres.get().getString("devDeviceDescHelp") + ">");

  }

  public static final TableFormat VARIABLE_VALUE_ERROR_FORMAT = new TableFormat(1, 1);
  public static final String ERROR_FIELD = "error";
  static
  {
    FieldFormat ff = FieldFormat.create(ERROR_FIELD, FieldFormat.STRING_FIELD, Cres.get().getString("error"));
    VARIABLE_VALUE_ERROR_FORMAT.addField(ff);
  }
  public static DataTable wrap(Exception e, int quality)
  {
    DataTable dt = new SimpleDataTable(VARIABLE_VALUE_ERROR_FORMAT, e.getMessage());
    dt.setQuality(quality);
    return dt;
  }
}
