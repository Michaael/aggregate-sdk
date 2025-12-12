package com.tibbo.aggregate.common.datatable;

import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.sql.rowset.serial.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.dao.*;
import com.tibbo.aggregate.common.data.*;
import com.tibbo.aggregate.common.datatable.encoding.*;
import com.tibbo.aggregate.common.query.*;
import com.tibbo.aggregate.common.util.*;

public class ResultSetDataTable extends AbstractBigDataTable
{
  private static final String ERROR_WITH_RESULT_SET = "An error occurred while working with result set: ";
  
  private final ResultSet resultSet;
  private final List<FieldDescriptor> fields;
  
  private boolean scrollable;
  
  private boolean ownDeletesAreVisible;
  private boolean ownInsertsAreVisible;
  private boolean ownUpdatesAreVisible;
  
  private boolean alreadyWarnedAboutDeletes;
  private boolean alreadyWarnedAboutInserts;
  private boolean alreadyWarnedAboutUpdates;
  
  public ResultSetDataTable(ResultSet resultSet)
  {
    if (resultSet == null)
      throw new IllegalArgumentException("resultSet must not be null");
    
    this.resultSet = resultSet;
    
    try
    {
      fields = DataTableWrappingUtils.extractResultSetFields(resultSet.getMetaData(), false);
      setFormat(DataTableSqlHelper.createTableFormat(fields, null));
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException("An error occurred while trying to create table format from result set: ", exc);
    }
    
    setImmutableStatus();
    setScrollableStatus();
    
    try
    {
      DatabaseMetaData dbMetaData = null;
      if (resultSet.getStatement() != null && resultSet.getStatement().getConnection() != null)
        dbMetaData = resultSet.getStatement().getConnection().getMetaData();
      
      setOwnChangesAreVisibleStatuses(dbMetaData);
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.warn("Could not get the database metadata: " + exc.getMessage());
    }
  }
  
  private void setImmutableStatus()
  {
    immutable = true;
    try
    {
      if (resultSet.getConcurrency() == ResultSet.CONCUR_UPDATABLE)
        immutable = false;
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.error("Could not get the concurrency of result set; immutable is set to true ", exc);
    }
  }
  
  private void setScrollableStatus()
  {
    scrollable = false;
    try
    {
      if (resultSet.getType() == ResultSet.TYPE_SCROLL_INSENSITIVE || resultSet.getType() == ResultSet.TYPE_SCROLL_SENSITIVE)
        scrollable = true;
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.error("Could not get the type of result set; scrollable is set to false ", exc);
    }
  }
  
  private void setOwnChangesAreVisibleStatuses(DatabaseMetaData dbMetaData)
  {
    if (dbMetaData == null)
      return;
    
    setOwnDeletesAreVisibleStatus(dbMetaData);
    setOwnInsertsAreVisibleStatus(dbMetaData);
    setOwnUpdatesAreVisibleStatus(dbMetaData);
  }
  
  private void setOwnDeletesAreVisibleStatus(DatabaseMetaData dbMetaData)
  {
    ownDeletesAreVisible = false;
    try
    {
      ownDeletesAreVisible = dbMetaData.ownDeletesAreVisible(resultSet.getType());
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.error("Could not get the type of result set; ownDeletesAreVisible is set to false ", exc);
    }
  }
  
  private void setOwnInsertsAreVisibleStatus(DatabaseMetaData dbMetaData)
  {
    ownInsertsAreVisible = false;
    try
    {
      ownInsertsAreVisible = dbMetaData.ownInsertsAreVisible(resultSet.getType());
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.error("Could not get the type of result set; ownInsertsAreVisible is set to false ", exc);
    }
  }
  
  private void setOwnUpdatesAreVisibleStatus(DatabaseMetaData dbMetaData)
  {
    ownUpdatesAreVisible = false;
    try
    {
      ownUpdatesAreVisible = dbMetaData.ownUpdatesAreVisible(resultSet.getType());
    }
    catch (SQLException exc)
    {
      Log.DATATABLE.error("Could not get the type of result set; ownUpdatesAreVisible is set to false ", exc);
    }
  }
  
  /**
   * Adds new record to the table.
   */
  @Override
  public DataTable addRecord(DataRecord record)
  {
    ensureMutable();
    ensureScrollable();
    checkIfOwnInsertsAreVisible();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      resultSet.moveToInsertRow();
      
      update(record);
      
      resultSet.insertRow();
      commitIfNeeded();
      
      incrementRecordCount();
      
      resultSet.absolute(initialPosition);
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
    
    return this;
  }
  
  @Override
  public void splitFormat()
  {
  }
  
  /**
   * Adds new record to the table.
   */
  @Override
  public DataRecord addRecord(Object... fieldValues)
  {
    final DataRecord record = new DataRecord(format, fieldValues);
    addRecord(record);
    return record;
  }
  
  @Override
  public DataTable addRecord(int index, DataRecord record)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public DataRecord addRecord()
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  private void update(DataRecord record) throws SQLException
  {
    try
    {
      validateRecord(record);
    }
    catch (ValidationException exc)
    {
      throw new IllegalStateException(exc.getMessage(), exc);
    }
    
    for (int fieldIndex = 0; fieldIndex < format.getFieldCount(); fieldIndex++)
    {
      final FieldFormat ff = record.getFormat().getField(fieldIndex);
      
      assertHasField(ff);
      
      final String columnName = fields.get(fieldIndex).getColumnName();
      
      final Object value = record.getValue(ff.getName());
      
      if (value == null)
      {
        resultSet.updateNull(columnName);
        return;
      }
      
      updateDependingOnType(ff.getType(), columnName, value);
    }
  }
  
  private void updateDependingOnType(char fieldType, String columnName, Object value) throws SQLException
  {
    switch (fieldType)
    {
      case FieldFormat.DATE_FIELD:
        java.util.Date date = (Date) value;
        resultSet.updateTimestamp(columnName, new java.sql.Timestamp(date.getTime())); // java.sql.Timestamp casts away milliseconds
        break;
      
      case FieldFormat.DATA_FIELD:
        final byte[] data = ((Data) value).getData();
        if (data != null)
          resultSet.updateBlob(columnName, new SerialBlob(data));
        else
          Log.DATATABLE.warn("The BLOB at field " + columnName + " was not updated because the data object is null");
        break;
      
      case FieldFormat.DATATABLE_FIELD:
        DataTable dataTable = (DataTable) value;
        resultSet.updateObject(columnName, dataTable.encode(false));
        break;
      
      default:
        resultSet.updateObject(columnName, value);
        break;
    }
  }
  
  private void assertHasField(FieldFormat ff)
  {
    final String fieldName = ff.getName();
    
    if (!format.hasField(fieldName))
      throw new IllegalArgumentException("There is no such field in the data table: " + fieldName);
    
    if (!format.getField(fieldName).equals(ff))
      throw new IllegalArgumentException("The field format of field " + fieldName + " of the record being added does not match the field format of the field with the same name in the data table");
  }
  
  @Override
  protected DataRecord removeRecordImpl(int index)
  {
    ensureMutable();
    ensureScrollable();
    checkIfOwnDeletesAreVisible();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      resultSet.absolute(index + 1); // Rows are counted from 1
      
      final DataRecord record = getCurrentRecord();
      
      resultSet.deleteRow();
      commitIfNeeded();
      
      decrementRecordCount();
      
      resultSet.absolute(initialPosition);
      
      return record;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  private void ensureScrollable()
  {
    if (!scrollable)
    {
      throw new IllegalStateException("The resultSet that this data table is based on is not scrollable; hence, only one iteration is permitted and the cursor cannot be set to an arbitrary index");
    }
  }
  
  private void checkIfOwnDeletesAreVisible()
  {
    if (!alreadyWarnedAboutDeletes && !ownDeletesAreVisible)
    {
      Log.DATATABLE.warn(
          "The database meta data indicate that result set own deletes are not visible. " +
              "Depending on the database engine / JDBC driver used it may mean that this data table will not reflect changes caused by the remove method.");
      alreadyWarnedAboutDeletes = true;
    }
  }
  
  private void checkIfOwnInsertsAreVisible()
  {
    if (!alreadyWarnedAboutInserts && !ownInsertsAreVisible)
    {
      Log.DATATABLE.warn(
          "The database meta data indicate that result set own inserts are not visible. " +
              "Depending on the database engine / JDBC driver used it may mean that this data table will not reflect changes caused by the setRecord method.");
      alreadyWarnedAboutInserts = true;
    }
  }
  
  private void checkIfOwnUpdatesAreVisible()
  {
    if (!alreadyWarnedAboutUpdates && !ownUpdatesAreVisible)
    {
      Log.DATATABLE.warn(
          "The database meta data indicate that result set own updates are not visible. " +
              "Depending on the database engine / JDBC driver used it may mean that this data table will not reflect changes caused by the addRecord methods.");
      alreadyWarnedAboutUpdates = true;
    }
  }
  
  private void commitIfNeeded() throws SQLException
  {
    if (resultSet.getStatement() == null || resultSet.getStatement().getConnection() == null)
      return;
    
    if (!resultSet.getStatement().getConnection().getAutoCommit())
    {
      resultSet.getStatement().getConnection().commit();
    }
  }
  
  @Override
  public void validate(Context context, ContextManager contextManager, CallerController caller) throws DataTableException
  {
    ensureScrollable();
    
    super.validate(context, contextManager, caller);
  }
  
  /**
   * Replaces record at the specified index.
   */
  @Override
  public DataTable setRecord(int index, DataRecord record)
  {
    ensureMutable();
    ensureScrollable();
    checkIfOwnUpdatesAreVisible();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      resultSet.absolute(index + 1); // Rows are counted from 1
      
      record.setTable(this);
      update(record);
      
      resultSet.updateRow();
      commitIfNeeded();
      
      resultSet.absolute(initialPosition);
      
      return this;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  /**
   * Returns record with specified index.
   */
  @Override
  public DataRecord getRecord(int number)
  {
    ensureScrollable();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      resultSet.absolute(number + 1); // Rows are counted from 1
      
      final DataRecord result = getCurrentRecord();
      
      resultSet.absolute(initialPosition);
      
      return result;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  /**
   * Removes all records equal to the rec parameter from the table.
   */
  @Override
  public void removeRecords(DataRecord rec)
  {
    ensureMutable();
    ensureScrollable();
    checkIfOwnDeletesAreVisible();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      resultSet.beforeFirst();
      
      while (resultSet.next())
      {
        final DataRecord currentRecord = getCurrentRecord();
        if (currentRecord.equals(rec))
        {
          resultSet.deleteRow();
          
          decrementRecordCount();
        }
      }
      commitIfNeeded();
      
      resultSet.absolute(initialPosition);
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  @Override
  public void reorderRecord(DataRecord record, int index)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    
    if (!(obj instanceof ResultSetDataTable))
    {
      return false;
    }
    
    ResultSetDataTable other = (ResultSetDataTable) obj;
    
    if (!resultSet.equals(other.resultSet)) // for JDBC result sets, this actually means referential equality
    {
      return false;
    }
    
    if (!Util.equals(quality, other.quality))
    {
      return false;
    }
    
    return true;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((resultSet == null) ? 0 : resultSet.hashCode());
    result = prime * result + ((quality == null) ? 0 : quality.hashCode());
    return result;
  }
  
  @Override
  void getEncodedRecordsOrTableID(StringBuilder finalSB, ClassicEncodingSettings settings, Boolean isTransferEncode, Integer encodeLevel)
  {
    if (id != null)
      new Element(ELEMENT_ID, String.valueOf(id)).encode(finalSB, settings, isTransferEncode, encodeLevel);
  }
  
  @Override
  public String toDefaultString()
  {
    return "Result set data table with format: " + format.toString();
  }
  
  @Override
  public String dataAsString(boolean showFieldNames, boolean showHiddenFields, boolean showPasswords)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  /**
   * Returns true if table has exactly one record and one field.
   */
  @Override
  public boolean isOneCellTable()
  {
    ensureScrollable();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      final boolean result = getFieldCount() == 1 && resultSet.absolute(1) && !resultSet.absolute(2);
      
      resultSet.absolute(initialPosition);
      
      return result;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException("An error occurred while attempting to set the cursor of result set", exc);
    }
  }
  
  @Override
  public List<DataRecord> selectAll(DataTableQuery query)
  {
    ensureScrollable();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      final List<DataRecord> result = super.selectAll(query);
      
      resultSet.absolute(initialPosition);
      
      return result;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  @Override
  public Integer findIndex(DataTableQuery query)
  {
    ensureScrollable();
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      int index = 0;
      for (DataRecord record : this)
      {
        boolean meet = true;
        
        for (QueryCondition cond : query.getConditions())
        {
          if (!record.meetToCondition(cond))
            meet = false;
        }
        if (meet)
        {
          resultSet.absolute(initialPosition);
          return index;
        }
        index++;
      }
      resultSet.absolute(initialPosition);
      
      return null;
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
  }
  
  @Override
  public void sort(DataTableSorter sorter)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public void sort(Comparator<DataRecord> comparator)
  {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }
  
  @Override
  public Iterator<DataRecord> iterator()
  {
    return new Iter();
  }
  
  @Override
  public Iterator<DataRecord> iterator(int index)
  {
    return new Iter(index);
  }
  
  private DataRecord getCurrentRecord() throws SQLException
  {
    final DataRecord record = new DataRecord(format);
    DataTableSqlHelper.populateRecord(resultSet, fields, format, record);
    return record;
  }
  
  /**
   * Creates a <code>SimpleDataTable</code> that contains same records (in the sense of the object equality but not necessarily the referential equality) as represented by this data table at the
   * moment of cloning. Does NOT call <code>super.clone()</code>
   */
  @Override
  public DataTable clone()
  {
    final SimpleDataTable clone;
    
    try
    {
      final int initialPosition = resultSet.getRow();
      
      if (!resultSet.isBeforeFirst())
      {
        if (scrollable)
          resultSet.beforeFirst();
        else
          throw new IllegalStateException(
              "The cursor of the result set is in the proper position and the result set is not scrollable which makes it impossible to reset its cursor position");
      }
      
      clone = toSimpleDataTable();
      
      resultSet.absolute(initialPosition);
    }
    catch (SQLException exc)
    {
      throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
    }
    
    clone.namingEvaluator = null;
    
    clone.immutable = false;
    
    if (Log.DATATABLE.isDebugEnabled())
      Log.DATATABLE.debug("The clone method was called; a SimpleDataTable copy of this ResultSetDataTable was created: " + clone);
    
    return clone;
  }
  
  private class Iter implements Iterator<DataRecord>
  {
    private DataRecord record;
    
    private int localCount;
    
    Iter()
    {
      try
      {
        if (scrollable && !resultSet.isBeforeFirst())
          resultSet.beforeFirst();
      }
      catch (SQLException exc)
      {
        throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
      }
      
      localCount = 0;
    }
    
    Iter(int index)
    {
      ensureScrollable();
      
      try
      {
        resultSet.absolute(index);
      }
      catch (SQLException exc)
      {
        throw new AggreGateRuntimeException("An error occurred while attempting to set the cursor of result set");
      }
      
      localCount = index;
    }
    
    @Override
    public boolean hasNext()
    {
      try
      {
        if (!resultSet.isLast())
        {
          return true;
        }
        else
        {
          setRecordCount(localCount);
          return false;
        }
      }
      catch (SQLException exc)
      {
        throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
      }
    }
    
    @Override
    public DataRecord next()
    {
      try
      {
        resultSet.next();
        
        record = getCurrentRecord();
        
        localCount++;
      }
      catch (SQLException exc)
      {
        throw new AggreGateRuntimeException(ERROR_WITH_RESULT_SET, exc);
      }
      
      return record;
    }
    
    @Override
    public void remove()
    {
      try
      {
        ensureMutable();
        checkIfOwnDeletesAreVisible();
        
        resultSet.deleteRow();
        
        localCount--;
      }
      catch (SQLException exc)
      {
        throw new AggreGateRuntimeException("An error occurred when trying to delete a row from result set: ", exc);
      }
      
      if (record != null)
      {
        record.setTable(null);
      }
    }
  }
  
  private ResultSet getResultSet()
  {
    return resultSet;
  }
  
  @Override
  public void close()
  {
    if (getResultSet() != null)
    {
      ResultSet resultSet = getResultSet();
      try
      {
        Statement stmt = resultSet.getStatement();
        Connection connection = (stmt != null && !stmt.isClosed()) ? stmt.getConnection() : null;
        if (!resultSet.isClosed())
        {
          resultSet.getStatement().close();
          resultSet.close();
          if (connection != null)
            connection.close();
        }
      }
      catch (SQLException e)
      {
        Log.DATATABLE.warn(e.getMessage(), e);
        throw new AggreGateRuntimeException("An error occurred while attempting to close the result set");
      }
    }
  }
}
