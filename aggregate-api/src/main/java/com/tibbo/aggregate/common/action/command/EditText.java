package com.tibbo.aggregate.common.action.command;

import java.util.LinkedHashMap;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.action.ActionUtils;
import com.tibbo.aggregate.common.action.GenericActionCommand;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;

public class EditText extends GenericActionCommand
{
  public static final String CF_TEXT = "text";
  public static final String CF_MODE = "mode";
  
  public static final String RF_RESULT = "result";
  public static final String RF_TEXT = "text";
  
  public static final TableFormat CFT_EDIT_TEXT = new TableFormat(1, 1);
  
  static
  {
    CFT_EDIT_TEXT.addField("<" + CF_TEXT + "><S><D=" + Cres.get().getString("text") + ">");
    
    FieldFormat<Object> ff = FieldFormat.create("<" + CF_MODE + "><S><F=N><D=" + Cres.get().getString("mode") + ">")
        .setSelectionValues(modes())
        .setExtendableSelectionValues(true)
        .setDefaultOverride(true);
    CFT_EDIT_TEXT.addField(ff);
  }
  
  public static Map<String, String> modes()
  {
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_AGGREGATE, StringFieldFormat.TEXT_EDITOR_MODE_AGGREGATE);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_JAVA, StringFieldFormat.TEXT_EDITOR_MODE_JAVA);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_XML, StringFieldFormat.TEXT_EDITOR_MODE_XML);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_SQL, StringFieldFormat.TEXT_EDITOR_MODE_SQL);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_HTML, StringFieldFormat.TEXT_EDITOR_MODE_HTML);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_SHELLSCRIPT, StringFieldFormat.TEXT_EDITOR_MODE_SHELLSCRIPT);
    map.put(StringFieldFormat.TEXT_EDITOR_MODE_SMI_MIB, StringFieldFormat.TEXT_EDITOR_MODE_SMI_MIB);
    map.put(StringFieldFormat.CODE_EDITOR_MODE_JAVASCRIPT, StringFieldFormat.CODE_EDITOR_MODE_JAVASCRIPT);
    
    return map;
  }
  
  public static final TableFormat RFT_EDIT_TEXT = new TableFormat(1, 1);
  
  static
  {
    RFT_EDIT_TEXT.addField("<" + RF_RESULT + "><S>");
    RFT_EDIT_TEXT.addField("<" + RF_TEXT + "><S><F=N>");
  }
  
  private String text;
  private String mode;
  
  public EditText()
  {
    super(ActionUtils.CMD_EDIT_TEXT, CFT_EDIT_TEXT, RFT_EDIT_TEXT);
  }
  
  public EditText(String title, String text, String mode)
  {
    super(ActionUtils.CMD_EDIT_TEXT, title);
    this.text = text;
    this.mode = mode;
  }
  
  public EditText(String title, DataTable parameters)
  {
    super(ActionUtils.CMD_EDIT_TEXT, title, parameters, CFT_EDIT_TEXT);
  }
  
  @Override
  protected DataTable constructParameters()
  {
    return new SimpleDataTable(CFT_EDIT_TEXT, text, mode);
  }
  
  public String getText()
  {
    return text;
  }
  
  public void setText(String text)
  {
    this.text = text;
  }
  
  public String getMode()
  {
    return mode;
  }
  
  public void setMode(String mode)
  {
    this.mode = mode;
  }
  
}
