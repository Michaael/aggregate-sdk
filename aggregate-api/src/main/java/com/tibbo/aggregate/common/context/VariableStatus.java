package com.tibbo.aggregate.common.context;

import com.tibbo.aggregate.common.datatable.DataRecord;
import com.tibbo.aggregate.common.datatable.DataTable;

public class VariableStatus
{
  
  public static final String VF_VARIABLE_STATUSES_COMMENT = "comment";
  public static final String VF_VARIABLE_STATUSES_STATUS = "status";
  public static final String VF_VARIABLE_STATUSES_NAME = "name";
  
  private final String status;
  private final String comment;
  
  public VariableStatus(String status, String comment)
  {
    this.status = status;
    this.comment = comment;
  }
  
  public String getComment()
  {
    return comment;
  }
  
  public String getStatus()
  {
    return status;
  }
  
  @Override
  public String toString()
  {
    return "[Status = " + status + ", Comment = " + comment + "]";
  }
  
  public static VariableStatus ofDataTable(DataTable dataTable)
  {
    return ofDataRecord(dataTable.rec());
  }
  
  public static VariableStatus ofDataRecord(DataRecord dataRecord)
  {
    String status = dataRecord.getString(VF_VARIABLE_STATUSES_STATUS);
    String comment = dataRecord.getString(VF_VARIABLE_STATUSES_COMMENT);
    return new VariableStatus(status, comment);
  }
}
