package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.expression.parser.ParseException;
import com.tibbo.aggregate.common.filter.converter.ClassDteFilterConverter;
import com.tibbo.aggregate.common.filter.converter.ConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.omg.CORBA.portable.ApplicationException;

public class SmartFilterConverterTest {

    @Test
    public void testLessThan() throws ParseException {
        String expression = "{COLUMN1} < 30";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testLessThanReverted() throws ParseException {
        String expression = "30 > {COLUMN1}";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testNotColumn() throws ParseException {

        Assertions.assertThrows(ConversionException.class, () -> {
            String expression = "!{COLUMN1}";
            FilterEvaluator fe = new FilterEvaluator(expression);
            ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        });
        String expression = "!contains({COLUMN1}, \"test\")";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testContainsFunction() throws ParseException {
        String expression = "contains({NAME}, \"substr\")";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testDoesNotContainFunction() throws ParseException {
        String expression = "!contains({NAME}, \"substr\")";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testLessOrDoesNotContainFunction() throws ParseException {
        String expression = "{AGE} < 60 || !contains({NAME}, \"substr\")";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(2, classFilter.getRecordCount());
    }

    @Test
    public void testNotEqualOperation() throws ParseException {
        String expression = "{AGE} != 50";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }

    @Test
    public void testDateFunction() throws ParseException {
        String expression = "{AGE} != date(2023, 06, 14, 9, 15, 10)";
        FilterEvaluator fe = new FilterEvaluator(expression);
        DataTable classFilter = ClassDteFilterConverter.convertExpressionToClassFilter(fe.getRootExpression());
        Assertions.assertEquals(1, classFilter.getRecordCount());
    }
}
