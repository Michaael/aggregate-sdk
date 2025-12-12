package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;

public class CommonServerFormats
{
  public static final TableFormat FIFT_LOGIN = new TableFormat(1, 1);
  
  static
  {
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_USERNAME + "><S>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_PASSWORD + "><S><F=N><E=" + StringFieldFormat.EDITOR_PASSWORD + ">");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_CODE + "><S><F=N>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_STATE + "><S><F=N>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_PROVIDER + "><S><F=N>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_COUNT_ATTEMPTS + "><B><A=true><F=N>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_TOKEN + "><A><F=N>");
    FIFT_LOGIN.addField("<" + RootContextConstants.FIF_LOGIN_LOGIN + "><S><F=N>");

  }
  
  public static final TableFormat FOFT_LOGIN = new TableFormat(1, 1);
  
  static
  {
    FOFT_LOGIN.addField("<" + RootContextConstants.FOF_LOGIN_USERNAME + "><S>");
    FOFT_LOGIN.addField("<" + RootContextConstants.FOF_LOGIN_LOGIN + "><S><F=N>");
  }
  
  public static final TableFormat FIFT_CHANGE_PASSWORD = new TableFormat(1, 1);
  
  static
  {
    FIFT_CHANGE_PASSWORD.addField("<" + RootContextConstants.FIF_LOGIN_USERNAME + "><S><F=HN><D=" + Cres.get().getString("username") + ">");
    FIFT_CHANGE_PASSWORD.addField("<" + RootContextConstants.FIF_CHANGE_PASSWORD_OLD_PASSWORD + "><S><D=" + Cres.get().getString("userOldPassword") + "><E=" + StringFieldFormat.EDITOR_PASSWORD + ">");
    FIFT_CHANGE_PASSWORD.addField("<" + RootContextConstants.FIF_CHANGE_PASSWORD_NEW_PASSWORD + "><S><D=" + Cres.get().getString("userNewPassword") + "><E=" + StringFieldFormat.EDITOR_PASSWORD + ">");
    FIFT_CHANGE_PASSWORD
        .addField("<" + RootContextConstants.FIF_CHANGE_PASSWORD_REPEAT_PASSWORD + "><S><D=" + Cres.get().getString("userPasswordRe") + "><E=" + StringFieldFormat.EDITOR_PASSWORD + ">");
  }
  
  public static final TableFormat FIFT_GET_FORMAT = new TableFormat(1, 1, "<" + RootContextConstants.FIF_GET_FORMAT_ID + "><I>");
  
  public static final TableFormat FOFT_GET_FORMAT = new TableFormat(1, 1, "<" + RootContextConstants.FOF_GET_FORMAT_DATA + "><S>");
  
  public static final TableFormat FIFT_INIT_HISTORY_MAX_RESULTS = new TableFormat(1, 1);
  
  static
  {
    FIFT_INIT_HISTORY_MAX_RESULTS.addField(FieldFormat.create(EventsContextConstants.FIF_INIT_HISTORY_START_TIME, FieldFormat.DATE_FIELD, Cres.get().getString("startTime"))
        .setNullable(true).setDefault(null));
    FIFT_INIT_HISTORY_MAX_RESULTS.addField(FieldFormat.create(EventsContextConstants.FIF_INIT_HISTORY_END_TIME, FieldFormat.DATE_FIELD, Cres.get().getString("efEndTime"))
        .setNullable(true).setDefault(null));
    FIFT_INIT_HISTORY_MAX_RESULTS.addField(FieldFormat.create(EventsContextConstants.FIF_INIT_HISTORY_MAX_RESULTS, FieldFormat.INTEGER_FIELD, Cres.get().getString("maximum"))
        .setNullable(true).setDefault(1000));
  }
}
