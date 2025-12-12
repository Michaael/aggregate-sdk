package com.tibbo.aggregate.common.expression.function;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.Evaluator;
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
import com.tibbo.aggregate.common.tests.CommonsFixture;
import com.tibbo.aggregate.common.tests.CommonsTestCase;

public class TestMathematicalFunctions extends CommonsTestCase {
    private Evaluator ev;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        ev = CommonsFixture.createTestEvaluator();
    }
    public void testAverageFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 192.0;

        Double result = (Double) new AverageFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testDispersionFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 76579.3;

        Double result = (Double) new VarianceFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testFrequencyFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Long expected = 1L;

        DataTable result = (DataTable) new FrequencyFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result.getRecord(0).getLong(1));
    }
    public void testLinearRegressionFunction() throws Exception
    {
        TableFormat tableFormat = new TableFormat();
        tableFormat.addField(FieldFormat.create("x",FieldFormat.DOUBLE_FIELD));
        tableFormat.addField(FieldFormat.create("y",FieldFormat.DOUBLE_FIELD));
        DataTable table = new SimpleDataTable(tableFormat);
        table.addRecord(1,3);
        table.addRecord(2,4);
        table.addRecord(3,5);
        Double expected1 = 1.0;
        Double expected2 = 2.0;

        DataTable result = (DataTable) new LinearRegressionFunction().execute(null, null, table, "x","y");

        assertEquals(expected1, result.getRecord(0).getDouble(0),0.1);
        assertEquals(expected2, result.getRecord(0).getDouble(1),0.1);
    }
    public void testMaxFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 666.0;

        Double result = (Double) new MaxFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testMedianFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 123.0;

        Double result = (Double) new MedianFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testMinFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = -111.0;

        Double result = (Double) new MinFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testModeFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = -111.0;

        Double result = (Double) new ModeFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testSimpleMovingAverageFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 172.5;

        DataTable result = (DataTable) new SimpleMovingAverageFunction().execute(null, null, defaultTable, "int",2);

        assertEquals(expected, result.getRecord(1).getDouble(1),0.1);
    }
    public void testSimpleMovingMedianFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 172.5;

        DataTable result = (DataTable) new SimpleMovingMedianFunction().execute(null, null, defaultTable, "int",2);

        assertEquals(expected, result.getRecord(1).getDouble(1),0.1);
    }
    public void testStandardDeviationFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 276.7;

        Double result = (Double) new StandardDeviationFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testStandardErrorMeanFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 256.2;

        Double result = (Double) new StandardErrorMeanFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result,0.1);
    }
    public void testSumFunction() throws Exception
    {
        DataTable defaultTable = ev.getDefaultResolver().getDefaultTable();
        Double expected = 1344.0;

        Double result = (Double) new SumFunction().execute(null, null, defaultTable, "int");

        assertEquals(expected, result);
    }
}
