package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.parser.ParseException;
import com.tibbo.aggregate.common.filter.converter.ClassDteFilterConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VisitorTest {

    public static final TableFormat tableFormat = new TableFormat();
    public static final DataTable dataTable;

    static {
        FieldFormat ff = FieldFormat.create("<FIRST_NAME><S>");
        tableFormat.addField(ff);

        ff = FieldFormat.create("<LAST_NAME><S>");
        tableFormat.addField(ff);

        ff = FieldFormat.create("<STATUS><B>");
        tableFormat.addField(ff);

        ff = FieldFormat.create("<AGE><I>");
        tableFormat.addField(ff);

        dataTable = new SimpleDataTable(tableFormat);
        dataTable.addRecord("NAME1", "LASTNAME1", false, 45);
        dataTable.addRecord("NAME2", "LASTNAME2", true, 55);
        dataTable.addRecord("NAME3", "LASTNAME3", true, 65);
    }

    @Test
    public void test1() throws ParseException {
        String exp = "{STATUS}";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(2, (int)dt.getRecordCount());
    }

    @Test
    public void test2() throws ParseException {
        String exp = "{AGE} > 55";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(1, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME3", firstName);
    }

    @Test
    public void test3() throws ParseException {
        String exp = "{AGE} >= 55";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(2, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME2", firstName);
        firstName = dt.getRecord(1).getString("FIRST_NAME");
        Assertions.assertEquals("NAME3", firstName);
    }

    @Test
    public void test4() throws ParseException {
        String exp = "{AGE} < 55";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(1, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME1", firstName);
    }

    @Test
    public void test5() throws ParseException {
        String exp = "{AGE} <= 55";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(2, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME1", firstName);
        firstName = dt.getRecord(1).getString("FIRST_NAME");
        Assertions.assertEquals("NAME2", firstName);
    }

    @Test
    public void test6() throws ParseException {
        String exp = "{AGE} <= 55 && {STATUS}";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(1, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME2", firstName);
    }

    @Test
    public void test7() throws ParseException {
        String exp = "{AGE} <= 55 || {STATUS}";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        fe.setDataTable(dataTable);
        DataTable dt = fe.filterTable();
        Assertions.assertEquals(3, (int)dt.getRecordCount());
        String firstName = dt.getRecord(0).getString("FIRST_NAME");
        Assertions.assertEquals("NAME1", firstName);
        firstName = dt.getRecord(1).getString("FIRST_NAME");
        Assertions.assertEquals("NAME2", firstName);
        firstName = dt.getRecord(2).getString("FIRST_NAME");
        Assertions.assertEquals("NAME3", firstName);
    }

    @Test
    public void test8() throws ParseException {
        String exp = "{AGE} <= 55 || {AGE} > 30";
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        Expression e = fe.getRootExpression();
        ClassDteFilterConverter.convertExpressionToClassFilter(e);
    }

    @Test
    public void test9() throws ParseException {
        String exp = "{AGE} <= 55 || {AGE} > 30 && {AGE} <= 60" ;
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        Expression e = fe.getRootExpression();
        ClassDteFilterConverter.convertExpressionToClassFilter(e);
    }

    @Test
    public void test10() throws ParseException {
        String exp = "({AGE} <= 55 || {AGE} > 30) && {AGE} <= 60" ;
        FilterEvaluator fe = new FilterEvaluator(dataTable.getFormat(), exp);
        Expression e = fe.getRootExpression();
        ClassDteFilterConverter.convertExpressionToClassFilter(e);
    }

}
