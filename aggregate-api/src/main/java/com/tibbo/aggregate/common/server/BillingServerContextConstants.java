package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.binding.*;
import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.datatable.validator.*;

public class BillingServerContextConstants
{
  public static final String F_UPDATE_BILLING_STATISTICS = "updateBillingStatistics";
    
  public static final String FIF_UPDATE_BILLING_STATISTICS_ACTIVATION_KEY = RootContextConstants.V_LICENSE_ACTIVATION_KEY;
  public static final String FIF_UPDATE_BILLING_STATISTICS_UPDATE_TIMESTAMP = "updateTimestamp";
  public static final String FIF_UPDATE_BILLING_STATISTICS_BILLING_STATISTICS = "billingStatistics";
  public static final TableFormat FIFT_UPDATE_BILLING_STATISTICS = new TableFormat();  
  static
  {
    FIFT_UPDATE_BILLING_STATISTICS.addField(FieldFormat.create(FIF_UPDATE_BILLING_STATISTICS_ACTIVATION_KEY, FieldFormat.STRING_FIELD));
    FIFT_UPDATE_BILLING_STATISTICS.addField(FieldFormat.create(FIF_UPDATE_BILLING_STATISTICS_UPDATE_TIMESTAMP, FieldFormat.DATE_FIELD));
    FIFT_UPDATE_BILLING_STATISTICS.addField(FieldFormat.create(FIF_UPDATE_BILLING_STATISTICS_BILLING_STATISTICS, FieldFormat.DATATABLE_FIELD));
  }
  
  public static final String FOF_UPDATE_BILLING_STATISTICS_IS_SUCCESSFULL = "isSuccessfull";
  public static final TableFormat FOFT_UPDATE_BILLING_STATISTICS = new TableFormat(1, 1);  
  static
  {
    FOFT_UPDATE_BILLING_STATISTICS.addField(FieldFormat.create(FOF_UPDATE_BILLING_STATISTICS_IS_SUCCESSFULL, FieldFormat.BOOLEAN_FIELD));
  }
  
  public static final String V_BILLING_STATISTICS = "billingStatistics";
  
  public static final String VF_BILLING_STATISTICS_ACTIVATION_KEY = RootContextConstants.V_LICENSE_ACTIVATION_KEY;
  public static final String VF_BILLING_STATISTICS_UPDATE_TIMESTAMP = "updateTimestamp";
  public static final String VF_BILLING_STATISTICS_BILLING_STATISTICS = "billingStatistics";
  public static final TableFormat VFT_BILLING_STATISTICS = new TableFormat();
  static
  {
    VFT_BILLING_STATISTICS.addField(FieldFormat.create(VF_BILLING_STATISTICS_ACTIVATION_KEY, FieldFormat.STRING_FIELD));
    VFT_BILLING_STATISTICS.addField(FieldFormat.create(VF_BILLING_STATISTICS_UPDATE_TIMESTAMP, FieldFormat.DATE_FIELD));
    VFT_BILLING_STATISTICS.addField(FieldFormat.create(VF_BILLING_STATISTICS_BILLING_STATISTICS, FieldFormat.DATATABLE_FIELD));  
  }
  
  public static final String E_BILLING_STATISTICS_UPDATE = "billingStatisticsUpdate";
  
  public static final String EF_BILLING_STATISTICS_UPDATE_ACTIVATION_KEY = RootContextConstants.V_LICENSE_ACTIVATION_KEY;
  public static final String EF_BILLING_STATISTICS_UPDATE_UPDATE_TIMESTAMP = "updateTimestamp";
  public static final String EF_BILLING_STATISTICS_UPDATE_BILLING_STATISTICS = "billingStatistics";
  public static final TableFormat EFT_BILLING_STATISTICS_UPDATE = new TableFormat();
  static
  {
    EFT_BILLING_STATISTICS_UPDATE.addField(FieldFormat.create(EF_BILLING_STATISTICS_UPDATE_ACTIVATION_KEY, FieldFormat.STRING_FIELD));
    EFT_BILLING_STATISTICS_UPDATE.addField(FieldFormat.create(EF_BILLING_STATISTICS_UPDATE_UPDATE_TIMESTAMP, FieldFormat.DATE_FIELD));
    EFT_BILLING_STATISTICS_UPDATE.addField(FieldFormat.create(EF_BILLING_STATISTICS_UPDATE_BILLING_STATISTICS, FieldFormat.DATATABLE_FIELD));  
  }
}
