package com.tibbo.aggregate.common.datatable;

import java.util.*;
import java.util.stream.Stream;

import com.tibbo.aggregate.common.context.CallerController;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.datatable.encoding.ClassicEncodingSettings;
import com.tibbo.aggregate.common.datatable.encoding.FormatCache;
import com.tibbo.aggregate.common.expression.AttributedObject;
import com.tibbo.aggregate.common.util.StringEncodable;

public interface DataTable extends Iterable<DataRecord>, AttributedObject, StringEncodable
{
  
  /**
   * Returns number of records in the table or null if the number of records is unknown.
   */
  Integer getRecordCount();
  
  /**
   * Returns number of fields in the table.
   */
  int getFieldCount();
  
  /**
   * Returns format of the table.
   */
  TableFormat getFormat();
  
  /**
   * Sets new format for the table.
   *
   * Note, that resulting table is not checked for validity. Format of existing records may be incompatible with new format of table.
   */
  DataTable setFormat(TableFormat format);
  
  /**
   * Returns format of field with specified index.
   */
  FieldFormat getFormat(int field);
  
  /**
   * Returns format of field with specified name.
   */
  FieldFormat getFormat(String name);
  
  boolean applyCachedFormat(Optional<FormatCache> formatCache);
  
  /**
   * Returns table ID.
   */
  Long getId();
  
  /**
   * Sets table ID.
   */
  void setId(Long id);
  
  boolean hasField(String field);
  
  /**
   * Adds new records to the table.
   */
  DataTable addRecords(List<DataRecord> records);
  
  /**
   * Adds new record to the table.
   */
  DataTable addRecord(DataRecord record);
  
  /**
   * Adds new record to the table.
   */
  DataRecord addRecord(Object... fieldValues);
  
  /**
   * Adds new record to the table at the specified index.
   */
  DataTable addRecord(int index, DataRecord record);
  
  /**
   * Adds new record to the table.
   */
  DataRecord addRecord();
  
  /**
   * Executes table validators to make sure table data is valid. Throws an error if validation fails.
   */
  void validate(Context context, ContextManager contextManager, CallerController caller) throws DataTableException;
  
  /**
   * Executes validators of a specific record to make sure its data is valid. Throws an error if validation fails.
   */
  void validateRecord(DataRecord record) throws ValidationException;
  
  /**
   * Replaces record at the specified index.
   */
  DataTable setRecord(int index, DataRecord record);
  
  /**
   * Swaps two records.
   *
   * Both records must belong to this table, otherwise method will throw an <code>IllegalStateException</code>
   */
  void swapRecords(int index1, int index2);
  
  /**
   * Returns list of table records.
   */
  @Deprecated
  List<DataRecord> getRecords();
  
  boolean isInvalid();
  
  String getInvalidationMessage();
  
  void setInvalidationMessage(String invalidationMessage);
  
  @Override
  Date getTimestamp();
  
  @Override
  void setTimestamp(Date timestamp);
  
  @Override
  Integer getQuality();
  
  @Override
  void setQuality(Integer quality);
  
  @Override
  Object getValue();
  
  /**
   * Returns record with specified index.
   */
  DataRecord getRecord(int number);
  
  DataRecord getRecordById(String id);
  
  /**
   * Removes record with specified index from the table.
   */
  DataRecord removeRecord(int index);
  
  /**
   * Removes all records from the table which contain in ids.
   */
  void removeRecordsByIds(Collection<String> ids);
  
  /**
   * Removes all records equal to the rec parameter from the table.
   */
  void removeRecords(DataRecord rec);
  
  /**
   * Moves specified record to position specified by <code>index</code> argument.
   *
   * <code>record<code> must belong to this table, otherwise method will throw an <code>IllegalStateException</code>
   */
  void reorderRecord(DataRecord record, int index);
  
  @Override
  boolean equals(Object obj);
  
  @Override
  int hashCode();
  
  String getEncodedData(ClassicEncodingSettings settings);
  
  StringBuilder getEncodedData(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel);
  
  String encode();
  
  String encode(boolean useVisibleSeparators);
  
  String encode(ClassicEncodingSettings settings);
  
  @Override
  StringBuilder encode(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel);
  
  @Override
  String toString();
  
  /**
   * Returns human-readable description of the table.
   */
  String getDescription();
  
  String toDefaultString();
  
  void fixRecords();
  
  String dataAsString();
  
  String dataAsString(boolean showFieldNames, boolean showHiddenFields);
  
  String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords);
  
  /**
   * Returns true if table has exactly one record and one field.
   */
  boolean isOneCellTable();
  
  boolean conform(TableFormat rf);
  
  String conformMessage(TableFormat rf);
  
  List<DataRecord> selectAll(DataTableQuery query);
  
  DataRecord select(DataTableQuery query);
  
  DataRecord select(String field, Object value);
  
  Integer findIndex(DataTableQuery query);
  
  Integer findIndex(String field, Object value);
  
  /**
   * Returns the index of the first occurrence of <code>record</code> in this Data Table, or null if this Data Table does not contain the specified record.
   */
  Integer findIndex(DataRecord record);
  
  void sort(String field, boolean ascending);
  
  void sort(DataTableSorter sorter);
  
  void sort(Comparator<DataRecord> comparator);
  
  /**
   * Returns first record of the table.
   */
  DataRecord rec();
  
  /**
   * Returns value of first field in first record of the table.
   */
  Object get();
  
  // This method should be called only by Data Table Editors! It creates a mutable copy of format in every record.
  void splitFormat();
  
  void joinFormats();
  
  @Override
  Iterator<DataRecord> iterator();
  
  Iterator<DataRecord> iterator(int index);
  
  DataTable clone();
  
  int compareTo(DataTable other);
  
  void append(DataTable src);
  
  DataTable makeImmutable();
  
  DataTable cloneIfImmutable();
  
  boolean isImmutable();
  
  boolean isSimple();
  
  void close();
  
  Stream<DataRecord> stream();
}