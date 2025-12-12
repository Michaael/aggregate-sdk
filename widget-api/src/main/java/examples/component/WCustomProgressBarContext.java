package examples.component;

import java.util.*;

import javax.swing.*;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.widget.*;
import com.tibbo.aggregate.common.widget.component.*;
import com.tibbo.aggregate.common.widget.context.*;

public class WCustomProgressBarContext extends WAbstractContext<WComponentContext, WCustomProgressBar>
{
  public static final String V_VALUE = "value";
  public static final String V_ORIENTATION = "orientation";
  public static final String V_MINIMUM = "minimum";
  public static final String V_MAXIMUM = "maximum";
  
  // Creating variable (value) format (one record, one integer field)
  public static final TableFormat VFT_VALUE = new TableFormat(1, 1, "<" + V_VALUE + "><I>");
  
  // Creating variable (minimum) format (one record, one integer field)
  public static final TableFormat VFT_MINIMUM = new TableFormat(1, 1, "<" + V_MINIMUM + "><I>");
  
  // Creating variable (maximum) format (one record, one integer field)
  public static final TableFormat VFT_MAXIMUM = new TableFormat(1, 1, "<" + V_MAXIMUM + "><I>");
  
  // Creating variable (orientation) format (one record, one field)
  public static final TableFormat VFT_ORIENTATION = new TableFormat(1, 1);
  static
  {
    // Creating orientation variable field format (integer field, default value is 0)
    FieldFormat orientationF = FieldFormat.create("<" + V_ORIENTATION + "><I><A=" + JProgressBar.HORIZONTAL + ">");
    
    // Creating selection values
    Map<Integer, String> selectionValues = new HashMap<Integer, String>();
    selectionValues.put(JProgressBar.HORIZONTAL, "Horizontal");
    selectionValues.put(JProgressBar.VERTICAL, "Vertical");
    
    // Setting selection values for field format
    orientationF.setSelectionValues(selectionValues);
    
    // Adding created field to variable format
    VFT_ORIENTATION.addField(orientationF);
  }
  
  // Creating variable (value) definition
  public static VariableDefinition VALUE_VD = new VariableDefinition(V_VALUE, VFT_VALUE, true, true, "Value", ContextUtils.GROUP_DEFAULT);
  
  // Creating variable (minimum) definition
  public static VariableDefinition MINIMUM_VD = new VariableDefinition(V_MINIMUM, VFT_MINIMUM, true, true, "Minimum", ContextUtils.GROUP_DEFAULT);
  
  // Creating variable (maximum) definition
  public static VariableDefinition MAXIMUM_VD = new VariableDefinition(V_MAXIMUM, VFT_MAXIMUM, true, true, "Maximum", ContextUtils.GROUP_DEFAULT);
  
  // Creating variable (orientation) definition
  public static VariableDefinition ORIENTATION_VD = new VariableDefinition(V_ORIENTATION, VFT_ORIENTATION, true, true, "Orientation", ContextUtils.GROUP_DEFAULT);
  
  public WCustomProgressBarContext(WCustomProgressBar component, WidgetTemplate widget)
  {
    super(component, widget);
  }
  
  @Override
  protected void createVariableDefinitions()
  {
    // Creating variable definitions using superclass (WAbstractContext) method
    super.createVariableDefinitions();
    
    // Removing unnecessary variable definitions that were created by superclass method
    removeVariableDefinition(WAbstractComponent.V_FOREGROUND);
    removeVariableDefinition(WAbstractComponent.V_BACKGROUND);
    removeVariableDefinition(WAbstractComponent.V_OPAQUE);
    removeVariableDefinition(WAbstractComponent.V_FONT);
    
    // Adding default variable definition (value)
    addDefaultVariableDefinition(VALUE_VD);
    
    // Adding variable definition (orientation)
    addVariableDefinition(ORIENTATION_VD);
    
    // Adding variable definition (minimum)
    addVariableDefinition(MINIMUM_VD);
    
    // Adding variable definition (maximum)
    addVariableDefinition(MAXIMUM_VD);
  }
}
