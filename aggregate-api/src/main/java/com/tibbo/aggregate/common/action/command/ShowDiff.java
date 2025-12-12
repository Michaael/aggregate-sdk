package com.tibbo.aggregate.common.action.command;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.action.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.field.*;

public class ShowDiff extends GenericActionCommand
{
  public static final String CF_FIRST_FILE_TITLE = "firstFileTitle";
  public static final String CF_FIRST_FILE = "firstFile";
  public static final String CF_SECOND_FILE_TITLE = "secondFileTitle";
  public static final String CF_SECOND_FILE = "secondFile";

  public static final TableFormat CFT_SHOW_DIFF = new TableFormat(1, 1);
  static
  {
    CFT_SHOW_DIFF.addField(FieldFormat.create(CF_FIRST_FILE_TITLE, FieldFormat.STRING_FIELD).setDescription(Cres.get().getString("acFirstFileTitle")));
    CFT_SHOW_DIFF.addField(FieldFormat.create(CF_FIRST_FILE, FieldFormat.STRING_FIELD).setEditor(StringFieldFormat.EDITOR_TEXT).setDescription(Cres.get().getString("acFirstFile")));
    CFT_SHOW_DIFF.addField(FieldFormat.create(CF_SECOND_FILE_TITLE, FieldFormat.STRING_FIELD).setDescription(Cres.get().getString("acSecondFileTitle")));
    CFT_SHOW_DIFF.addField(FieldFormat.create(CF_SECOND_FILE, FieldFormat.STRING_FIELD).setEditor(StringFieldFormat.EDITOR_TEXT).setDescription(Cres.get().getString("acSecondFile")));
  }

  private String firstFileTitle, secondFileTitle;
  private String firstFile, secondFile;

  public ShowDiff()
  {
    super(ActionUtils.CMD_SHOW_DIFF, CFT_SHOW_DIFF, null);
  }

  public ShowDiff(String title, String _firstFileTitle, String _firstFile, String _secondFileTitle, String _secondFile)
  {
    super(ActionUtils.CMD_SHOW_DIFF, title);
    firstFileTitle = _firstFileTitle;
    firstFile = _firstFile;
    secondFileTitle = _secondFileTitle;
    secondFile = _secondFile;
  }

  public ShowDiff(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_SHOW_DIFF, title, parameters, CFT_SHOW_DIFF);
  }

  @Override
  protected DataTable constructParameters()
  {
    DataTable dt = new SimpleDataTable(CFT_SHOW_DIFF);
    DataRecord rec = dt.addRecord();

    rec.setValue(CF_FIRST_FILE_TITLE, firstFileTitle);
    rec.setValue(CF_FIRST_FILE, firstFile);
    rec.setValue(CF_SECOND_FILE_TITLE, secondFileTitle);
    rec.setValue(CF_SECOND_FILE, secondFile);

    return dt;
  }
}
