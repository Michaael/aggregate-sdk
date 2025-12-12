package com.tibbo.aggregate.common.data;

import java.util.*;

import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;
import com.tibbo.aggregate.common.tests.*;

public class TestEvent extends CommonsTestCase
{
  public void testEventClone() throws Exception
  {
    Event ev = new Event("context", "name", EventLevel.INFO, new SimpleDataTable(new TableFormat(1, 1, "<str><S>"), true), 123l);
    
    ev.addAcknowledgement(new Acknowledgement("author", new Date(), "text"));
    ev.addAcknowledgement(new Acknowledgement("author2", new Date(), "text2"));
    
    ev.addEnrichment(new Enrichment("xx", "yy", new Date(), "author"));
    
    Event clone = ev.clone();
    
    assertEquals("context", clone.getContext());
    assertEquals("name", clone.getName());
    assertEquals(EventLevel.INFO, clone.getLevel());
    assertEquals("author2", clone.getAcknowledgements().get(1).getAuthor());
    assertEquals("yy", clone.getEnrichments().get(0).getValue());
  }
}
