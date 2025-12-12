package com.tibbo.aggregate.common.event;

import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTable;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.DataTableUtils;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.expression.EvaluationException;
import com.tibbo.aggregate.common.expression.Evaluator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

public class EventDataTableUtils {

    public static TableFormat EVENT_TABLE_FORMAT = new TableFormat(1, 1);

    public static final String VF_EVENT_TABLE_ID = "id";
    public static final String VF_EVENT_TABLE_INSTANTIATION_TIME = "instantiationtime";
    public static final String VF_EVENT_TABLE_CREATION_TIME = "creationtime";
    public static final String VF_EVENT_TABLE_EXPIRATION_TIME = "expirationtime";
    public static final String VF_EVENT_TABLE_CONTEXT = "context";
    public static final String VF_EVENT_TABLE_NAME = "name";
    public static final String VF_EVENT_TABLE_ACKNOWLEDGEMENTS_TABLE = "acknowledgementsTable";
    public static final String VF_EVENT_TABLE_DATA = "data";
    public static final String VF_EVENT_TABLE_LISTENER = "listener";
    public static final String VF_EVENT_TABLE_LEVEL = "level";
    public static final String VF_EVENT_TABLE_COUNT = "count";
    public static final String VF_EVENT_TABLE_ENRICHMENTS_TABLE = "enrichmentsTable";
    public static final String VF_EVENT_TABLE_ORIGINATOR = "originator";
    public static final String VF_EVENT_TABLE_DEDUPLICATION_ID = "deduplicationId";
    public static final String VF_EVENT_TABLE_SESSION_ID = "sessionID";

    static {
        FieldFormat FF_ID = FieldFormat.create(VF_EVENT_TABLE_ID, FieldFormat.LONG_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_ID);

        FieldFormat FF_INSTANTIATION_TIME = FieldFormat.create(VF_EVENT_TABLE_INSTANTIATION_TIME, FieldFormat.DATE_FIELD).setReadonly(true);
        EVENT_TABLE_FORMAT.addField(FF_INSTANTIATION_TIME);

        FieldFormat FF_CREATION_TIME = FieldFormat.create(VF_EVENT_TABLE_CREATION_TIME, FieldFormat.DATE_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_CREATION_TIME);

        FieldFormat FF_EXPIRATION_TIME = FieldFormat.create(VF_EVENT_TABLE_EXPIRATION_TIME, FieldFormat.DATE_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_EXPIRATION_TIME);

        FieldFormat FF_CONTEXT = FieldFormat.create(VF_EVENT_TABLE_CONTEXT, FieldFormat.STRING_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_CONTEXT);

        FieldFormat FF_NAME = FieldFormat.create(VF_EVENT_TABLE_NAME, FieldFormat.STRING_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_NAME);

        FieldFormat FF_ACKNOWLEDGEMENTS = FieldFormat.create(VF_EVENT_TABLE_ACKNOWLEDGEMENTS_TABLE, FieldFormat.DATATABLE_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_ACKNOWLEDGEMENTS);

        FieldFormat FF_DATA = FieldFormat.create(VF_EVENT_TABLE_DATA, FieldFormat.DATATABLE_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_DATA);

        FieldFormat FF_LISTENER = FieldFormat.create(VF_EVENT_TABLE_LISTENER, FieldFormat.INTEGER_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_LISTENER);

        FieldFormat FF_LEVEL = FieldFormat.create(VF_EVENT_TABLE_LEVEL, FieldFormat.INTEGER_FIELD);
        EVENT_TABLE_FORMAT.addField(FF_LEVEL);

        FieldFormat FF_COUNT = FieldFormat.create(VF_EVENT_TABLE_COUNT, FieldFormat.INTEGER_FIELD);
        EVENT_TABLE_FORMAT.addField(FF_COUNT);

        FieldFormat FF_ENRICHMENTS = FieldFormat.create(VF_EVENT_TABLE_ENRICHMENTS_TABLE, FieldFormat.DATATABLE_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_ENRICHMENTS);

        FieldFormat FF_ORIGINATOR = FieldFormat.create(VF_EVENT_TABLE_ORIGINATOR, FieldFormat.DATA_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_ORIGINATOR);

        FieldFormat FF_DEDUPLICATION_ID = FieldFormat.create(VF_EVENT_TABLE_DEDUPLICATION_ID, FieldFormat.STRING_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_DEDUPLICATION_ID);

        FieldFormat FF_SESSION_ID = FieldFormat.create(VF_EVENT_TABLE_SESSION_ID, FieldFormat.LONG_FIELD).setNullable(true);
        EVENT_TABLE_FORMAT.addField(FF_SESSION_ID);
    }

    public static Event evaluateExpression(Event ev, String expression, ContextManager contextManager, Context context) throws ContextException {
        DataTable defaultDataTable;
        try {
            defaultDataTable = DataTableConversion.beanToTable(ev, EventDataTableUtils.EVENT_TABLE_FORMAT);
            defaultDataTable.rec().setValue(VF_EVENT_TABLE_ACKNOWLEDGEMENTS_TABLE, ev.getAcknowledgementsTable());
            defaultDataTable.rec().setValue(VF_EVENT_TABLE_ENRICHMENTS_TABLE, ev.getEnrichmentsTable());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert bean to datatable", e);
        }
        Evaluator evaluator = DataTableUtils.createEvaluator(defaultDataTable, contextManager, context);
        DataTable resultDataTable;

        try {
            resultDataTable = evaluator.evaluateToDataTable(new Expression(expression));
        } catch (EvaluationException | SyntaxErrorException e) {
            throw new ContextException(e);
        }

        Event oev = DataTableConversion.beanFromTable(resultDataTable, Event.class, EventDataTableUtils.EVENT_TABLE_FORMAT, false);
        oev.setPermissions(ev.getPermissions());
        return oev;
    }
}
