package com.tibbo.aggregate.common.widget;

import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.swing.*;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.datatable.TableFormat;

public class WidgetConstants
{
  public static final String COMPONENT_WIDGET = "widget";
  public static final String COMPONENT_EMPTY = "empty";
  public static final String COMPONENT_LABEL = "label";
  public static final String COMPONENT_HTML_SNIPPET = "htmlSnippet";
  public static final String COMPONENT_DROP_DOWN_BUTTON = "dropDownButton";
  public static final String COMPONENT_TIMER = "timer";
  public static final String COMPONENT_NUMBER_FIELD = "numberField";
  public static final String COMPONENT_BREADCRUMB = "breadcrumb";
  public static final String COMPONENT_TREE_SELECT = "treeSelect";
  public static final String COMPONENT_TEXTFIELD = "textField";
  public static final String COMPONENT_BUTTON = "button";
  public static final String COMPONENT_CHECKBOX = "checkBox";
  public static final String COMPONENT_CHECKBOX_GROUP = "checkBoxGroup";
  public static final String COMPONENT_DROPDOWN_LIST = "dropdownList";
  public static final String COMPONENT_COMBOBOX = "comboBox";
  public static final String COMPONENT_DATE_TIME_PICKER = "dateTimePicker";
  public static final String COMPONENT_DEVICE = "device";
  public static final String COMPONENT_DATA_TABLE_EDITOR = "dataTableEditor";
  public static final String COMPONENT_CLASS_DATA_TABLE_EDITOR = "classDataTableEditor";
  public static final String COMPONENT_CLASS_FIELD_LIST = "classFieldList";
  public static final String COMPONENT_CONTEXT_LIST = "contextList";
  public static final String COMPONENT_DATA_TABLE = "dataTable";
  public static final String COMPONENT_DRAWER_PANEL = "drawerPanel";
  public static final String COMPONENT_EVENT_LOG = "eventLog";
  public static final String COMPONENT_CUSTOM_TREE = "customTree";
  public static final String COMPONENT_SYSTEM_TREE = "systemTree";
  public static final String COMPONENT_SLIDER = "slider";
  public static final String COMPONENT_RANGE_SLIDER = "rangeSlider";
  public static final String COMPONENT_SPINNER = "spinner";
  public static final String COMPONENT_TEXTAREA = "textArea";
  public static final String COMPONENT_HTMLAREA = "htmlArea";
  public static final String COMPONENT_IMAGE = "image";
  public static final String COMPONENT_VECTOR_DRAWING = "vectorDrawing";
  public static final String COMPONENT_PASSWORD_FIELD = "passwordField";
  public static final String COMPONENT_FORMATTED_TEXT_FIELD = "formattedTextField";
  public static final String COMPONENT_PROGRESS_BAR = "progressBar";
  public static final String COMPONENT_TOGGLE_BUTTON = "toggleButton";
  public static final String COMPONENT_LIST = "list";
  public static final String COMPONENT_RADIO_BUTTON = "radioButton";
  public static final String COMPONENT_SIMPLE_GAUGE = "gauge";
  public static final String COMPONENT_SIMPLE_ARC_GAUGE = "arcGauge";
  public static final String COMPONENT_RADIAL_GAUGE = "radialGauge";
  public static final String COMPONENT_RADIAL_BARGRAPH_GAUGE = "radialBargraphGauge";
  public static final String COMPONENT_ARC_GAUGE = "arcGauge2";
  public static final String COMPONENT_HALF_ROUND_GAUGE = "halfRoundGauge";
  public static final String COMPONENT_QUARTER_ROUND_GAUGE = "quarterRoundGauge";
  public static final String COMPONENT_LINEAR_GAUGE = "linearGauge";
  public static final String COMPONENT_LINEAR_BARGRAPH_GAUGE = "linearBargraphGauge";
  public static final String COMPONENT_COUNTER_GAUGE = "counterGauge";
  public static final String COMPONENT_LIQUID_GAUGE = "liquidGauge";
  public static final String COMPONENT_METER = "meter";
  public static final String COMPONENT_COMPASS = "compass";
  public static final String COMPONENT_MAP = "map";
  public static final String COMPONENT_MAP_LAYER = "mapLayer";
  public static final String COMPONENT_SUBWIDGET = "subwidget";
  public static final String COMPONENT_GRAPH = "graph";
  public static final String COMPONENT_TIME_PICKER = "timePicker";
  public static final String COMPONENT_UPLOADER = "uploader";
  public static final String COMPONENT_MENU = "menu";
  public static final String COMPONENT_SUBDASHBOARD = "subdashboard";
  public static final String COMPONENT_IMPLANT = "implant";
  
  public static final String COMPONENT_FBD_INPUT = "fbdInput";
  public static final String COMPONENT_FBD_OUTPUT = "fbdOutput";
  public static final String COMPONENT_FBD_POU = "fbdPou";
  public static final String COMPONENT_FBD_JUMP = "fbdJump";
  public static final String COMPONENT_FBD_LABEL = "fbdLabel";
  public static final String COMPONENT_FBD_RETURN = "fbdReturn";
  public static final String COMPONENT_SFC_STEP = "sfcStep";
  public static final String COMPONENT_SFC_JUMP = "sfcJump";
  public static final String COMPONENT_SFC_BRANCH = "sfcBranch";
  public static final String COMPONENT_SFC_TRANSITION = "sfcTransition";
  public static final String COMPONENT_LD_POWER_RAIL = "ldRung";
  public static final String COMPONENT_LD_RETURN = "ldReturn";
  public static final String COMPONENT_LD_JUMP = "ldJump";
  public static final String COMPONENT_LD_BLOCK = "ldBlock";
  public static final String COMPONENT_LD_BRANCH = "ldBranch";
  public static final String COMPONENT_LD_COIL = "ldDirectCoil";
  public static final String COMPONENT_LD_NORMALLY_OPEN_CONTACT = "ldDirectContact";
  public static final String COMPONENT_LD_NEGATIVE_TRANSITION_SENSING_CONTACT = "ldFallingContact";
  public static final String COMPONENT_LD_NORMALLY_CLOSED_CONTACT = "ldReverseContact";
  public static final String COMPONENT_LD_POSITIVE_TRANSITION_SENSING_CONTACT = "ldRisingContact";
  public static final String COMPONENT_LD_NEGATIVE_TRANSITION_SENSING_COIL = "ldFallingCoil";
  public static final String COMPONENT_LD_RESET_COIL = "ldResetCoil";
  public static final String COMPONENT_LD_NEGATED_COIL = "ldReverseCoil";
  public static final String COMPONENT_LD_POSITIVE_TRANSITION_SENSING_COIL = "ldRisingCoil";
  public static final String COMPONENT_LD_SET_COIL = "ldSetCoil";
  public static final String COMPONENT_PC_ROOT_PANEL = "pcRoot";
  public static final String COMPONENT_BUTTON_GROUP = "buttonGroup";
  public static final String COMPONENT_BUTTON_GROUPS = "buttonGroups";
  public static final String COMPONENT_SCHEDULER = "scheduler";
  public static final String COMPONENT_PIE_CHART = "pieChart";
  public static final String COMPONENT_RING_CHART = "ringChart";
  public static final String COMPONENT_POLAR_CHART = "polarChart";
  public static final String COMPONENT_SPIDER_CHART = "spiderChart";
  public static final String COMPONENT_BLOCK_CHART = "blockChart";
  public static final String COMPONENT_BUBBLE_CHART = "bubbleChart";
  public static final String COMPONENT_INTERVAL_CHART = "intervalChart";
  public static final String COMPONENT_DEVIATION_CHART = "deviationChart";
  public static final String COMPONENT_XY_ERROR_CHART = "xyErrorChart";
  public static final String COMPONENT_VECTOR_CHART = "vectorChart";
  public static final String COMPONENT_FINANCIAL_CHART = "financialChart";
  public static final String COMPONENT_CANDLESTICK_CHART = "candlestickChart";
  public static final String COMPONENT_XY_BAR_CHART = "xyBarChart";
  public static final String COMPONENT_XY_AREA_CHART = "xyAreaChart";
  public static final String COMPONENT_XY_LINE_CHART = "xyLineChart";
  public static final String COMPONENT_GANTT_CHART = "ganttChart";
  public static final String COMPONENT_LINE_CHART = "lineChart";
  public static final String COMPONENT_AREA_CHART = "areaChart";
  public static final String COMPONENT_BAR_CHART = "barChart";
  public static final String COMPONENT_STATISTICAL_CHART = "statisticalChart";
  public static final String COMPONENT_INTERVAL_BAR_CHART = "intervalBarChart";
  public static final String COMPONENT_MIXED_XY_CHART = "mixedXYChart";
  public static final String COMPONENT_MIXED_CATEGORY_CHART = "mixedCategoryChart";
  public static final String COMPONENT_COMBINED_DOMAIN_XY_CHART = "combinedDomainXYChart";
  public static final String COMPONENT_COMBINED_RANGE_XY_CHART = "combinedRangeXYChart";
  public static final String COMPONENT_COMBINED_DOMAIN_CATEGORY_CHART = "combinedDomainCategoryChart";
  public static final String COMPONENT_COMBINED_RANGE_CATEGORY_CHART = "combinedRangeCategoryChart";
  public static final String COMPONENT_PANEL = "panel";
  public static final String COMPONENT_LAYERED_PANEL = "layeredPanel";
  public static final String COMPONENT_LAYERED_PANEL_LAYER = "layer";
  public static final String COMPONENT_TABBED_PANEL = "tabbedPanel";
  public static final String COMPONENT_CAROUSEL = "carousel";
  public static final String COMPONENT_TABBED_PANEL_TAB = "tab";
  public static final String COMPONENT_STEPS = "steps";
  public static final String COMPONENT_STEPS_CONTENT = "stepsContent";
  public static final String COMPONENT_SPLIT_PANEL = "splitPanel";
  public static final String COMPONENT_SPLIT_PANEL_FRAME = "frame";
  public static final String COMPONENT_VIDEO_PLAYER = "videoPlayer";
  public static final String COMPONENT_LED_DISPLAY = "ledDisplay";
  public static final String COMPONENT_ELLIPSE = "ellipse";
  public static final String COMPONENT_POLYGON = "polygon";
  public static final String COMPONENT_POLYLINE = "polyline";
  public static final String COMPONENT_RECTANGLE = "rectangle";
  public static final String COMPONENT_ARROW = "arrow";
  public static final int GRID_LAYOUT = 1;
  public static final int ABSOLUTE_LAYOUT = 2;
  public static final String V_GRIDX = "gridx";
  public static final String V_GRIDY = "gridy";
  public static final String V_GRID_HEIGHT = "gridHeight";
  public static final String V_GRID_WIDTH = "gridWidth";
  public static final String V_WEIGHTX = "weightx";
  public static final String V_WEIGHTY = "weighty";
  public static final String V_ANCHOR = "anchor";
  public static final String V_INSETS_TOP = "insetsTop";
  public static final String V_INSETS_LEFT = "insetsLeft";
  public static final String V_INSETS_BOTTOM = "insetsBottom";
  public static final String V_INSETS_RIGHT = "insetsRight";
  public static final String V_FILL = "fill";
  public static final String V_XCOORDINATE = "xCoordinate";
  public static final String V_YCOORDINATE = "yCoordinate";
  public static final String V_ZORDER = "zOrder";
  public static final String V_SELECTION_OPTIONS = "options";
  public static final int HORIZONTAL_ALIGNMENT_CENTER = SwingConstants.CENTER;
  public static final int HORIZONTAL_ALIGNMENT_LEFT = SwingConstants.LEFT;
  public static final int HORIZONTAL_ALIGNMENT_RIGHT = SwingConstants.RIGHT;
  public static final int VERTICAL_ALIGNMENT_CENTER = SwingConstants.CENTER;
  public static final int VERTICAL_ALIGNMENT_TOP = SwingConstants.TOP;
  public static final int VERTICAL_ALIGNMENT_BOTTOM = SwingConstants.BOTTOM;
  public static final String LEFT_CLICK = "leftClick";
  public static final String RIGHT_CLICK = "rightClick";
  public static final java.util.List<String> CONSTRAINTS_PROPERTIES_LIST = new LinkedList<>();
  public static final String ROOT_RESOURCE_ID = "";
  public static final String OLD_ROOT_RESOURCE_ID = "root";
  public static final String BUTTON_GROUPS_RESOURCE_ID = "buttonGroups";
  public static final String TEMPLATE_TAG = "template";
  public static final String RESOURCES_TAG = "resources";
  public static final String RESOURCE_TAG = "resource";
  public static final String ID_ATTRIBUTE = "id";
  public static final String PARENT_ATTRIBUTE = "parent";
  public static final String VIEW_TAG = "view";
  public static final String SCRIPTS_TAG = "scripts";
  public static final String SCRIPT_TAG = "script";
  public static final String NAME_ATTRIBUTE = "name";
  public static final String RESOURCE_ID_ATTRIBUTE = "resourceID";
  public static final String BINDINGS_TAG = "bindings";
  public static final String BINDING_TAG = "binding";
  public static final String TARGET_ATTRIBUTE = "target";
  public static final String EXPRESSION_ELEMENT = "expression";
  public static final String CONDITION_ELEMENT = "condition";
  public static final String EVALUATION_PATTERN_ATTRIBUTE = "pattern";
  public static final String PERIOD_ATTRIBUTE = "period";
  public static final String ACTIVATOR_REFERENCE_ATTRIBUTE = "activator";
  public static final String QUEUE_ATTRIBUTE = "queue";
  public static final String ANCHOR_CENTER = "center";
  public static final String ANCHOR_NORTHEAST = "northEast";
  public static final String ANCHOR_NORTHWEST = "northWest";
  public static final String ANCHOR_SOUTHEAST = "southEast";
  public static final String ANCHOR_SOUTHWEST = "southWest";
  public static final String ANCHOR_EAST = "east";
  public static final String ANCHOR_WEST = "west";
  public static final String ANCHOR_SOUTH = "south";
  public static final String ANCHOR_NORTH = "north";
  public static final String ANCHOR_CENTER_TITLE = Cres.get().getString("center");
  public static final String ANCHOR_NORTH_TITLE = Cres.get().getString("wAnchorPageStart");
  public static final String ANCHOR_EAST_TITLE = Cres.get().getString("wAnchorLineEnd");
  public static final String ANCHOR_SOUTH_TITLE = Cres.get().getString("wAnchorPageEnd");
  public static final String ANCHOR_WEST_TITLE = Cres.get().getString("wAnchorLineStart");
  public static final String ANCHOR_NORTHWEST_TITLE = Cres.get().getString("wAnchorFirstLineStart");
  public static final String ANCHOR_NORTHEAST_TITLE = Cres.get().getString("wAnchorFirstLineEnd");
  public static final String ANCHOR_SOUTHEAST_TITLE = Cres.get().getString("wAnchorLastLineEnd");
  public static final String ANCHOR_SOUTHWEST_TITLE = Cres.get().getString("wAnchorLastLineStart");
  public static final float WEIGHTX_DEFAULT_VALUE = (float) 1.0;
  public static final float WEIGHTY_DEFAULT_VALUE = (float) 1.0;
  public static final float FLOAT_DELTA = (float) 0.0001;
  public static final String ALIGN_TOP = "alignTop";
  public static final String ALIGN_BOTTOM = "alignBottom";
  public static final String ALIGN_LEFT = "alignLeft";
  public static final String ALIGN_RIGHT = "alignRight";
  public static final String ALIGN_HAXIS = "alignHAxis";
  public static final String ALIGN_VAXIS = "alignVAxis";
  public static final String SAME_WIDTH_MIN = "sameWidthMin";
  public static final String SAME_WIDTH_MAX = "sameWidthMax";
  public static final String SAME_HEIGHT_MAX = "sameHeightMax";
  public static final String SAME_HEIGHT_MIN = "sameHeightMin";
  public static final String GROUP_CUSTOM_PROPERTIES = ContextUtils.createGroup(ContextUtils.GROUP_DEFAULT, Res.get().getString("wCustomProperties"));
  public static final String HORIZONTAL_SPACING_EQUALS_BETWEEN_CENTERS = "hSpacingEqualsBetweenCenters";
  public static final String VERTICAL_SPACING_EQUALS_BETWEEN_CENTERS = "vSpacingEqualsBetweenCenters";
  public static final String HORIZONTAL_SPACING_EQUALS_BETWEEN_BOUNDS = "hSpacingEqualsBetweenBounds";
  public static final String VERTICAL_SPACING_EQUALS_BETWEEN_BOUNDS = "vSpacingEqualsBetweenBounds";
  public static final String F_NAME = "name";
  public static final String F_DESCRIPTION = "description";
  public static final String F_HELP = "help";
  /**
   * @deprecated Use of this constant is strongly discouraged as it may confuse and lead to hard-to-hunt bugs. 
   * Please use corresponding field for format storage only (not value) referring by {@linkplain #F_FORMAT appropriate} name.   
   */
  @Deprecated
  public static final String F_VALUE = "format"; // It's not a format, it's actually a value. "format" left for compatibility reasons.
  /**
   * The same as {@link #F_VALUE} but with straightforward (non-confusing) name
   */
  public static final String F_FORMAT = "format";
  
  public static final TableFormat CUSTOM_PROPERTIES_FORMAT = new TableFormat();
  public static String RESOURCE_NAME_PATTERN_EXPRESSION = "^[\\w_]{1,100}$";
  public static Pattern RESOURCE_ID_REGEX = Pattern.compile(RESOURCE_NAME_PATTERN_EXPRESSION);

  static
  {
    CONSTRAINTS_PROPERTIES_LIST.add(V_GRIDX);
    CONSTRAINTS_PROPERTIES_LIST.add(V_GRIDY);
    CONSTRAINTS_PROPERTIES_LIST.add(V_GRID_WIDTH);
    CONSTRAINTS_PROPERTIES_LIST.add(V_GRID_HEIGHT);
    CONSTRAINTS_PROPERTIES_LIST.add(V_ANCHOR);
    CONSTRAINTS_PROPERTIES_LIST.add(V_INSETS_TOP);
    CONSTRAINTS_PROPERTIES_LIST.add(V_INSETS_BOTTOM);
    CONSTRAINTS_PROPERTIES_LIST.add(V_INSETS_RIGHT);
    CONSTRAINTS_PROPERTIES_LIST.add(V_INSETS_LEFT);
    CONSTRAINTS_PROPERTIES_LIST.add(V_FILL);
    CONSTRAINTS_PROPERTIES_LIST.add(V_WEIGHTX);
    CONSTRAINTS_PROPERTIES_LIST.add(V_WEIGHTY);
    CONSTRAINTS_PROPERTIES_LIST.add(V_XCOORDINATE);
    CONSTRAINTS_PROPERTIES_LIST.add(V_YCOORDINATE);
    CONSTRAINTS_PROPERTIES_LIST.add(V_ZORDER);

  }

  static
  {
    CUSTOM_PROPERTIES_FORMAT.addField('S', F_NAME, Cres.get().getString("name"));
    CUSTOM_PROPERTIES_FORMAT.addField('T', F_VALUE, Cres.get().getString("format"), null, true);
    CUSTOM_PROPERTIES_FORMAT.addField('S', F_DESCRIPTION, Cres.get().getString("description"));
    CUSTOM_PROPERTIES_FORMAT.addField('S', F_HELP, Cres.get().getString("help"), null, true);
  }
  
  public static boolean hasInvalidResourceID(String ID)
  {
    return !(ID.equals(ROOT_RESOURCE_ID) || ID.equals(BUTTON_GROUPS_RESOURCE_ID) || RESOURCE_ID_REGEX.matcher(ID).matches());
  }
  
  public enum ComponentGroup
  {
    CONTENT, CONTAINER, CHART_CATEGORY, CHART_XY, CHART_OTHER, GAUGES, CUSTOM, HIDDEN, SFC, FBD, LD, WORKFLOW
  }
}
