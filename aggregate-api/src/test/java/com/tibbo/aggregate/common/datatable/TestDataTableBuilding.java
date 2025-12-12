package com.tibbo.aggregate.common.datatable;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.converter.editor.*;
import com.tibbo.aggregate.common.datatable.validator.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TestDataTableBuilding
{
  
  @Test
  public void testCreateTableFormat() throws ContextException
  {
    assertEquals(getTableFormat(), DataTableBuilding.createTableFormat(getFormatAsDataTable()));
  }
  
  @Test
  public void testCreateDataTableFromTableFormat()
  {
    DataTable ex = getFormatAsDataTable();
    DataTable ac = DataTableBuilding.formatToTable(getTableFormat());
    System.out.println(ex + " " + ac);
    assertEquals(getFormatAsDataTable(), DataTableBuilding.formatToTable(getTableFormat()));
  }
  
  @Test
  public void testConvertTableFormat() throws ContextException
  {
    TableFormat tf = getTableFormat();
    
    DataTable dt = DataTableBuilding.formatToTable(tf);
    
    assertEquals(getTableFormat(), DataTableBuilding.createTableFormat(dt));
  }
  
  @Test
  public void testExtendFormat() throws ContextException
  {
    TableFormat tf = getTableFormat();
    
    DataTable dt = DataTableBuilding.formatToTable(tf);
    
    assertNull(DataTableBuilding.createTableFormat(dt).extendMessage(getTableFormat()));
  }
  
  private DataTable getFormatAsDataTable()
  {
    DataTable dt = new SimpleDataTable(DataTableBuilding.TABLE_FORMAT, true);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_MIN_RECORDS, 1);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_MAX_RECORDS, 10);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_MAX_RECORDS, 10);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_REORDERABLE, false);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_UNRESIZABLE, false);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_BINDINGS, new SimpleDataTable(DataTableBuilding.BINDINGS_FORMAT));
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_ENCODED, "<<testField><I><A=0><D=testField><V=<L=1 10>>><<testField2><S><F=R><A=test><D=desc for testField2><V=<N=>>><M=1><X=10>");
    
    DataTable fields = new SimpleDataTable(DataTableBuilding.FIELDS_FORMAT);
    DataRecord field = fields.addRecord();
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_NAME, "testField");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_TYPE, FieldFormat.INTEGER_FIELD);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_VALIDATORS, getIntValidator());
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_NULLABLE, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_DEFAULT_VALUE, 0);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_DESCRIPTION, "testField");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR_OPTIONS, EditorOptionsUtils.createEditorOptionsTable("I", null, null));
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_READONLY, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_KEY, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_GROUP, null);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_SELVALS, new SimpleDataTable(DataTableBuilding.SELECTION_VALUES_FORMAT));
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_HIDDEN, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EXTSELVALS, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_HELP, null);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_OLDNAME, "testField");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_INLINE, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR, null);
    
    field = fields.addRecord();
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_NAME, "testField2");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_TYPE, FieldFormat.STRING_FIELD);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_DESCRIPTION, "desc for testField2");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_READONLY, true);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR_OPTIONS, EditorOptionsUtils.createEditorOptionsTable("S", null, null));
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_VALIDATORS, getNonNullValidator());
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_DEFAULT_VALUE, "test");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_KEY, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_GROUP, null);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_NULLABLE, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_SELVALS, new SimpleDataTable(DataTableBuilding.SELECTION_VALUES_FORMAT));
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_HIDDEN, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EXTSELVALS, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_HELP, null);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_OLDNAME, "testField2");
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_INLINE, false);
    field.setValue(DataTableBuilding.FIELD_FIELDS_FORMAT_EDITOR, null);
    dt.rec().setValue(DataTableBuilding.FIELD_TABLE_FORMAT_FIELDS, fields);
    return dt;
  }
  
  private TableFormat getTableFormat()
  {
    TableFormat format = new TableFormat(1, 10);
    FieldFormat ff = FieldFormat.create("testField", FieldFormat.INTEGER_FIELD, "testField", 0);
    ff.setNullable(false);
    ff.addValidator(new LimitsValidator(1, 10));
    format.addField(ff);
    ff = FieldFormat.create("testField2", FieldFormat.STRING_FIELD, "desc for testField2", "test");
    ff.setReadonly(true);
    ff.addValidator(new NonNullValidator());
    format.addField(ff);
    return format;
  }
  
  private DataTable getIntValidator()
  {
    DataTable validators = new SimpleDataTable(DataTableBuilding.VALIDATORS_FORMAT);
    DataRecord validator = validators.addRecord();
    validator.setValue(DataTableBuilding.FIELD_VALIDATORS_VALIDATOR, FieldFormat.VALIDATOR_LIMITS);
    validator.setValue(DataTableBuilding.FIELD_VALIDATORS_OPTIONS, "1 10");
    return validators;
  }
  
  private DataTable getNonNullValidator()
  {
    DataTable validators = new SimpleDataTable(DataTableBuilding.VALIDATORS_FORMAT);
    DataRecord validator = validators.addRecord();
    validator.setValue(DataTableBuilding.FIELD_VALIDATORS_VALIDATOR, FieldFormat.VALIDATOR_NON_NULL);
    return validators;
  }
}
