package com.tibbo.aggregate.common.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.datatable.AggreGateBean;
import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.expression.DefaultReferenceResolver;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;

public class ViewFilterElement extends AggreGateBean
{
  public static final int LOGICAL_OPERATION_NONE = 0;
  public static final int LOGICAL_OPERATION_AND = 1;
  public static final int LOGICAL_OPERATION_OR = 2;

  public static final int TYPE_CONDITION = 0;
  public static final int TYPE_NESTED_CONDITIONS = 1;

  public static final String OPERATION_EQUALS = "EQUALS";
  public static final String OPERATION_DOES_NOT_EQUAL = "DOES_NOT_EQUAL";
  public static final String OPERATION_IS_NULL = "IS_NULL";
  public static final String OPERATION_IS_NOT_NULL = "IS_NOT_NULL";
  public static final String OPERATION_CONTAINS = "CONTAINS";
  public static final String OPERATION_DOES_NOT_CONTAIN = "DOES_NOT_CONTAIN";
  public static final String OPERATION_BEGINS_WITH = "BEGINS_WITH";
  public static final String OPERATION_DOES_NOT_BEGIN_WITH = "DOES_NOT_BEGIN_WITH";
  public static final String OPERATION_ENDS_WITH = "ENDS_WITH";
  public static final String OPERATION_DOES_NOT_END_WITH = "DOES_NOT_END_WITH";
  public static final String OPERATION_IN = "IN";
  public static final String OPERATION_IS_GREATER_THAN = "IS_GREATER_THAN";
  public static final String OPERATION_IS_GREATER_OR_EQUAL_THAN = "IS_GREATER_OR_EQUAL_THAN";
  public static final String OPERATION_IS_LESS_THAN = "IS_LESS_THAN";
  public static final String OPERATION_IS_LESS_OR_EQUAL_THAN = "IS_LESS_OR_EQUAL_THAN";
  public static final String OPERATION_ON = "ON";
  public static final String OPERATION_ON_OR_AFTER = "ON_OR_AFTER";
  public static final String OPERATION_ON_OR_BEFORE = "ON_OR_BEFORE";
  public static final String OPERATION_LAST_HOUR = "LAST_HOUR";
  public static final String OPERATION_THIS_HOUR = "THIS_HOUR";
  public static final String OPERATION_NEXT_HOUR = "NEXT_HOUR";
  public static final String OPERATION_YESTERDAY = "YESTERDAY";
  public static final String OPERATION_TODAY = "TODAY";
  public static final String OPERATION_TOMORROW = "TOMORROW";
  public static final String OPERATION_LAST_WEEK = "LAST_WEEK";
  public static final String OPERATION_THIS_WEEK = "THIS_WEEK";
  public static final String OPERATION_NEXT_WEEK = "NEXT_WEEK";
  public static final String OPERATION_LAST_MONTH = "LAST_MONTH";
  public static final String OPERATION_THIS_MONTH = "THIS_MONTH";
  public static final String OPERATION_NEXT_MONTH = "NEXT_MONTH";
  public static final String OPERATION_LAST_YEAR = "LAST_YEAR";
  public static final String OPERATION_THIS_YEAR = "THIS_YEAR";
  public static final String OPERATION_NEXT_YEAR = "NEXT_YEAR";
  public static final String OPERATION_LAST_X_HOURS = "LAST_X_HOURS";
  public static final String OPERATION_NEXT_X_HOURS = "NEXT_X_HOURS";
  public static final String OPERATION_LAST_X_DAYS = "LAST_X_DAYS";
  public static final String OPERATION_NEXT_X_DAYS = "NEXT_X_DAYS";
  public static final String OPERATION_LAST_X_WEEKS = "LAST_X_WEEKS";
  public static final String OPERATION_NEXT_X_WEEKS = "NEXT_X_WEEKS";
  public static final String OPERATION_LAST_X_MONTHS = "LAST_X_MONTHS";
  public static final String OPERATION_NEXT_X_MONTHS = "NEXT_X_MONTHS";
  public static final String OPERATION_LAST_X_YEARS = "LAST_X_YEARS";
  public static final String OPERATION_NEXT_X_YEARS = "NEXT_X_YEARS";

  public static final Map<String, String> OPERATIONS = new LinkedHashMap();

  static
  {
    OPERATIONS.put(OPERATION_EQUALS, Cres.get().getString("viewOpEquals"));
    OPERATIONS.put(OPERATION_DOES_NOT_EQUAL, Cres.get().getString("viewOpDoesNotEqual"));
    OPERATIONS.put(OPERATION_IS_NULL, Cres.get().getString("viewOpIsNull"));
    OPERATIONS.put(OPERATION_IS_NOT_NULL, Cres.get().getString("viewOpIsNotNull"));
    OPERATIONS.put(OPERATION_CONTAINS, Cres.get().getString("viewOpContains"));
    OPERATIONS.put(OPERATION_DOES_NOT_CONTAIN, Cres.get().getString("viewOpDoesNotContain"));
    OPERATIONS.put(OPERATION_BEGINS_WITH, Cres.get().getString("viewOpBeginsWith"));
    OPERATIONS.put(OPERATION_DOES_NOT_BEGIN_WITH, Cres.get().getString("viewOpDoesNotBeginWith"));
    OPERATIONS.put(OPERATION_ENDS_WITH, Cres.get().getString("viewOpEndsWith"));
    OPERATIONS.put(OPERATION_DOES_NOT_END_WITH, Cres.get().getString("viewOpDoesNotEndWith"));
    OPERATIONS.put(OPERATION_IN,Cres.get().getString("viewOpIn"));
    OPERATIONS.put(OPERATION_IS_GREATER_THAN, Cres.get().getString("viewOpIsGreaterThan"));
    OPERATIONS.put(OPERATION_IS_GREATER_OR_EQUAL_THAN, Cres.get().getString("viewOpIsGreaterThanOrEqualTo"));
    OPERATIONS.put(OPERATION_IS_LESS_THAN, Cres.get().getString("viewOpIsLessThan"));
    OPERATIONS.put(OPERATION_IS_LESS_OR_EQUAL_THAN, Cres.get().getString("viewOpIsLessThanOrEqualTo"));
    OPERATIONS.put(OPERATION_ON, Cres.get().getString("viewOpOn"));
    OPERATIONS.put(OPERATION_ON_OR_AFTER, Cres.get().getString("viewOpOnOrAfter"));
    OPERATIONS.put(OPERATION_ON_OR_BEFORE, Cres.get().getString("viewOpOnOrBefore"));
    OPERATIONS.put(OPERATION_LAST_HOUR, Cres.get().getString("viewOpLastHour"));
    OPERATIONS.put(OPERATION_THIS_HOUR, Cres.get().getString("viewOpThisHour"));
    OPERATIONS.put(OPERATION_NEXT_HOUR, Cres.get().getString("viewOpNextHour"));
    OPERATIONS.put(OPERATION_YESTERDAY, Cres.get().getString("viewOpYesterday"));
    OPERATIONS.put(OPERATION_TODAY, Cres.get().getString("viewOpToday"));
    OPERATIONS.put(OPERATION_TOMORROW, Cres.get().getString("viewOpTomorrow"));
    OPERATIONS.put(OPERATION_LAST_WEEK, Cres.get().getString("viewOpLastWeek"));
    OPERATIONS.put(OPERATION_THIS_WEEK, Cres.get().getString("viewOpThisWeek"));
    OPERATIONS.put(OPERATION_NEXT_WEEK, Cres.get().getString("viewOpNextWeek"));
    OPERATIONS.put(OPERATION_LAST_MONTH, Cres.get().getString("viewOpLastMonth"));
    OPERATIONS.put(OPERATION_THIS_MONTH, Cres.get().getString("viewOpThisMonth"));
    OPERATIONS.put(OPERATION_NEXT_MONTH, Cres.get().getString("viewOpNextMonth"));
    OPERATIONS.put(OPERATION_LAST_YEAR, Cres.get().getString("viewOpLastYear"));
    OPERATIONS.put(OPERATION_THIS_YEAR, Cres.get().getString("viewOpThisYear"));
    OPERATIONS.put(OPERATION_NEXT_YEAR, Cres.get().getString("viewOpNextYear"));
    OPERATIONS.put(OPERATION_LAST_X_HOURS, Cres.get().getString("viewOpLastXHours"));
    OPERATIONS.put(OPERATION_NEXT_X_HOURS, Cres.get().getString("viewOpNextXHours"));
    OPERATIONS.put(OPERATION_LAST_X_DAYS, Cres.get().getString("viewOpLastXDays"));
    OPERATIONS.put(OPERATION_NEXT_X_DAYS, Cres.get().getString("viewOpNextXDays"));
    OPERATIONS.put(OPERATION_LAST_X_WEEKS, Cres.get().getString("viewOpLastXWeeks"));
    OPERATIONS.put(OPERATION_NEXT_X_WEEKS, Cres.get().getString("viewOpNextXWeeks"));
    OPERATIONS.put(OPERATION_LAST_X_MONTHS, Cres.get().getString("viewOpLastXMonths"));
    OPERATIONS.put(OPERATION_NEXT_X_MONTHS, Cres.get().getString("viewOpNextXMonths"));
    OPERATIONS.put(OPERATION_LAST_X_YEARS, Cres.get().getString("viewOpLastXYears"));
    OPERATIONS.put(OPERATION_NEXT_X_YEARS, Cres.get().getString("viewOpNextXYears"));
  }

  public static final String FIELD_LOGICAL = "logical";
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_STORAGE = "storage";
  public static final String FIELD_TABLE = "table";
  public static final String FIELD_COLUMN = "column";
  public static final String FIELD_OPERATION = "operation";
  public static final String FIELD_VALUE = "value";
  public static final String FIELD_VALUE_EDITOR = "editor";
  public static final String FIELD_NESTED = "nested";

  public static final TableFormat SIMPLE_FILTERS_FORMAT = new TableFormat();

  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_LOGICAL + "><I><A=" + LOGICAL_OPERATION_AND + "><D=" + Cres.get().getString("viewLogicalOperation") + ">");
    ff.addSelectionValue(LOGICAL_OPERATION_NONE, " ");
    ff.addSelectionValue(LOGICAL_OPERATION_AND, Cres.get().getString("viewLogicalAnd"));
    ff.addSelectionValue(LOGICAL_OPERATION_OR, Cres.get().getString("viewLogicalOr"));
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_TYPE + "><I><A=" + TYPE_CONDITION + "><D=" + Cres.get().getString("type") + ">");
    ff.addSelectionValue(TYPE_CONDITION, Cres.get().getString("condition"));
    ff.addSelectionValue(TYPE_NESTED_CONDITIONS, Cres.get().getString("viewNestedConditions"));
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_COLUMN + "><S><F=E><D=" + Cres.get().getString("column") + ">");
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_OPERATION + "><S><D=" + Cres.get().getString("operation") + ">");
    ff.setSelectionValues(OPERATIONS);
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_VALUE + "><S><D=" + Cres.get().getString("value") + ">");
    ff.setEditor(StringFieldFormat.EDITOR_EXPRESSION);
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_VALUE_EDITOR + "><S><D=" + Cres.get().getString("dtEditorViewer") + ">");
    ff.setNullable(true);
    ff.setSelectionValues(DataTableUtils.getEditorSelectionValues());
    ff.setExtendableSelectionValues(true);
    ff.setHidden(true);
    SIMPLE_FILTERS_FORMAT.addField(ff);

    ff = FieldFormat.create("<" + FIELD_NESTED + "><T><F=NI><D=" + Cres.get().getString("viewNestedConditions") + ">");
    SIMPLE_FILTERS_FORMAT.addField(ff);

    String ref = FIELD_LOGICAL + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    String exp = "{" + FIELD_TYPE + "#" + DefaultReferenceResolver.ROW + "} != 0";
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);

    ref = FIELD_NESTED + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_TYPE + "} == " + TYPE_NESTED_CONDITIONS;
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);

    ref = FIELD_LOGICAL;
    exp = "{" + FIELD_TYPE + "#" + DefaultReferenceResolver.ROW + "} != 0 ? {" + FIELD_LOGICAL + "} : " + DefaultFunctions.INTEGER + "(" + LOGICAL_OPERATION_NONE + ")";
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);

    ref = FIELD_COLUMN + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_TYPE + "} == " + TYPE_CONDITION;
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);

    ref = FIELD_OPERATION + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_TYPE + "} == " + TYPE_CONDITION;
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);

    ref = FIELD_VALUE + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "{" + FIELD_TYPE + "} == " + TYPE_CONDITION;
    SIMPLE_FILTERS_FORMAT.addBinding(ref, exp);
  }

  public static final TableFormat FORMAT = SIMPLE_FILTERS_FORMAT.clone();

  static
  {
    FieldFormat ff = FieldFormat.create("<" + FIELD_STORAGE + "><S><F=H><D=" + Cres.get().getString("storage") + ">");
    FORMAT.addField(ff, 2);

    ff = FieldFormat.create("<" + FIELD_TABLE + "><S><F=H><D=" + Cres.get().getString("table") + ">");
    FORMAT.addField(ff, 3);


    String ref = FIELD_OPERATION + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    String exp = DefaultFunctions.CALL_FUNCTION + "({" + FIELD_STORAGE + "}, '" + StorageHelper.F_STORAGE_OPERATIONS + "', {" + FIELD_TABLE + "}, {" + FIELD_COLUMN + "})";
    FORMAT.addBinding(ref, exp);

    FORMAT.setNamingExpression(DefaultFunctions.PRINT + "(" + DefaultFunctions.DT + "(), '(" + DefaultFunctions.DR + "() > 0 ? ({" + FIELD_LOGICAL + "} == " + LOGICAL_OPERATION_AND + " ? \"AND \" : \"OR \") : \"\") + ({"
            + FIELD_TYPE + "} == " + TYPE_CONDITION + " ? {" + FIELD_COLUMN + "} + \" \" + {" + FIELD_OPERATION + "} + \" \" + {"
            + FIELD_VALUE + "} : \"(\" + {" + FIELD_NESTED + "} + \")\")', ' ')");
  }

  private int logical;
  private int type;
  private String storage;
  private String table;
  private String column;
  private String operation;
  private String value;
  private String editor;

  private List<ViewFilterElement> nested;

  private boolean local = false;

  public ViewFilterElement(DataRecord data)
  {
    super(FORMAT, data);
  }

  public ViewFilterElement()
  {
    super(FORMAT);
  }

  public int getLogical()
  {
    return logical;
  }

  public void setLogical(int logical)
  {
    this.logical = logical;
  }

  public int getType()
  {
    return type;
  }

  public void setType(int type)
  {
    this.type = type;
  }

  public String getStorage()
  {
    return storage;
  }

  public void setStorage(String storage)
  {
    this.storage = storage;
  }

  public String getTable()
  {
    return table;
  }

  public void setTable(String table)
  {
    this.table = table;
  }

  public String getColumn()
  {
    return column;
  }

  public void setColumn(String column)
  {
    this.column = column;
  }

  public String getOperation()
  {
    return operation;
  }

  public void setOperation(String operation)
  {
    this.operation = operation;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getEditor()
  {
    return editor;
  }

  public void setEditor(String editor)
  {
    this.editor = editor;
  }

  public List<ViewFilterElement> getNested()
  {
    return nested;
  }

  public void setNested(List<ViewFilterElement> nested)
  {
    this.nested = nested;
  }

  public void addNested(ViewFilterElement nestedElement)
  {
    if (type != TYPE_NESTED_CONDITIONS)
    {
      throw new IllegalStateException("Filter element is not switched into nested conditions mode");
    }
    if (nested == null)
    {
      // Оптимизация: используем ArrayList вместо LinkedList для лучшей производительности
      nested = new ArrayList<ViewFilterElement>();
    }
    nested.add(nestedElement);
  }

  public boolean isLocal()
  {
    return local;
  }

  public void setLocal(boolean local)
  {
    this.local = local;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ViewFilterElement that = (ViewFilterElement) o;
    return logical == that.logical &&
        type == that.type &&
        local == that.local &&
        Objects.equals(storage, that.storage) &&
        Objects.equals(table, that.table) &&
        Objects.equals(column, that.column) &&
        Objects.equals(operation, that.operation) &&
        Objects.equals(value, that.value) &&
        Objects.equals(nested, that.nested);
  }

  @Override
  public String toString()
  {
    return "ViewFilterElement{" +
        "logical=" + logical +
        ", type=" + type +
        ", storage='" + storage + '\'' +
        ", table='" + table + '\'' +
        ", column='" + column + '\'' +
        ", operation='" + operation + '\'' +
        ", value='" + value + '\'' +
        ", nested=" + nested +
        ", local=" + local +
        '}';
  }
}
