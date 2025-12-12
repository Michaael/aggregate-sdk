package com.tibbo.aggregate.common.datatable;

import java.text.*;
import java.util.*;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.util.*;

public class DataTableReplication
{
  
  public static Set<String> copy(DataTable source, DataTable target)
  {
    return copy(source, target, false, false, true, true, false, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields)
  {
    return copy(source, target, copyReadOnlyFields, false, true, true, false, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields)
  {
    return copy(source, target, copyReadOnlyFields, copyNonReplicatableFields, true, true, false, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget)
  {
    return copy(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, true, false, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget, boolean addRecordsToTarget)
  {
    return copy(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, false, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget, boolean addRecordsToTarget,
      boolean ignoreUnresizable)
  {
    return copy(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, ignoreUnresizable, null);
  }
  
  public static Set<String> copy(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget, boolean addRecordsToTarget,
      boolean ignoreUnresizable, Collection<String> fields)
  {
    if (target.getFormat().getKeyFields().size() == 0)
    {
      return copyWithoutKeyFields(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, ignoreUnresizable, fields);
    }
    else
    {
      return copyWithKeyFields(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, ignoreUnresizable, fields);
    }
  }
  
  public static Set<String> copyWithKeyFields(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget,
      boolean addRecordsToTarget, boolean ignoreUnresizable, Collection<String> fields)
  {
    Set<String> errors = new LinkedHashSet();
    
    List<String> keyFields = target.getFormat().getKeyFields();
    
    for (String fieldName : keyFields)
    {
      if (!source.getFormat().hasField(fieldName))
      {
        return copyWithoutKeyFields(source, target, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, ignoreUnresizable, fields);
      }
    }
    
    String singleKeyField = null;
    Map<Object, DataRecord> sourceLookup = null;
    Map<Object, DataRecord> targetLookup = null;
    
    if (keyFields.size() == 1)
    {
      singleKeyField = keyFields.get(0);
      
      sourceLookup = new HashMap();
      
      for (DataRecord cur : source)
      {
        sourceLookup.put(cur.getValue(singleKeyField), cur);
      }
      
      targetLookup = new HashMap();
      
      for (DataRecord cur : target)
      {
        targetLookup.put(cur.getValue(singleKeyField), cur);
        
      }
      
    }
    
    for (Iterator iter = target.iterator(); iter.hasNext();)
    {
      DataRecord targetRec = (DataRecord) iter.next();
      
      DataRecord sourceRec;
      
      if (singleKeyField != null)
      {
        sourceRec = sourceLookup.get(targetRec.getValue(singleKeyField));
      }
      else
      {
        DataTableQuery query = new DataTableQuery();
        
        for (String keyField : keyFields)
        {
          query.addCondition(new QueryCondition(keyField, targetRec.getValue(keyField)));
        }
        
        sourceRec = source.select(query);
      }
      
      if (removeRecordsFromTarget && sourceRec == null && (ignoreUnresizable || !target.getFormat().isUnresizable()))
      {
        if (target.getRecordCount() > target.getFormat().getMinRecords())
        {
          iter.remove();
        }
        else
        {
          if (source.getFormat().getMinRecords() != source.getFormat().getMaxRecords())
          {
            errors.add(Cres.get().getString("dtTargetTableMinRecordsReached"));
          }
          break;
        }
      }
    }
    
    for (DataRecord sourceRec : source)
    {
      
      DataRecord targetRec;
      
      if (singleKeyField != null)
      {
        
        targetRec = targetLookup.get(sourceRec.getValue(singleKeyField));
        
      }
      else
      {
        
        DataTableQuery query = new DataTableQuery();
        
        for (String keyField : keyFields)
        {
          query.addCondition(new QueryCondition(keyField, sourceRec.getValue(keyField)));
        }
        
        targetRec = target.select(query);
      }
      
      if (targetRec == null)
      {
        if (addRecordsToTarget && (ignoreUnresizable || !target.getFormat().isUnresizable()))
        {
          if (target.getRecordCount() < target.getFormat().getMaxRecords())
          {
            DataRecord newRec = new DataRecord(target.getFormat()); // We are not using target.addRecord() to avoid key field validation errors
            
            errors.addAll(DataTableReplication.copyRecord(sourceRec, newRec, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, fields));
            
            try
            {
              target.addRecord(newRec);
            }
            catch (Exception ex)
            {
              errors.add(Cres.get().getString("dtCannotAddRecord") + (ex.getMessage() != null ? ex.getMessage() : ex.toString()));
            }
          }
          else
          {
            if (source.getFormat().getMinRecords() != source.getFormat().getMaxRecords())
            {
              errors.add(Cres.get().getString("dtTargetTableMaxRecordsReached"));
            }
          }
        }
      }
      else
      {
        errors.addAll(DataTableReplication.copyRecord(sourceRec, targetRec, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, fields));
      }
    }
    
    copyTimestampAndQuality(source, target);
    
    return errors;
  }
  
  public static Set<String> copyWithoutKeyFields(DataTable source, DataTable target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget,
      boolean addRecordsToTarget, boolean ignoreUnresizable, Collection<String> fields)
  {
    Set<String> errors = new LinkedHashSet();
    
    if (removeRecordsFromTarget && (ignoreUnresizable || !target.getFormat().isUnresizable()))
    {
      while (target.getRecordCount() > source.getRecordCount())
      {
        if (target.getRecordCount() > target.getFormat().getMinRecords())
        {
          target.removeRecord(target.getRecordCount() - 1);
        }
        else
        {
          if (source.getFormat().getMinRecords() != source.getFormat().getMaxRecords())
          {
            errors.add(Cres.get().getString("dtTargetTableMinRecordsReached"));
          }
          break;
        }
      }
    }
    
    for (int i = 0; i < Math.min(source.getRecordCount(), target.getRecordCount()); i++)
    {
      DataRecord srcRec = source.getRecord(i);
      DataRecord tgtRec = target.getRecord(i);
      
      errors.addAll(copyRecord(srcRec, tgtRec, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, fields));
    }
    
    if (addRecordsToTarget && (ignoreUnresizable || !target.getFormat().isUnresizable()))
    {
      if (source.getRecordCount() > target.getRecordCount())
      {
        for (int i = target.getRecordCount(); i < Math.min(target.getFormat().getMaxRecords(), source.getRecordCount()); i++)
        {
          errors.addAll(copyRecord(source.getRecord(i), target.addRecord(), copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget, fields));
        }
      }
    }
    
    if (source.getRecordCount() > target.getFormat().getMaxRecords())
    {
      if (source.getFormat().getMinRecords() != source.getFormat().getMaxRecords())
      {
        errors.add(Cres.get().getString("dtTargetTableMaxRecordsReached"));
      }
    }
    
    copyTimestampAndQuality(source, target);
    
    return errors;
  }
  
  public static void copyTimestampAndQuality(DataTable source, DataTable target)
  {
    if (source.getTimestamp() != null)
      target.setTimestamp(source.getTimestamp());
    
    if (source.getQuality() != null)
      target.setQuality(source.getQuality());
  }
  
  public static Set<String> copyRecord(DataRecord source, DataRecord target)
  {
    return copyRecord(source, target, false, false, true, true, null);
  }
  
  public static Set<String> copyRecord(DataRecord source, DataRecord target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields)
  {
    return copyRecord(source, target, copyReadOnlyFields, copyNonReplicatableFields, true, true, null);
  }
  
  public static Set<String> copyRecord(DataRecord source, DataRecord target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, Collection<String> fields)
  {
    return copyRecord(source, target, copyReadOnlyFields, copyNonReplicatableFields, true, true, fields);
  }
  
  public static Set<String> copyRecord(DataRecord source, DataRecord target, boolean copyReadOnlyFields, boolean copyNonReplicatableFields, boolean removeRecordsFromTarget,
      boolean addRecordsToTarget, Collection<String> fields)
  {
    Set<String> errors = new LinkedHashSet();
    
    for (FieldFormat tgtFf : target.getFormat())
    {
      String fieldName = tgtFf.getName();
      
      FieldFormat srcFf = null;
      
      srcFf = source.getFormat().getField(fieldName);
      
      if (fields != null && !fields.contains(tgtFf.getName()))
      {
        continue;
      }
      
      if (srcFf == null)
      {
        continue;
      }
      
      if (tgtFf.isReadonly() && !copyReadOnlyFields)
      {
        continue;
      }
      
      if (!copyNonReplicatableFields)
      {
        if (tgtFf.isNotReplicated() || srcFf.isNotReplicated())
        {
          continue;
        }
      }
      
      try
      {
        if (srcFf.getType() == FieldFormat.DATATABLE_FIELD && tgtFf.getType() == FieldFormat.DATATABLE_FIELD)
        {
          final DataTable sourceTable = source.getDataTable(fieldName);
          final DataTable targetTable = target.getDataTable(fieldName);
          if (sourceTable != null && targetTable != null)
          {
            if (Util.equals(targetTable.getFormat(), AbstractDataTable.DEFAULT_FORMAT))
            {
              target.setValue(fieldName, sourceTable.clone());
            }
            else
            {
              errors.addAll(DataTableReplication.copy(sourceTable, targetTable, copyReadOnlyFields, copyNonReplicatableFields, removeRecordsFromTarget, addRecordsToTarget));
              target.setValue(fieldName, targetTable);
            }
            continue;
          }
        }
        
        if (srcFf.getFieldWrappedClass().equals(tgtFf.getFieldWrappedClass()))
        {
          target.setValue(fieldName, CloneUtils.genericClone(source.getValue(fieldName)));
        }
        else
        {
          target.setValue(fieldName, tgtFf.valueFromString(srcFf.valueToString(source.getValue(fieldName))));
        }
      }
      catch (Exception ex2)
      {
        String msg = MessageFormat.format(Cres.get().getString("dtErrCopyingField"), fieldName);
        Log.DATATABLE.debug(msg, ex2);
        errors.add(msg + ": " + ex2.getMessage());
        continue;
      }
    }
    
    return errors;
  }
}
