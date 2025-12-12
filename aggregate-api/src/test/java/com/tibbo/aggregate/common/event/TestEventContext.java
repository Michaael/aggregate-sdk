package com.tibbo.aggregate.common.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import com.tibbo.aggregate.common.context.AbstractContext;
import com.tibbo.aggregate.common.context.Context;
import com.tibbo.aggregate.common.context.ContextException;
import com.tibbo.aggregate.common.context.ContextManager;
import com.tibbo.aggregate.common.data.Event;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TestEventContext {

    @Test
    public void testChangeSimpleField() throws ContextException {
        Context c = Mockito.mock(AbstractContext.class);
        ContextManager cm = Mockito.mock(ContextManager.class);

        Event ev = new Event();
        ev.setCount(5);
        ev.setLevel(7);
        String expression = "set(dt(),\"count\",0,6)";
        Event rev = EventDataTableUtils.evaluateExpression(ev, expression, cm, c);
        assertEquals(rev.getCount(), 6);
    }

    @Test
    public void testUntouchedAcknowledgement() throws ContextException {
        Context c = Mockito.mock(AbstractContext.class);
        ContextManager cm = Mockito.mock(ContextManager.class);

        Event ev = new Event();
        ev.getAcknowledgements().add(new Acknowledgement("autor1", new Date(), "text1"));
        ev.getAcknowledgements().add(new Acknowledgement("autor2", new Date(), "text2"));
        Event rev = EventDataTableUtils.evaluateExpression(ev, "set(dt(), \"count\",0,6)", cm, c);
        assertEquals(ev.getAcknowledgements().get(0).getAuthor(), rev.getAcknowledgements().get(0).getAuthor());
        assertEquals(ev.getAcknowledgements().get(0).getTime(), rev.getAcknowledgements().get(0).getTime());
        assertEquals(ev.getAcknowledgements().get(0).getText(), rev.getAcknowledgements().get(0).getText());

        assertEquals(ev.getAcknowledgements().get(1).getAuthor(), rev.getAcknowledgements().get(1).getAuthor());
        assertEquals(ev.getAcknowledgements().get(1).getTime(), rev.getAcknowledgements().get(1).getTime());
        assertEquals(ev.getAcknowledgements().get(1).getText(), rev.getAcknowledgements().get(1).getText());
    }

    @Test
    public void testUntouchedEnrichments() throws ContextException {
        Context c = Mockito.mock(AbstractContext.class);
        ContextManager cm = Mockito.mock(ContextManager.class);

        Event ev = new Event();
        ev.getEnrichments().add(new Enrichment("name1", "value1", new Date(), "author1"));
        ev.getEnrichments().add(new Enrichment("name2", "value2", new Date(), "author2"));
        Event rev = EventDataTableUtils.evaluateExpression(ev, "set(dt(), \"count\",0,6)", cm, c);

        assertEquals(ev.getEnrichments().get(0).getName(), rev.getEnrichments().get(0).getName());
        assertEquals(ev.getEnrichments().get(0).getValue(), rev.getEnrichments().get(0).getValue());
        assertEquals(ev.getEnrichments().get(0).getDate(), rev.getEnrichments().get(0).getDate());
        assertEquals(ev.getEnrichments().get(0).getAuthor(), rev.getEnrichments().get(0).getAuthor());

        assertEquals(ev.getEnrichments().get(1).getName(), rev.getEnrichments().get(1).getName());
        assertEquals(ev.getEnrichments().get(1).getValue(), rev.getEnrichments().get(1).getValue());
        assertEquals(ev.getEnrichments().get(1).getDate(), rev.getEnrichments().get(1).getDate());
        assertEquals(ev.getEnrichments().get(1).getAuthor(), rev.getEnrichments().get(1).getAuthor());
    }


}
