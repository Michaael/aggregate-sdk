package com.tibbo.aggregate.common.datatable;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.ImmutableMap;
import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.data.Data;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.TransferEncodingHelper;
import com.tibbo.aggregate.common.datatable.field.BooleanFieldFormat;
import com.tibbo.aggregate.common.datatable.field.ColorFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DataFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DataTableFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DateFieldFormat;
import com.tibbo.aggregate.common.datatable.field.DoubleFieldFormat;
import com.tibbo.aggregate.common.datatable.field.FloatFieldFormat;
import com.tibbo.aggregate.common.datatable.field.IntFieldFormat;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.datatable.validator.AbstractFieldValidator;
import com.tibbo.aggregate.common.datatable.validator.ExpressionValidator;
import com.tibbo.aggregate.common.datatable.validator.FieldValidator;
import com.tibbo.aggregate.common.datatable.validator.IdValidator;
import com.tibbo.aggregate.common.datatable.validator.LimitsValidator;
import com.tibbo.aggregate.common.datatable.validator.NonNullValidator;
import com.tibbo.aggregate.common.datatable.validator.RegexValidator;
import com.tibbo.aggregate.common.datatable.validator.ValidatorHelper;
import com.tibbo.aggregate.common.util.CloneUtils;
import com.tibbo.aggregate.common.util.Element;
import com.tibbo.aggregate.common.util.ElementList;
import com.tibbo.aggregate.common.util.PublicCloneable;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.Util;

/**
 * <code>FieldFormat</code> is a part of <code>TableFormat</code> that describes single <code>DataTable</code> field.
 */

public abstract class FieldFormat<T> implements Cloneable, PublicCloneable
{
  private static final Map<Class, Character> CLASS_TO_TYPE = new ConcurrentHashMap<>();
  private static final Map<String, Class<? extends AbstractFieldValidator>> VALIDATOR_TO_TYPE = new LinkedHashMap();
  
  private static final Map<Object, String> TYPE_SELECTION_VALUES = new LinkedHashMap();
  
  public static final char INTEGER_FIELD = 'I';
  public static final char STRING_FIELD = 'S';
  public static final char BOOLEAN_FIELD = 'B';
  public static final char LONG_FIELD = 'L';
  public static final char FLOAT_FIELD = 'F';
  public static final char DOUBLE_FIELD = 'E';
  public static final char DATE_FIELD = 'D';
  public static final char DATATABLE_FIELD = 'T';
  public static final char COLOR_FIELD = 'C';
  public static final char DATA_FIELD = 'A';
  
  private static final String ELEMENT_FLAGS = "F";
  private static final String ELEMENT_DEFAULT_VALUE = "A";
  private static final String ELEMENT_DESCRIPTION = "D";
  private static final String ELEMENT_HELP = "H";
  private static final String ELEMENT_SELECTION_VALUES = "S";
  private static final String ELEMENT_VALIDATORS = "V";
  private static final String ELEMENT_EDITOR = "E";
  private static final String ELEMENT_EDITOR_OPTIONS = "O";
  private static final String ELEMENT_ICON = "I";
  private static final String ELEMENT_GROUP = "G";
  
  private static final char ADVANCED_FLAG = 'A';
  private static final char NOT_REPLICATED_FLAG = 'C';
  private static final char EXTENDABLE_SELECTION_VALUES_FLAG = 'E';
  private static final char HIDDEN_FLAG = 'H';
  private static final char INLINE_DATA_FLAG = 'I';
  private static final char KEY_FIELD_FLAG = 'K';
  private static final char NULLABLE_FLAG = 'N';
  private static final char OPTIONAL_FLAG = 'O';
  private static final char READ_ONLY_FLAG = 'R';
  private static final char DEFAULT_OVERRIDE = 'D';
  private static final char SHALLOW_FLAG = 'S';
  private static final char ENCRYPTED_FLAG = 'Y';
  
  public static final char VALIDATOR_LIMITS = 'L';
  public static final char VALIDATOR_REGEX = 'R';
  public static final char VALIDATOR_NON_NULL = 'N';
  public static final char VALIDATOR_ID = 'I';
  public static final char VALIDATOR_EXPRESSION = 'E';
  
  static
  {
    
    CLASS_TO_TYPE.put(Integer.class, INTEGER_FIELD);
    CLASS_TO_TYPE.put(String.class, STRING_FIELD);
    CLASS_TO_TYPE.put(Boolean.class, BOOLEAN_FIELD);
    CLASS_TO_TYPE.put(Long.class, LONG_FIELD);
    CLASS_TO_TYPE.put(Float.class, FLOAT_FIELD);
    CLASS_TO_TYPE.put(Double.class, DOUBLE_FIELD);
    CLASS_TO_TYPE.put(Date.class, DATE_FIELD);
    CLASS_TO_TYPE.put(DataTable.class, DATATABLE_FIELD);
    CLASS_TO_TYPE.put(Color.class, COLOR_FIELD);
    CLASS_TO_TYPE.put(Data.class, DATA_FIELD);
    
    VALIDATOR_TO_TYPE.put(String.valueOf(VALIDATOR_LIMITS), LimitsValidator.class);
    VALIDATOR_TO_TYPE.put(String.valueOf(VALIDATOR_REGEX), RegexValidator.class);
    VALIDATOR_TO_TYPE.put(String.valueOf(VALIDATOR_NON_NULL), NonNullValidator.class);
    VALIDATOR_TO_TYPE.put(String.valueOf(VALIDATOR_ID), IdValidator.class);
    VALIDATOR_TO_TYPE.put(String.valueOf(VALIDATOR_EXPRESSION), ExpressionValidator.class);
    
    TYPE_SELECTION_VALUES.put(String.valueOf(INTEGER_FIELD), Cres.get().getString("dtInteger"));
    TYPE_SELECTION_VALUES.put(String.valueOf(STRING_FIELD), Cres.get().getString("dtString"));
    TYPE_SELECTION_VALUES.put(String.valueOf(BOOLEAN_FIELD), Cres.get().getString("dtBoolean"));
    TYPE_SELECTION_VALUES.put(String.valueOf(LONG_FIELD), Cres.get().getString("dtLong"));
    TYPE_SELECTION_VALUES.put(String.valueOf(FLOAT_FIELD), Cres.get().getString("dtFloat"));
    TYPE_SELECTION_VALUES.put(String.valueOf(DOUBLE_FIELD), Cres.get().getString("dtDouble"));
    TYPE_SELECTION_VALUES.put(String.valueOf(DATE_FIELD), Cres.get().getString("date"));
    TYPE_SELECTION_VALUES.put(String.valueOf(DATATABLE_FIELD), Cres.get().getString("dtDataTable"));
    TYPE_SELECTION_VALUES.put(String.valueOf(COLOR_FIELD), Cres.get().getString("color"));
    TYPE_SELECTION_VALUES.put(String.valueOf(DATA_FIELD), Cres.get().getString("dtDataBlock"));
  }
  
  private String name;
  private boolean nullable;
  private boolean optional;
  private boolean extendableSelectionValues;
  private boolean readonly;
  private boolean notReplicated;
  private boolean hidden;
  private boolean keyField;
  private boolean inlineData;
  private boolean advanced;
  private boolean defaultOverride;
  private boolean shallow;
  private boolean encrypted;
  private T defaultValue;
  private String description;
  private String help;
  private String group;
  private String editor;
  private String editorOptions;
  private Map<T, String> selectionValues;
  private List<FieldValidator> validators = new LinkedList<>();
  private String icon;
  
  private boolean transferEncode;
  
  private boolean immutable;
  
  private String cachedDefaultDescription;
  
  public static final String EDITOR_LIST = "list";
  public static final String EDITOR_BAR = "bar";
  public static final String EDITOR_BYTES = "bytes";
  public static final String EDITOR_INSTANCE = "instance";
  public static final String EDITOR_FOREIGN_INSTANCE = "foreignInstance";
  public static final String EDITOR_FORMAT_STRING = "formatString";

  public abstract char getType();
  
  public abstract Class getFieldClass();
  
  public abstract Class getFieldWrappedClass();

  public abstract T getNotNullDefault();
  
  public abstract T valueFromString(String value, ClassicEncodingSettings settings, boolean validate);
  
  public abstract String valueToString(T value, ClassicEncodingSettings settings);
  
  /**
   * Constructs a <code>FieldFormat</code> with the specified name.
   */
  protected FieldFormat(String name)
  {
    this.name = name;
  }
  
  /**
   * Constructs a <code>FieldFormat</code> with specified name that will hold values of class <code>valueClass</code>.
   */
  public static <S> FieldFormat<S> create(String name, Class valueClass)
  {
    if (DataTable.class.isAssignableFrom(valueClass))
    {
      valueClass = DataTable.class;
    }
    
    Character type = CLASS_TO_TYPE.get(valueClass);
    
    if (type == null)
    {
      throw new IllegalArgumentException("Unknown field class: " + valueClass.getName());
    }
    
    return create(name, type);
  }
  
  public static FieldFormat create(String name, char type, boolean validate)
  {
    FieldFormat ff = create(name, type);
    if (validate)
    {
      ff.validateName();
    }
    return ff;
  }
  
  /**
   * Constructs a <code>FieldFormat</code> with specified name of type <code>type</code>.
   */
  public static FieldFormat create(String name, char type)
  {
    switch (type)
    {
      case FieldFormat.INTEGER_FIELD:
        return new IntFieldFormat(name);
      
      case FieldFormat.STRING_FIELD:
        return new StringFieldFormat(name);
      
      case FieldFormat.BOOLEAN_FIELD:
        return new BooleanFieldFormat(name);
      
      case FieldFormat.LONG_FIELD:
        return new LongFieldFormat(name);
      
      case FieldFormat.FLOAT_FIELD:
        return new FloatFieldFormat(name);
      
      case FieldFormat.DOUBLE_FIELD:
        return new DoubleFieldFormat(name);
      
      case FieldFormat.DATE_FIELD:
        return new DateFieldFormat(name);
      
      case FieldFormat.DATATABLE_FIELD:
        return new DataTableFieldFormat(name);
      
      case FieldFormat.COLOR_FIELD:
        return new ColorFieldFormat(name);
      
      case FieldFormat.DATA_FIELD:
        return new DataFieldFormat(name);
      
      default:
        throw new IllegalArgumentException("Unknown field type: " + type);
    }
  }
  
  public static <S> FieldFormat<S> create(String name, char type, String description)
  {
    FieldFormat<S> ff = create(name, type);
    ff.setDescription(description);
    return ff;
  }
  
  public static <S> FieldFormat<S> create(String name, char type, String description, S defaultValue)
  {
    return create(name, type, description, defaultValue, false, null);
  }
  
  public static <S> FieldFormat<S> create(String name, char type, String description, S defaultValue, String group)
  {
    return create(name, type, description, defaultValue, false, group);
  }
  
  public static <S> FieldFormat<S> create(String name, char type, String description, S defaultValue, boolean nullable)
  {
    return create(name, type, description, defaultValue, nullable, null);
  }
  
  public static <S> FieldFormat<S> create(String name, char type, String description, S defaultValue, boolean nullable, String group)
  {
    FieldFormat<S> ff = create(name, type, description);
    ff.setNullable(nullable);
    ff.setDefault(defaultValue);
    ff.setGroup(group);
    return ff;
  }
  
  public static <S> FieldFormat<S> create(String format, ClassicEncodingSettings settings)
  {
    return create(format, settings, true);
  }
  
  public static <S> FieldFormat<S> create(String format, ClassicEncodingSettings settings, boolean validate)
  {
    ElementList els = StringUtils.elements(format, settings.isUseVisibleSeparators());
    
    String name = null;
    char type;
    
    try
    {
      name = els.get(0).getValue();
      type = els.get(1).getValue().charAt(0);
    }
    catch (IndexOutOfBoundsException ex1)
    {
      throw new IllegalArgumentException(ex1.getMessage() + ", format was '" + format + "'", ex1);
    }
    
    FieldFormat<S> ff = create(name, type);
    
    if (validate)
    {
      ff.validateName();
    }
    
    Element el = els.getElement(ELEMENT_FLAGS);
    
    if (el != null)
    {
      String flags = el.getValue();
      ff.setNullable(flags.indexOf(NULLABLE_FLAG) != -1 ? true : false);
      ff.setOptional(flags.indexOf(OPTIONAL_FLAG) != -1 ? true : false);
      ff.setExtendableSelectionValues(flags.indexOf(EXTENDABLE_SELECTION_VALUES_FLAG) != -1 ? true : false);
      ff.setReadonly(flags.indexOf(READ_ONLY_FLAG) != -1 ? true : false);
      ff.setNotReplicated(flags.indexOf(NOT_REPLICATED_FLAG) != -1 ? true : false);
      ff.setHidden(flags.indexOf(HIDDEN_FLAG) != -1 ? true : false);
      ff.setKeyField(flags.indexOf(KEY_FIELD_FLAG) != -1 ? true : false);
      ff.setInlineData(flags.indexOf(INLINE_DATA_FLAG) != -1 ? true : false);
      ff.setAdvanced(flags.indexOf(ADVANCED_FLAG) != -1 ? true : false);
      ff.setDefaultOverride(flags.indexOf(DEFAULT_OVERRIDE) != -1 ? true : false);
      ff.setShallow(flags.indexOf(SHALLOW_FLAG) != -1 ? true : false);
      ff.setEncrypted(flags.indexOf(ENCRYPTED_FLAG) != -1 ? true : false);
    }
    
    el = els.getElement(ELEMENT_DEFAULT_VALUE);
    
    if (el != null)
    {
      ff.setDefaultFromString(el.getValue(), settings, validate);
    }
    
    el = els.getElement(ELEMENT_DESCRIPTION);
    
    if (el != null)
    {
      ff.setDescription(DataTableUtils.transferDecode(el.getValue()));
    }
    
    el = els.getElement(ELEMENT_HELP);
    
    if (el != null)
    {
      ff.setHelp(DataTableUtils.transferDecode(el.getValue()));
    }
    
    el = els.getElement(ELEMENT_SELECTION_VALUES);
    
    if (el != null)
    {
      ff.createSelectionValues(el.getValue(), settings);
    }
    
    el = els.getElement(ELEMENT_VALIDATORS);
    
    if (el != null)
    {
      ff.createValidators(el.getValue(), settings);
    }
    
    el = els.getElement(ELEMENT_EDITOR);
    
    if (el != null)
    {
      ff.setEditor(DataTableUtils.transferDecode(el.getValue()));
    }
    
    el = els.getElement(ELEMENT_EDITOR_OPTIONS);
    
    if (el != null)
    {
      ff.setEditorOptions(DataTableUtils.transferDecode(el.getValue()));
    }
    
    el = els.getElement(ELEMENT_ICON);
    
    if (el != null)
    {
      ff.setIcon(DataTableUtils.transferDecode(el.getValue()));
    }
    
    el = els.getElement(ELEMENT_GROUP);
    
    if (el != null)
    {
      ff.setGroup(DataTableUtils.transferDecode(el.getValue()));
    }
    
    return ff;
  }
  
  private void validateName()
  {
    try
    {
      ValidatorHelper.NAME_SYNTAX_VALIDATOR.validate(getName());
    }
    catch (ValidationException ve)
    {
      throw new RuntimeException(MessageFormat.format(Cres.get().getString("dtIllegalFieldValue"), getName(), toDetailedString()) + ve.getMessage(), ve);
    }
  }
  
  /**
   * Decodes <code>FieldFormat</code> from string.
   */
  public static <S> FieldFormat<S> create(String format)
  {
    return create(format, new ClassicEncodingSettings(true), true);
  }
  
  private void createSelectionValues(String source, ClassicEncodingSettings settings)
  {
    if (source.length() == 0)
    {
      return;
    }
    
    Map<T, String> values = new LinkedHashMap();
    
    ElementList els = StringUtils.elements(source, settings.isUseVisibleSeparators());
    for (Element el : els)
    {
      String valueSource = el.getValue();
      
      T selectionValue = valueFromEncodedString(valueSource, settings, true);
      
      String desc = el.getName() != null ? el.getName() : selectionValue.toString();
      values.put(selectionValue, desc);
    }
    
    setSelectionValues(values.size() > 0 ? values : null);
  }
  
  public T valueFromEncodedString(String source)
  {
    return valueFromEncodedString(source, new ClassicEncodingSettings(false), true);
  }
  
  public T valueFromEncodedString(String source, ClassicEncodingSettings settings, boolean validate)
  {
    final String nullElement = settings.isUseVisibleSeparators() ? DataTableUtils.DATA_TABLE_VISIBLE_NULL : DataTableUtils.DATA_TABLE_NULL;
    final boolean sourceIsNull = source.equals(nullElement);
    return sourceIsNull ? null : valueFromString(isTransferEncode() ? DataTableUtils.transferDecode(source) : source, settings, validate);
  }
  
  /**
   * Converts <code>value</code> string to the object of type suitable for this <code>FieldFormat</code>.
   */
  public T valueFromString(String value)
  {
    return valueFromString(value, null, false);
  }
  
  /**
   * Converts value suitable for this field format to string.
   */
  public String valueToString(T value)
  {
    return valueToString(value, null);
  }
  
  public String valueToEncodedString(T value, ClassicEncodingSettings settings)
  {
    return valueToEncodedString(value, settings, new StringBuilder(), 1).toString();
  }
  
  public StringBuilder valueToEncodedString(T value, ClassicEncodingSettings settings, StringBuilder sb, Integer encodeLevel)
  {
    String strVal = valueToString(value, settings);
    
    if (strVal == null)
    {
      return (settings == null || !settings.isUseVisibleSeparators()) ? sb.append(DataTableUtils.DATA_TABLE_NULL) : sb.append(DataTableUtils.DATA_TABLE_VISIBLE_NULL);
    }
    
    if (isTransferEncode())
      TransferEncodingHelper.encode(strVal, sb, encodeLevel);
    else
      sb.append(strVal);
    
    return sb;
  }
  
  /**
   * Sets default value of field from its string representation.
   */
  public void setDefaultFromString(String value)
  {
    setDefaultFromString(value, new ClassicEncodingSettings(false), true);
  }
  
  public void setDefaultFromString(String value, ClassicEncodingSettings settings, boolean validate)
  {
    if (value.length() == 0)
    {
      return;
    }
    
    // Overriding validate flag here, as default value may contain non-valid table
    setDefault(valueFromEncodedString(value, settings, false));
  }
  
  /**
   * Sets default value of field.
   */
  public FieldFormat<T> setDefault(T value)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    try
    {
      defaultValue = checkAndConvertValue(value, true);
    }
    catch (ContextException ex)
    {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
    return this;
  }
  
  private String getEncodedSelectionValues(ClassicEncodingSettings settings)
  {
    if (selectionValues == null)
    {
      return null;
    }
    
    StringBuffer enc = new StringBuffer();
    
    for (T value : selectionValues.keySet())
    {
      String valueDesc = selectionValues.get(value);
      enc.append(new Element(valueDesc, valueToEncodedString(value, settings)).encode(settings));
    }
    
    return enc.toString();
  }
  
  public String getEncodedValidators(ClassicEncodingSettings settings)
  {
    if (validators.size() == 0)
    {
      return null;
    }
    
    StringBuffer enc = new StringBuffer();
    
    for (FieldValidator fv : validators)
    {
      if (fv.getType() != null)
      {
        enc.append(new Element(String.valueOf(fv.getType()), fv.encode()).encode(settings));
      }
    }
    
    return enc.length() != 0 ? enc.toString() : null;
  }
  
  private String getEncodedFlags()
  {
    StringBuilder buf = new StringBuilder();
    if (isNullable())
    {
      buf.append(NULLABLE_FLAG);
    }
    if (isOptional())
    {
      buf.append(OPTIONAL_FLAG);
    }
    if (isReadonly())
    {
      buf.append(READ_ONLY_FLAG);
    }
    if (isNotReplicated())
    {
      buf.append(NOT_REPLICATED_FLAG);
    }
    if (isExtendableSelectionValues())
    {
      buf.append(EXTENDABLE_SELECTION_VALUES_FLAG);
    }
    if (isHidden())
    {
      buf.append(HIDDEN_FLAG);
    }
    if (isKeyField())
    {
      buf.append(KEY_FIELD_FLAG);
    }
    if (isInlineData())
    {
      buf.append(INLINE_DATA_FLAG);
    }
    if (isAdvanced())
    {
      buf.append(ADVANCED_FLAG);
    }
    if (isDefaultOverride())
    {
      buf.append(DEFAULT_OVERRIDE);
    }
    if (isShallow())
    {
      buf.append(SHALLOW_FLAG);
    }
    if (isEncrypted())
    {
      buf.append(ENCRYPTED_FLAG);
    }
    return buf.toString();
  }
  
  private static void encAppend(StringBuilder buffer, String name, String value, ClassicEncodingSettings settings)
  {
    encAppend(buffer, name, value, settings, false);
  }
  
  private static void encAppend(StringBuilder buffer, String name, String value, ClassicEncodingSettings settings, boolean allowEmptyString)
  {
    if (value != null && (allowEmptyString || !value.isEmpty()))
    {
      new Element(name, value).encode(buffer, settings, false, 0);
    }
  }
  
  public String encode()
  {
    return encode(true);
  }
  
  public String encode(boolean useVisibleSeparators)
  {
    return encode(new ClassicEncodingSettings(useVisibleSeparators));
  }
  
  public String encode(ClassicEncodingSettings settings)
  {
    StringBuilder data = new StringBuilder();
    
    new Element(null, getName()).encode(data, settings);
    new Element(null, String.valueOf(getType())).encode(data, settings);
    
    encAppend(data, ELEMENT_FLAGS, getEncodedFlags(), settings);
    
    if (settings.isEncodeDefaultValues())
    {
      
      new Element(ELEMENT_DEFAULT_VALUE, valueToEncodedString(getDefaultValue(), settings)).encode(data, settings);
    }
    
    encAppend(data, ELEMENT_DESCRIPTION, DataTableUtils.transferEncode(description), settings);
    encAppend(data, ELEMENT_HELP, DataTableUtils.transferEncode(help), settings);
    encAppend(data, ELEMENT_SELECTION_VALUES, getEncodedSelectionValues(settings), settings);
    encAppend(data, ELEMENT_VALIDATORS, getEncodedValidators(settings), settings);
    encAppend(data, ELEMENT_EDITOR, DataTableUtils.transferEncode(editor), settings);
    encAppend(data, ELEMENT_EDITOR_OPTIONS, DataTableUtils.transferEncode(editorOptions), settings, true);
    encAppend(data, ELEMENT_ICON, DataTableUtils.transferEncode(icon), settings);
    encAppend(data, ELEMENT_GROUP, DataTableUtils.transferEncode(group), settings);
    
    return new String(data);
  }
  
  public boolean extend(FieldFormat other)
  {
    return extendMessage(other) == null;
  }
  
  public String extendMessage(FieldFormat other)
  {
    if (!getName().equals(other.getName()))
    {
      return "Wrong name: need " + getName() + ", found " + other.getName();
    }
    if (other.hasDescription() && !Util.equals(getDescription(), other.getDescription()))
    {
      return "Wrong description: need " + getDescription() + ", found " + other.getDescription();
    }
    if (!Util.equals(getHelp(), other.getHelp()))
    {
      return "Wrong help: need " + getHelp() + ", found " + other.getHelp();
    }
    if (getType() != other.getType())
    {
      return "Wrong type: need " + getType() + ", found " + other.getType();
    }
    if (!isNullable() && other.isNullable())
    {
      return "Different nullable flags: need " + isNullable() + ", found " + other.isNullable();
    }
    if (isReadonly() != other.isReadonly())
    {
      return "Different readonly flags: need " + isReadonly() + ", found " + other.isReadonly();
    }
    if (isHidden() != other.isHidden())
    {
      return "Different hidden flags: need " + isHidden() + ", found " + other.isHidden();
    }
    if (isEncrypted() != other.isEncrypted())
    {
      return "Different encrypted flags: need " + isEncrypted() + ", found " + other.isEncrypted();
    }
    if (isKeyField() != other.isKeyField())
    {
      return "Different kay field flags: need " + isKeyField() + ", found " + other.isKeyField();
    }
    
    if (!isExtendableSelectionValues() || !other.isExtendableSelectionValues())
    {
      boolean selectionValuesOk = other.getSelectionValues() == null || Util.equals(getSelectionValues(), other.getSelectionValues());
      if (!selectionValuesOk && getSelectionValues() != null)
      {
        boolean foundMissingValues = false;
        for (Object value : getSelectionValues().keySet())
        {
          if (!other.getSelectionValues().containsKey(value))
          {
            foundMissingValues = true;
          }
        }
        if (!foundMissingValues)
        {
          selectionValuesOk = true;
        }
      }
      
      if (!selectionValuesOk)
      {
        return "Different selection values: need " + other.getSelectionValues() + ", found " + getSelectionValues();
      }
    }
    
    if (!Util.equals(getEditor(), other.getEditor()))
    {
      return "Different editor: need " + getEditor() + ", found " + other.getEditor();
    }
    if (!Util.equals(getEditorOptions(), other.getEditorOptions()))
    {
      return "Different editor options: need " + getEditorOptions() + ", found " + other.getEditorOptions();
    }
    if (!Util.equals(getIcon(), other.getIcon()))
    {
      return "Wrong icon: need " + getIcon() + ", found " + other.getIcon();
    }
    if (!Util.equals(getGroup(), other.getGroup()))
    {
      return "Wrong group: need " + getGroup() + ", found " + other.getGroup();
    }
    
    List<FieldValidator> otherValidators = other.getValidators();
    for (FieldValidator otherValidator : otherValidators)
    {
      if (!getValidators().contains(otherValidator))
      {
        return "Different validators: need " + getValidators() + ", found " + other.getValidators();
      }
    }
    
    if (getDefaultValue() != null && getDefaultValue() instanceof DataTable && other.getDefaultValue() != null && other.getDefaultValue() instanceof DataTable)
    {
      DataTable my = (DataTable) getDefaultValue();
      DataTable another = (DataTable) other.getDefaultValue();
      String msg = my.getFormat().extendMessage(another.getFormat());
      if (msg != null)
      {
        return "Field format doesn't match: " + msg;
      }
    }
    return null;
  }
  
  /**
   * Adds new field validator.
   */
  public FieldFormat<T> addValidator(FieldValidator validator)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    validators.add(validator);
    return this;
  }
  
  public void setValidators(Collection<FieldValidator> validators)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.validators.clear();
    this.validators.addAll(validators);
  }
  
  public void createValidators(String source, ClassicEncodingSettings settings)
  {
    if (source == null || source.length() == 0)
    {
      return;
    }
    
    ElementList validatorsData = StringUtils.elements(source, settings.isUseVisibleSeparators());
    
    for (Element el : validatorsData)
    {
      char validatorType = el.getName().charAt(0);
      String validatorParams = el.getValue();
      
      switch (validatorType)
      {
        case VALIDATOR_LIMITS:
          addValidator(new LimitsValidator(this, validatorParams));
          break;
        
        case VALIDATOR_REGEX:
          addValidator(new RegexValidator(validatorParams));
          break;
        
        case VALIDATOR_NON_NULL:
          addValidator(new NonNullValidator(validatorParams));
          break;
        
        case VALIDATOR_ID:
          addValidator(new IdValidator());
          break;
        
        case VALIDATOR_EXPRESSION:
          addValidator(new ExpressionValidator(validatorParams));
          break;
      }
    }
  }
  
  public T checkAndConvertValue(T value, boolean validate) throws ValidationException
  {
    return checkAndConvertValue(null, null, null, value, validate);
  }
  
  public T checkAndConvertValue(Context context, ContextManager contextManager, CallerController caller, T value, boolean validate) throws ValidationException
  {
    if (!isNullable() && value == null)
    {
      throw new ValidationException(MessageFormat.format(Cres.get().getString("dtNullsNotPermitted"), this.toString()));
    }
    
    value = (T) convertValue(value);
    
    if (value != null && !isExtendableSelectionValues() && selectionValues != null && !selectionValues.containsKey(value))
    {
      if (validate)
      {
        throw new ValidationException(Cres.get().getString("dtValueNotInSelVals") + value + " (" + value.getClass().getName() + ")");
      }
      else
      {
        value = getDefaultValue();
      }
    }
    
    if (validate)
    {
      for (FieldValidator<T> fv : validators)
      {
        value = fv.validate(context, contextManager, caller, value);
      }
    }
    
    return value;
  }
  
  protected Object convertValue(Object value) throws ValidationException
  {
    if (value != null)
    {
      if (getFieldClass() == value.getClass() || getFieldWrappedClass() == value.getClass() || getFieldClass().isAssignableFrom(value.getClass())
          || getFieldWrappedClass().isAssignableFrom(value.getClass()))
      {
        return value;
      }
      
      throw new ValidationException("Invalid class, need '" + getFieldWrappedClass().getName() + "', found '" + value.getClass().getName() + "'");
    }
    
    return value;
  }
  
  /**
   * Returns human-readable name of field type.
   */
  public String getTypeName()
  {
    return TYPE_SELECTION_VALUES.get(String.valueOf(getType()));
  }
  
  /**
   * Returns field name.
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * Returns true if field is nullable, i.e. may contain NULL values.
   */
  public boolean isNullable()
  {
    return nullable;
  }
  
  /**
   * Returns true if field is shallow.
   */
  public boolean isShallow()
  {
    return shallow;
  }
  
  /**
   * Returns default value of the field.
   */
  public T getDefaultValue()
  {
    // Don't call this method before format construction is finished, otherwise setting format to nullable will not set default value to null
    
    if (defaultValue == null && !isNullable())
    {
      defaultValue = getNotNullDefault();
    }
    
    return defaultValue;
  }
  
  public T getDefaultValueCopy()
  {
    T def = getDefaultValue();
    return def == null ? null : (T) CloneUtils.deepClone(def);
  }
  
  /**
   * Returns description of the field.
   */
  public String getDescription()
  {
    if (description == null)
    {
      if (cachedDefaultDescription == null)
      {
        cachedDefaultDescription = Util.nameToDescription(name);
      }
      
      return cachedDefaultDescription;
    }
    
    return description;
  }
  
  public boolean hasDescription()
  {
    return description != null;
  }
  
  /**
   * Returns help (detailed description) of the field.
   */
  public String getHelp()
  {
    return help;
  }
  
  public boolean isOptional()
  {
    return optional;
  }
  
  /**
   * Returns true if field has selection values.
   */
  public boolean hasSelectionValues()
  {
    return selectionValues != null && selectionValues.size() > 0;
  }
  
  /**
   * Returns true if field has specified selection value.
   */
  public boolean hasSelectionValue(Object value)
  {
    return selectionValues != null && selectionValues.containsKey(value);
  }
  
  /**
   * Returns map containing selection values and their textual descriptions.
   */
  public Map<T, String> getSelectionValues()
  {
    return immutable ? (selectionValues != null ? Collections.unmodifiableMap(selectionValues) : null) : selectionValues;
  }
  
  /**
   * Adds new selection value to the field.
   */
  public FieldFormat<T> addSelectionValue(T value, String description)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    if (StringUtils.isEmpty(description))
    {
      throw new IllegalArgumentException("Empty selection value description");
    }
    
    try
    {
      value = (T) convertValue(value);
    }
    catch (ValidationException ex)
    {
      throw new IllegalArgumentException(ex.getMessage(), ex);
    }
    
    if (selectionValues == null)
    {
      selectionValues = new LinkedHashMap();
    }
    
    selectionValues.put(value, description);
    
    return this;
  }
  
  /**
   * Adds new selection value to the field. Value description is obtained by calling value.toString().
   */
  public FieldFormat<T> addSelectionValue(T value)
  {
    return addSelectionValue(value, value.toString());
  }
  
  /**
   * Returns true if field selection values are extendable, i.e. field may contain values that does not appear in selection values list.
   */
  public boolean isExtendableSelectionValues()
  {
    return extendableSelectionValues;
  }
  
  /**
   * Returns modifiable list of field validators.
   */
  public List<FieldValidator> getValidators()
  {
    return immutable ? Collections.unmodifiableList(validators) : validators;
  }
  
  /**
   * Returns true if field is read-only.
   */
  public boolean isReadonly()
  {
    return readonly;
  }
  
  /**
   * Returns true if field must not be replicated.
   */
  public boolean isNotReplicated()
  {
    return notReplicated;
  }
  
  protected boolean isTransferEncode()
  {
    return transferEncode;
  }
  
  /**
   * Returns true if field if hidden.
   */
  public boolean isHidden()
  {
    return hidden;
  }
  
  /**
   * Returns field editor/renderer ID.
   */
  public String getEditor()
  {
    return editor;
  }
  
  public static Map<Object, String> getTypeSelectionValues()
  {
    return TYPE_SELECTION_VALUES;
  }
  
  public static Character getType(Class valueClass)
  {
    valueClass = getSupertypeWhereRequired(valueClass);
    return CLASS_TO_TYPE.get(valueClass);
  }
  
  public static boolean isFieldClass(Class valueClass)
  {
    valueClass = getSupertypeWhereRequired(valueClass);
    return CLASS_TO_TYPE.containsKey(valueClass);
  }
  
  private static Class getSupertypeWhereRequired(Class valueClass)
  {
    if (DataTable.class.isAssignableFrom(valueClass))
    {
      return DataTable.class;
    }
    return valueClass;
  }
  
  public static Collection getTypes()
  {
    return CLASS_TO_TYPE.values();
  }
  
  public static Map<String, Class<? extends AbstractFieldValidator>> getValidatorToTypeMap()
  {
    return VALIDATOR_TO_TYPE;
  }
  
  /**
   * Returns true if field is a key field.
   */
  public boolean isKeyField()
  {
    return keyField;
  }
  
  /**
   * Returns field editor/renderer options in the encoded form.
   */
  public String getEditorOptions()
  {
    return editorOptions;
  }
  
  public boolean isInlineData()
  {
    return inlineData;
  }
  
  public boolean isAdvanced()
  {
    return advanced;
  }
  
  public FieldFormat<T> setAdvanced(boolean advanced)
  {
    this.advanced = advanced;
    return this;
  }
  
  /**
   * Sets field description.
   */
  public FieldFormat<T> setDescription(String description)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.description = description;
    return this;
  }
  
  /**
   * Sets field help (detailed description).
   */
  public FieldFormat<T> setHelp(String help)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.help = help;
    return this;
  }
  
  /**
   * Sets field selection values.
   */
  public FieldFormat<T> setSelectionValues(Map<? extends T, String> selectionValues)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    if (selectionValues == null || selectionValues.size() == 0)
    {
      this.selectionValues = null;
      return this;
    }
    
    this.selectionValues = (selectionValues instanceof Cloneable) ? (Map<T, String>) selectionValues : new LinkedHashMap(selectionValues);
    
    // If current default value doesn't match to new selection values, we change it to the first selection value from the list
    
    if (!selectionValues.containsKey(getDefaultValue()) && !extendableSelectionValues)
    {
      setDefault(selectionValues.keySet().iterator().next());
    }
    
    return this;
  }
  
  public <E extends Enum<E> & SelectionValue> FieldFormat<T> setSelectionValues(Class<E> templateTypeClass)
  {
    EnumSet<E> enumSet = EnumSet.allOf(templateTypeClass);
    ImmutableMap.Builder<T, String> builder = ImmutableMap.builder();
    for (SelectionValue selectionValue : enumSet)
    {
      builder.put((T) selectionValue.getValue(), selectionValue.getDescription());
    }
    return setSelectionValues(builder.build());
  }
  
  /**
   * Sets extendable selection values flag.
   */
  public FieldFormat<T> setExtendableSelectionValues(boolean extendableSelectionValues)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.extendableSelectionValues = extendableSelectionValues;
    return this;
  }
  
  /**
   * Sets nullable flag.
   */
  public FieldFormat<T> setNullable(boolean nullable)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.nullable = nullable;
    return this;
  }
  
  /**
   * Sets shallow flag.
   */
  public FieldFormat<T> setShallow(boolean shallow)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.shallow = shallow;
    return this;
  }
  
  public FieldFormat<T> setOptional(boolean optional)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.optional = optional;
    return this;
  }
  
  /**
   * Sets read only flag.
   */
  public FieldFormat<T> setReadonly(boolean readonly)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.readonly = readonly;
    return this;
  }
  
  /**
   * Sets not replicated flag.
   */
  public FieldFormat<T> setNotReplicated(boolean notReplicated)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.notReplicated = notReplicated;
    return this;
  }
  
  protected FieldFormat<T> setTransferEncode(boolean transferEncode)
  {
    this.transferEncode = transferEncode;
    return this;
  }
  
  /**
   * Sets hidden flag.
   */
  public FieldFormat<T> setHidden(boolean hidden)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.hidden = hidden;
    return this;
  }
  
  /**
   * Sets field editor/renderer ID.
   */
  public FieldFormat<T> setEditor(String editor)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    if (editor != null && !getSuitableEditors().contains(editor))
    {
      throw new IllegalArgumentException(MessageFormat.format(Cres.get().getString("dtEditorNotSuitable"), toString()) + editor);
    }
    
    this.editor = editor;
    return this;
  }
  
  /**
   * Sets key field flag.
   */
  public FieldFormat<T> setKeyField(boolean keyField)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.keyField = keyField;
    return this;
  }
  
  /**
   * Sets field name.
   */
  public FieldFormat<T> setName(String name)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.name = name;
    return this;
  }
  
  /**
   * Sets field editor/renderer options.
   */
  public FieldFormat<T> setEditorOptions(String editorOptions)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.editorOptions = editorOptions;
    return this;
  }
  
  public FieldFormat<T> setInlineData(boolean inlineData)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.inlineData = inlineData;
    return this;
  }
  
  /**
   * Sets field selection values.
   */
  public void setSelectionValues(String source) throws DataTableException
  {
    createSelectionValues(source, new ClassicEncodingSettings(false));
  }
  
  public FieldFormat<T> setIcon(String icon)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.icon = icon;
    
    return this;
  }
  
  public String getIcon()
  {
    return icon;
  }
  
  public String getGroup()
  {
    return group;
  }
  
  public void removeGroup()
  {
    group = null;
  }
  
  public FieldFormat<T> setGroup(String group)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.group = group;
    
    return this;
  }
  
  public boolean isDefaultOverride()
  {
    return defaultOverride;
  }
  
  public FieldFormat<T> setDefaultOverride(boolean defaultOverride)
  {
    this.defaultOverride = defaultOverride;
    return this;
  }
  
  public boolean isEncrypted()
  {
    return encrypted;
  }
  
  public FieldFormat<T> setEncrypted(boolean encrypted)
  {
    if (immutable)
    {
      throw new IllegalStateException("Immutable");
    }
    
    this.encrypted = encrypted;
    return this;
  }
  
  @Override
  public String toString()
  {
    return description != null ? description : name;
  }
  
  public String toDetailedString()
  {
    return (description != null ? description + " (" + name + ")" : name) + ", " + getTypeName();
  }
  
  public List<String> getSuitableEditors()
  {
    return Arrays.asList(new String[] { EDITOR_LIST });
  }
  
  public TableFormat wrap()
  {
    return new TableFormat(this);
  }
  
  public TableFormat wrapSimple()
  {
    return new TableFormat(this).setMinRecords(1).setMaxRecords(1);
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    Object def = getDefaultValue();
    result = prime * result + ((def == null) ? 0 : def.hashCode());
    result = prime * result + getType();
    result = prime * result + ((description == null) ? 0 : description.hashCode());
    result = prime * result + ((editor == null) ? 0 : editor.hashCode());
    result = prime * result + ((editorOptions == null) ? 0 : editorOptions.hashCode());
    result = prime * result + ((icon == null) ? 0 : icon.hashCode());
    result = prime * result + ((group == null) ? 0 : group.hashCode());
    result = prime * result + (extendableSelectionValues ? 1231 : 1237);
    result = prime * result + ((help == null) ? 0 : help.hashCode());
    result = prime * result + (hidden ? 1231 : 1237);
    result = prime * result + (inlineData ? 1231 : 1237);
    result = prime * result + (encrypted ? 1231 : 1237);
    result = prime * result + (keyField ? 1231 : 1237);
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + (notReplicated ? 1231 : 1237);
    result = prime * result + (nullable ? 1231 : 1237);
    result = prime * result + (optional ? 1231 : 1237);
    result = prime * result + (readonly ? 1231 : 1237);
    result = prime * result + (advanced ? 1231 : 1237);
    result = prime * result + ((selectionValues == null) ? 0 : selectionValues.hashCode());
    result = prime * result + (transferEncode ? 1231 : 1237);
    result = prime * result + ((validators == null) ? 0 : validators.hashCode());
    return result;
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    FieldFormat other = (FieldFormat) obj;
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    if (extendableSelectionValues != other.extendableSelectionValues)
    {
      return false;
    }
    if (hidden != other.hidden)
    {
      return false;
    }
    if (inlineData != other.inlineData)
    {
      return false;
    }
    if (encrypted != other.encrypted)
    {
      return false;
    }
    if (keyField != other.keyField)
    {
      return false;
    }
    if (notReplicated != other.notReplicated)
    {
      return false;
    }
    if (nullable != other.nullable)
    {
      return false;
    }
    if (optional != other.optional)
    {
      return false;
    }
    if (readonly != other.readonly)
    {
      return false;
    }
    if (advanced != other.advanced)
    {
      return false;
    }
    if (description == null)
    {
      if (other.description != null)
      {
        return false;
      }
    }
    else if (!description.equals(other.description))
    {
      return false;
    }
    Object def = getDefaultValue();
    Object odef = other.getDefaultValue();
    if (def == null)
    {
      if (odef != null)
      {
        return false;
      }
    }
    else if (!def.equals(odef))
    {
      return false;
    }
    if (help == null)
    {
      if (other.help != null)
      {
        return false;
      }
    }
    else if (!help.equals(other.help))
    {
      return false;
    }
    if (editor == null)
    {
      if (other.editor != null)
      {
        return false;
      }
    }
    else if (!editor.equals(other.editor))
    {
      return false;
    }
    if (editorOptions == null)
    {
      if (other.editorOptions != null)
      {
        return false;
      }
    }
    else if (!editorOptions.equals(other.editorOptions))
    {
      return false;
    }
    if (icon == null)
    {
      if (other.icon != null)
      {
        return false;
      }
    }
    else if (!icon.equals(other.icon))
    {
      return false;
    }
    if (group == null)
    {
      if (other.group != null)
      {
        return false;
      }
    }
    else if (!group.equals(other.group))
    {
      return false;
    }
    if (selectionValues == null)
    {
      if (other.selectionValues != null)
      {
        return false;
      }
    }
    else if (!selectionValues.equals(other.selectionValues))
    {
      return false;
    }
    if (transferEncode != other.transferEncode)
    {
      return false;
    }
    if (validators == null)
    {
      if (other.validators != null)
      {
        return false;
      }
    }
    else if (!validators.equals(other.validators))
    {
      return false;
    }
    return true;
  }
  
  @Override
  public FieldFormat<T> clone()
  {
    FieldFormat<T> cl;
    try
    {
      cl = (FieldFormat<T>) super.clone();
    }
    catch (CloneNotSupportedException ex)
    {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
    
    cl.defaultValue = (T) CloneUtils.genericClone(getDefaultValue());
    cl.selectionValues = (Map) CloneUtils.deepClone(selectionValues);
    cl.validators = (List) CloneUtils.deepClone(validators);
    
    cl.immutable = false;
    
    return cl;
  }
  
  protected void makeImmutable()
  {
    immutable = true;
  }
}
