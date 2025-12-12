package com.tibbo.aggregate.common.expression.function;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.Function;
import com.tibbo.aggregate.common.expression.function.color.BlueFunction;
import com.tibbo.aggregate.common.expression.function.color.ColorFunction;
import com.tibbo.aggregate.common.expression.function.color.GreenFunction;
import com.tibbo.aggregate.common.expression.function.color.RedFunction;
import com.tibbo.aggregate.common.expression.function.context.AvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.CallFunctionExFunction;
import com.tibbo.aggregate.common.expression.function.context.CallFunctionFunction;
import com.tibbo.aggregate.common.expression.function.context.DcFunction;
import com.tibbo.aggregate.common.expression.function.context.DrFunction;
import com.tibbo.aggregate.common.expression.function.context.DtFunction;
import com.tibbo.aggregate.common.expression.function.context.EventAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.EventFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.EventGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.FireEventFunction;
import com.tibbo.aggregate.common.expression.function.context.FullDescriptionFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionInputFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.FunctionOutputFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.GetVariableFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableExFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableFieldFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableFunction;
import com.tibbo.aggregate.common.expression.function.context.SetVariableRecordFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableAvailableFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableFormatFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableGroupFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableReadableFunction;
import com.tibbo.aggregate.common.expression.function.context.VariableWritableFunction;
import com.tibbo.aggregate.common.expression.function.date.DateAddFunction;
import com.tibbo.aggregate.common.expression.function.date.DateCreateFunction;
import com.tibbo.aggregate.common.expression.function.date.DateDiffFunction;
import com.tibbo.aggregate.common.expression.function.date.DateFieldFunction;
import com.tibbo.aggregate.common.expression.function.date.FormatDateFunction;
import com.tibbo.aggregate.common.expression.function.date.ParseDateFunction;
import com.tibbo.aggregate.common.expression.function.date.PrintPeriodFunction;
import com.tibbo.aggregate.common.expression.function.date.TimeFunction;
import com.tibbo.aggregate.common.expression.function.math.AverageFunction;
import com.tibbo.aggregate.common.expression.function.math.VarianceFunction;
import com.tibbo.aggregate.common.expression.function.math.FrequencyFunction;
import com.tibbo.aggregate.common.expression.function.math.LinearRegressionFunction;
import com.tibbo.aggregate.common.expression.function.math.MaxFunction;
import com.tibbo.aggregate.common.expression.function.math.MedianFunction;
import com.tibbo.aggregate.common.expression.function.math.MinFunction;
import com.tibbo.aggregate.common.expression.function.math.ModeFunction;
import com.tibbo.aggregate.common.expression.function.math.SimpleMovingAverageFunction;
import com.tibbo.aggregate.common.expression.function.math.SimpleMovingMedianFunction;
import com.tibbo.aggregate.common.expression.function.math.StandardDeviationFunction;
import com.tibbo.aggregate.common.expression.function.math.StandardErrorMeanFunction;
import com.tibbo.aggregate.common.expression.function.math.SumFunction;
import com.tibbo.aggregate.common.expression.function.number.EqFunction;
import com.tibbo.aggregate.common.expression.function.number.FormatNumberFunction;
import com.tibbo.aggregate.common.expression.function.number.GeFunction;
import com.tibbo.aggregate.common.expression.function.number.GtFunction;
import com.tibbo.aggregate.common.expression.function.number.JavaMathFunction;
import com.tibbo.aggregate.common.expression.function.number.LeFunction;
import com.tibbo.aggregate.common.expression.function.number.LtFunction;
import com.tibbo.aggregate.common.expression.function.number.NeFunction;
import com.tibbo.aggregate.common.expression.function.other.AndFunction;
import com.tibbo.aggregate.common.expression.function.other.CaseFunction;
import com.tibbo.aggregate.common.expression.function.other.CatchFunction;
import com.tibbo.aggregate.common.expression.function.other.EvaluateFunction;
import com.tibbo.aggregate.common.expression.function.other.FailFunction;
import com.tibbo.aggregate.common.expression.function.string.FormatFunction;
import com.tibbo.aggregate.common.expression.function.other.GroupsFunction;
import com.tibbo.aggregate.common.expression.function.other.IfFunction;
import com.tibbo.aggregate.common.expression.function.other.LdFunction;
import com.tibbo.aggregate.common.expression.function.other.LoginFunction;
import com.tibbo.aggregate.common.expression.function.other.OrFunction;
import com.tibbo.aggregate.common.expression.function.other.SleepFunction;
import com.tibbo.aggregate.common.expression.function.other.StFunction;
import com.tibbo.aggregate.common.expression.function.other.TableFromCSVFunction;
import com.tibbo.aggregate.common.expression.function.other.TableFromJSONFunction;
import com.tibbo.aggregate.common.expression.function.other.TableToJSONFunction;
import com.tibbo.aggregate.common.expression.function.other.TraceFunction;
import com.tibbo.aggregate.common.expression.function.other.UserFunction;
import com.tibbo.aggregate.common.expression.function.other.WriteLogFunction;
import com.tibbo.aggregate.common.expression.function.other.XPathFunction;
import com.tibbo.aggregate.common.expression.function.string.CharacterFunction;
import com.tibbo.aggregate.common.expression.function.string.DataBlockFunction;
import com.tibbo.aggregate.common.expression.function.string.JavaStringFunction;
import com.tibbo.aggregate.common.expression.function.string.SplitFunction;
import com.tibbo.aggregate.common.expression.function.string.UrlDecodeFunction;
import com.tibbo.aggregate.common.expression.function.string.UrlEncodeFunction;
import com.tibbo.aggregate.common.expression.function.table.AddColumnsExFunction;
import com.tibbo.aggregate.common.expression.function.table.AddColumnsFunction;
import com.tibbo.aggregate.common.expression.function.table.AddRecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.AdjustRecordLimitsFunction;
import com.tibbo.aggregate.common.expression.function.table.AggregateFunction;
import com.tibbo.aggregate.common.expression.function.table.CellFunction;
import com.tibbo.aggregate.common.expression.function.table.ClearFunction;
import com.tibbo.aggregate.common.expression.function.table.ConvertFunction;
import com.tibbo.aggregate.common.expression.function.table.CopyFunction;
import com.tibbo.aggregate.common.expression.function.table.DecodeFunction;
import com.tibbo.aggregate.common.expression.function.table.DescribeFunction;
import com.tibbo.aggregate.common.expression.function.table.DescriptionFunction;
import com.tibbo.aggregate.common.expression.function.table.DistinctFunction;
import com.tibbo.aggregate.common.expression.function.table.EncodeFunction;
import com.tibbo.aggregate.common.expression.function.table.FilterFunction;
import com.tibbo.aggregate.common.expression.function.table.GetFormatFunction;
import com.tibbo.aggregate.common.expression.function.table.GetQualityFunction;
import com.tibbo.aggregate.common.expression.function.table.GetTimestampFunction;
import com.tibbo.aggregate.common.expression.function.table.HasFieldFunction;
import com.tibbo.aggregate.common.expression.function.table.IntersectFunction;
import com.tibbo.aggregate.common.expression.function.table.JoinFunction;
import com.tibbo.aggregate.common.expression.function.table.PrintFunction;
import com.tibbo.aggregate.common.expression.function.table.ProcessBindingsFunction;
import com.tibbo.aggregate.common.expression.function.table.RecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.RemoveColumnsFunction;
import com.tibbo.aggregate.common.expression.function.table.RemoveRecordsFunction;
import com.tibbo.aggregate.common.expression.function.table.SelectFunction;
import com.tibbo.aggregate.common.expression.function.table.SetFunction;
import com.tibbo.aggregate.common.expression.function.table.SetMultipleFunction;
import com.tibbo.aggregate.common.expression.function.table.SetNestedFieldFunction;
import com.tibbo.aggregate.common.expression.function.table.SetQualityFunction;
import com.tibbo.aggregate.common.expression.function.table.SetTimestampFunction;
import com.tibbo.aggregate.common.expression.function.table.SortFunction;
import com.tibbo.aggregate.common.expression.function.table.SubtableFunction;
import com.tibbo.aggregate.common.expression.function.table.TableFunction;
import com.tibbo.aggregate.common.expression.function.table.UnionFunction;
import com.tibbo.aggregate.common.expression.function.table.ValidateFunction;
import com.tibbo.aggregate.common.expression.function.type.BooleanFunction;
import com.tibbo.aggregate.common.expression.function.type.DoubleFunction;
import com.tibbo.aggregate.common.expression.function.type.FetchDataBlockFunction;
import com.tibbo.aggregate.common.expression.function.type.FloatFunction;
import com.tibbo.aggregate.common.expression.function.type.IntegerFunction;
import com.tibbo.aggregate.common.expression.function.type.LongFunction;
import com.tibbo.aggregate.common.expression.function.type.StringFunction;
import com.tibbo.aggregate.common.expression.function.type.TimestampFunction;

public enum DefaultFunctions
{
  ABS(new JavaMathFunction("abs", "Double value", "Double", Cres.get().getString("fDescAbs"))),
  ACOS(new JavaMathFunction("acos", "Double value", "Double", Cres.get().getString("fDescAcos"))),
  ASIN(new JavaMathFunction("asin", "Double value", "Double", Cres.get().getString("fDescAsin"))),
  ATAN(new JavaMathFunction("atan", "Double value", "Double", Cres.get().getString("fDescAtan"))),
  CBRT(new JavaMathFunction("cbrt", "Double value", "Double", Cres.get().getString("fDescCbrt"))),
  CEIL(new JavaMathFunction("ceil", "Double value", "Double", Cres.get().getString("fDescCeil"))),
  COS(new JavaMathFunction("cos", "Double value", "Double", Cres.get().getString("fDescCos"))),
  COSH(new JavaMathFunction("cosh", "Double value", "Double", Cres.get().getString("fDescCosh"))),
  E(new FloatConstantFunction("e", (float) Math.E, Cres.get().getString("fDescE"))),
  EXP(new JavaMathFunction("exp", "Double value", "Double", Cres.get().getString("fDescExp"))),
  EQ(new EqFunction()),
  FLOOR(new JavaMathFunction("floor", "Double value", "Double", Cres.get().getString("fDescFloor"))),
  FORMAT_NUMBER(new FormatNumberFunction()),
  GE(new GeFunction()),
  GT(new GtFunction()),
  LE(new LeFunction()),
  LOG(new JavaMathFunction("log", "Double value", "Double", Cres.get().getString("fDescLog"))),
  LOG10(new JavaMathFunction("log10", "Double value", "Double", Cres.get().getString("fDescLog10"))),
  LT(new LtFunction()),
  NE(new NeFunction()),
  PI(new FloatConstantFunction("pi", (float) Math.PI, Cres.get().getString("fDescPi"))),
  POW(new JavaMathFunction("pow", "Double base, Double power", "Double", Cres.get().getString("fDescPow"))),
  RANDOM(new JavaMathFunction("random", "", "Double", Cres.get().getString("fDescRandom"))),
  ROUND(new JavaMathFunction("round", "Double value", "Long", Cres.get().getString("fDescRound"))),
  SIGNUM(new JavaMathFunction("signum", "Double value", "Double", Cres.get().getString("fDescSignum"))),
  SIN(new JavaMathFunction("sin", "Double value", "Double", Cres.get().getString("fDescSin"))),
  SINH(new JavaMathFunction("sinh", "Double value", "Double", Cres.get().getString("fDescSinh"))),
  SQRT(new JavaMathFunction("sqrt", "Double value", "Double", Cres.get().getString("fDescSqrt"))),
  TAN(new JavaMathFunction("tan", "Double value", "Double", Cres.get().getString("fDescTan"))),
  TANH(new JavaMathFunction("tanh", "Double value", "Double", Cres.get().getString("fDescTanh"))),

  //Mathematical Aggregation Functions
  MIN(new MinFunction()),
  MAX(new MaxFunction()),
  AVG(new AverageFunction()),
  VARIANCE(new VarianceFunction()),
  FREQUENCY(new FrequencyFunction()),
  LINEAR_REGRESSION(new LinearRegressionFunction()),
  MEDIAN(new MedianFunction()),
  MODE(new ModeFunction()),
  SMA(new SimpleMovingAverageFunction()),
  SMM(new SimpleMovingMedianFunction()),
  STANDARD_DEVIATION(new StandardDeviationFunction()),
  STANDARD_ERROR(new StandardErrorMeanFunction()),
  SUM(new SumFunction()),

  // String Functions
  CONTAINS(new JavaStringFunction("contains", "String string, String substring", "Boolean", Cres.get().getString("fDescContains"))),
  ENDS_WITH(new JavaStringFunction("endsWith", "String string, String suffix", "Boolean", Cres.get().getString("fDescEndsWith"))),
  FORMAT(new FormatFunction()),
  GROUPS(new GroupsFunction()),
  INDEX(new JavaStringFunction("index","indexOf", "String string, String substring [, Integer fromIndex]", "Integer", Cres.get().getString("fDescIndex"))),
  IS_DIGIT(new CharacterFunction("isDigit", Cres.get().getString("fDescIsDigit"))),
  IS_LETTER(new CharacterFunction("isLetter", Cres.get().getString("fDescIsLetter"))),
  IS_LOWER_CASE(new CharacterFunction("isLowerCase", Cres.get().getString("fDescIsLowerCase"))),
  IS_UPPER_CASE(new CharacterFunction("isUpperCase", Cres.get().getString("fDescIsUpperCase"))),
  IS_WHITESPACE(new CharacterFunction("isWhitespace", Cres.get().getString("fDescIsWhitespace"))),
  LAST_INDEX(new JavaStringFunction("lastIndex", "lastIndexOf", "String string, String substring [, Integer fromIndex]", "Integer", Cres.get().getString("fDescLastIndex"))),
  LENGTH(new JavaStringFunction("length", "String string", "Integer", Cres.get().getString("fDescLength"))),
  LOWER(new JavaStringFunction("lower", "toLowerCase", "String string", "String", Cres.get().getString("fDescLower"))),
  REPLACE(new JavaStringFunction("replace", "String string, String target, String replacement", "String", Cres.get().getString("fDescReplace"))),
  REPLACE_SMART(new JavaStringFunction("replaceSmart", "replaceAll", "String string, String regex, String replacement", "String", Cres.get().getString("fDescReplaceSmart"))),
  SPLIT(new SplitFunction()),
  STARTS_WITH(new JavaStringFunction("startsWith", "String string, String prefix", "Boolean", Cres.get().getString("fDescStartsWith"))),
  SUBSTRING(new JavaStringFunction("substring", "String string, Integer beginIndex [, Integer endIndex]", "String", Cres.get().getString("fDescSubstring"))),
  TRIM(new JavaStringFunction("trim", "String string", "String", Cres.get().getString("fDescTrim"))),
  UPPER(new JavaStringFunction("upper", "toUpperCase", "String string", "String", Cres.get().getString("fDescUpper"))),
  URL_DECODE(new UrlDecodeFunction()),
  URL_ENCODE(new UrlEncodeFunction()),
  DATA_BLOCK(new DataBlockFunction()),
  
  // Date/Time Functions
  DATE(new DateCreateFunction()),
  DATE_ADD(new DateAddFunction()),
  DATE_DIFF(new DateDiffFunction()),
  DAY(new DateFieldFunction("day", Calendar.DAY_OF_MONTH, Cres.get().getString("fDescDay"))),
  DAY_OF_WEEK(new DateFieldFunction("dayOfWeek", Calendar.DAY_OF_WEEK, Cres.get().getString("fDescDayOfWeek"))),
  DAY_OF_YEAR(new DateFieldFunction("dayOfYear", Calendar.DAY_OF_YEAR, Cres.get().getString("fDescDayOfYear"))),
  FORMAT_DATE(new FormatDateFunction()),
  HOUR(new DateFieldFunction("hour", Calendar.HOUR_OF_DAY, Cres.get().getString("fDescHour"))),
  MILLISECOND(new DateFieldFunction("millisecond", Calendar.MILLISECOND, Cres.get().getString("fDescMillisecond"))),
  MINUTE(new DateFieldFunction("minute", Calendar.MINUTE, Cres.get().getString("fDescMinute"))),
  MONTH(new DateFieldFunction("month", Calendar.MONTH, Cres.get().getString("fDescMonth"))),
  NOW(new JavaConstructorFunction("now", Date.class.getName(), Function.GROUP_DATE_TIME_PROCESSING, "Date", Cres.get().getString("fDescNow"))),
  SECOND(new DateFieldFunction("second", Calendar.SECOND, Cres.get().getString("fDescSecond"))),
  PARSE_DATE(new ParseDateFunction()),
  PRINT_PERIOD(new PrintPeriodFunction()),
  TIME(new TimeFunction()),
  YEAR(new DateFieldFunction("year", Calendar.YEAR, Cres.get().getString("fDescYear"))),
  
  // Color Processing Functions
  BLUE(new BlueFunction()),
  BRIGHTER(new JavaMethodFunction(Color.class.getName(), "brighter", "brighter", false, Function.GROUP_COLOR_PROCESSING, "Color color", "Color", Cres.get().getString("fDescBrighter"))),
  COLOR(new ColorFunction()),
  DARKER(new JavaMethodFunction(Color.class.getName(), "darker", "darker", false, Function.GROUP_COLOR_PROCESSING, "Color color", "Color", Cres.get().getString("fDescDarker"))),
  GREEN(new GreenFunction()),
  RED(new RedFunction()),
  
  // Data Table Processing Functions
  ADD_COLUMNS(new AddColumnsFunction()),
  ADD_COLUMNS_EX(new AddColumnsExFunction()),
  ADD_RECORDS(new AddRecordsFunction()),
  ADJUST_RECORD_LIMITS(new AdjustRecordLimitsFunction()),
  AGGREGATE(new AggregateFunction()),
  CELL(new CellFunction()),
  CLEAR(new ClearFunction()),
  CONVERT(new ConvertFunction()),
  COPY(new CopyFunction()),
  DECODE(new DecodeFunction()),
  DESCRIBE(new DescribeFunction()),
  DESCRIPTION(new DescriptionFunction()),
  DISTINCT(new DistinctFunction()),
  ENCODE(new EncodeFunction()),
  FILTER(new FilterFunction()),
  INTERSECT(new IntersectFunction()),
  FETCH_DATA_BLOCK(new FetchDataBlockFunction()),
  GET_FORMAT(new GetFormatFunction()),
  GET_QUALITY(new GetQualityFunction()),
  GET_TIMESTAMP(new GetTimestampFunction()),
  HAS_FIELD(new HasFieldFunction()),
  JOIN(new JoinFunction()),
  PRINT(new PrintFunction()),
  PROCESS_BINDINGS(new ProcessBindingsFunction()),
  RECORDS(new RecordsFunction()),
  REMOVE_COLUMNS(new RemoveColumnsFunction()),
  REMOVE_RECORDS(new RemoveRecordsFunction()),
  SELECT(new SelectFunction()),
  SET(new SetFunction()),
  SET_MULTIPLE(new SetMultipleFunction()),
  SET_NESTED_FIELD(new SetNestedFieldFunction()),
  SET_QUALITY(new SetQualityFunction()),
  SET_TIMESTAMP(new SetTimestampFunction()),
  SORT(new SortFunction()),
  SUBTABLE(new SubtableFunction()),
  TABLE(new TableFunction()),
  TABLE_FROM_CSV(new TableFromCSVFunction()),
  TABLE_FROM_JSON(new TableFromJSONFunction()),
  TABLE_TO_JSON(new TableToJSONFunction(Function.GROUP_OTHER)),
  UNION(new UnionFunction()),
  VALIDATE(new ValidateFunction()),

  // Type Conversion Functions
  BOOLEAN(new BooleanFunction()),
  DOUBLE(new DoubleFunction()),
  INTEGER(new IntegerFunction()),
  FLOAT(new FloatFunction()),
  LONG(new LongFunction()),
  STRING(new StringFunction()),
  TIMESTAMP(new TimestampFunction()),
  
  // Context-Related Functions
  AVAILABLE(new AvailableFunction()),
  CALL_FUNCTION(new CallFunctionFunction()),
  CALL_FUNCTION_EX(new CallFunctionExFunction()),
  FULL_DESCRIPTION(new FullDescriptionFunction()),
  DC(new DcFunction()),
  DR(new DrFunction()),
  DT(new DtFunction()),
  EVENT_AVAILABLE(new EventAvailableFunction("eventAvailable", Function.GROUP_CONTEXT_RELATED)),
  EVENT_FORMAT(new EventFormatFunction()),
  EVENT_GROUP(new EventGroupFunction()),
  FIRE_EVENT(new FireEventFunction()),
  FUNCTION_AVAILABLE(new FunctionAvailableFunction("functionAvailable", Function.GROUP_CONTEXT_RELATED)),
  FUNCTION_GROUP(new FunctionGroupFunction()),
  FUNCTION_INPUT_FORMAT(new FunctionInputFormatFunction()),
  FUNCTION_OUTPUT_FORMAT(new FunctionOutputFormatFunction()),
  GET_VARIABLE(new GetVariableFunction()),
  SET_VARIABLE(new SetVariableFunction()),
  SET_VARIABLE_EX(new SetVariableExFunction()),
  SET_VARIABLE_FIELD(new SetVariableFieldFunction()),
  SET_VARIABLE_RECORD(new SetVariableRecordFunction()),
  VARIABLE_AVAILABLE(new VariableAvailableFunction()),
  VARIABLE_FORMAT(new VariableFormatFunction()),
  VARIABLE_GROUP(new VariableGroupFunction()),
  VARIABLE_READABLE(new VariableReadableFunction("variableReadable", Function.GROUP_CONTEXT_RELATED)),
  VARIABLE_WRITABLE(new VariableWritableFunction(Function.GROUP_CONTEXT_RELATED)),
  
  // Miscellaneous Functions
  AND(new AndFunction()),
  EVALUATE(new EvaluateFunction()),
  CASE(new CaseFunction()),
  CATCH(new CatchFunction()),
  FAIL(new FailFunction()),
  IF(new IfFunction()),
  LD(new LdFunction()),
  LOGIN(new LoginFunction()),
  OR(new OrFunction()),
  SLEEP(new SleepFunction()),
  ST(new StFunction()),
  TRACE(new TraceFunction()),
  USER(new UserFunction()),
  XPATH(new XPathFunction()),
  
  // System Functions
  EXPRESSION_EDITOR_OPTIONS(new ExpressionEditorOptionsFunction()),
  WRITE_LOG(new WriteLogFunction()),

  // Deprecated Functions
  HAS_VARIABLE(new VariableReadableFunction("hasVariable", Function.GROUP_SYSTEM)),
  HAS_FUNCTION(new FunctionAvailableFunction("hasFunction", Function.GROUP_SYSTEM)),
  HAS_EVENT(new EventAvailableFunction("hasEvent", Function.GROUP_SYSTEM)),
  JSON(new TableFromJSONFunction("json", Function.GROUP_SYSTEM)),
  TABLE_QUALITY(new GetQualityFunction("tableQuality")),
  TABLE_TIMESTAMP(new GetTimestampFunction("tableTimestamp"));

  public final Function impl;
  public final String name;
  
  DefaultFunctions(Function impl)
  {
    this.impl = impl;
    this.name = impl.getName();
  }

  public static void registerDefaultFunctions(Map<String, Function> defaultFunctions)
  {
    for (DefaultFunctions function : DefaultFunctions.values())
    {
      registerDefaultFunction(function.getName(), new FunctionStatisticDecorator(function.impl), defaultFunctions);
    }
  }
  
  public String getName()
  {
    return name;
  }
  
  @Override
  public String toString()
  {
    return getName();
  }
  
  private static void registerDefaultFunction(String name, Function impl, Map<String, Function> defaultFunctions)
  {
    if (defaultFunctions.containsKey(name))
    {
      throw new IllegalArgumentException("Function already registered:" + name);
    }
    
    defaultFunctions.put(name, impl);
  }

}
