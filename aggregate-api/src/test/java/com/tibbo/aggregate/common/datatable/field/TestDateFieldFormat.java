package com.tibbo.aggregate.common.datatable.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.tests.*;

public class TestDateFieldFormat extends CommonsTestCase {

    public void testMethods() throws Exception {
        FieldFormat ff = FieldFormat.create("test", 'D');
        Object nd = ff.getNotNullDefault();
        String dv = "2000-02-01 12:00:00.000";

        assertEquals(ff.valueToString(nd), dv);
        assertEquals(ff.valueFromString(dv), nd);
    }
}
