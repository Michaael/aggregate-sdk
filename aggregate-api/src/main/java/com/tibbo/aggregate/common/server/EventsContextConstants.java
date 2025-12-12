package com.tibbo.aggregate.common.server;

import com.tibbo.aggregate.common.*;
import com.tibbo.aggregate.common.datatable.*;
import com.tibbo.aggregate.common.event.*;

public interface EventsContextConstants
{
  
  public static final String F_ACKNOWLEDGE = "acknowledge";
  public static final String F_ENRICH = "enrich";
  public static final String F_DELETE = "delete";
  public static final String F_INIT_HISTORY = "initHistory";
  public static final String F_GET_HISTORY = "getHistory";
  public static final String F_SORT_HISTORY = "sortHistory";
  public static final String F_GET_CURRENT = "getCurrent";
  public static final String F_MASS_DELETE = "massDelete";
  public static final String F_REMOVE_EXPIRED = "removeExpired";
  public static final String F_GET = "get";
  public static final String F_GET_BY_ID = "getById";
  public static final String F_FIRE_EVENT = "fireEvent";
  public static final String F_UPDATE = "update";
  
  public static final String E_ACKNOWLEDGEMENT = "acknowledgement";
  public static final String E_ENRICHMENT = "enrichment";
  public static final String E_REMOVAL = "removal";
  
  public static final String A_REMOVE_EXPIRED = "removeExpired";
  public static final String A_ACKNOWLEDGE_ALERT = "acknowledgeAlert";
  public static final String A_ACKNOWLEDGE_EVENT = "acknowledgeEvent";
  
  public static final String FIF_SOFT_HISTORY_LISTENER = "listener";
  public static final String FIF_SOFT_HISTORY_FIELD = "field";
  public static final String FIF_SOFT_HISTORY_DATATABLEFIELD = "datatablefield";
  public static final String FIF_SOFT_HISTORY_ASCENDING = "ascending";
  
  public static final String FIF_GET_HISTORY_LISTENER = "listener";
  public static final String FIF_GET_HISTORY_FIRST = "first";
  public static final String FIF_GET_HISTORY_NUMBER = "number";
  public static final String FIF_GET_HISTORY_SMART_FILTER_EXPRESSION = "smartFilterExpression";
  
  public static final String FIF_GET_BY_ID_CONTEXT = "context";
  public static final String FIF_GET_BY_ID_EVENT = "event";
  public static final String FIF_GET_BY_ID_ID = "id";
  public static final String FIF_GET_BY_ID_DATA_AS_TABLE = "dataAsTable";
  
  public static final String FIF_INIT_HISTORY_LISTENER = "listener";
  public static final String FIF_INIT_HISTORY_EVENTLIST = "eventlist";
  public static final String FIF_INIT_HISTORY_START_TIME = "startTime";
  public static final String FIF_INIT_HISTORY_END_TIME = "endTime";
  public static final String FIF_INIT_HISTORY_MAX_RESULTS = "maxResults";
  public static final String FIF_INIT_HISTORY_SMART_FILTER_EXPRESSION = "smartFilterExpression";


  public static final String FIF_ACKNOWLEDGE_CONTEXT = "context";
  public static final String FIF_ACKNOWLEDGE_NAME = "name";
  public static final String FIF_ACKNOWLEDGE_ID = "id";
  public static final String FIF_ACKNOWLEDGE_ACK = "ack";
  public static final String FIF_ACKNOWLEDGE_AUTHOR = "author";
  
  public static final String FIF_ENRICH_CONTEXT = "context";
  public static final String FIF_ENRICH_NAME = "name";
  public static final String FIF_ENRICH_ID = "id";
  public static final String FIF_ENRICH_ENRICHMENT_NAME = "enrichmentName";
  public static final String FIF_ENRICH_ENRICHMENT_VALUE = "enrichmentValue";

  public static final String FIF_UPDATE_CONTEXT = "context";
  public static final String FIF_UPDATE_NAME = "name";
  public static final String FIF_UPDATE_ID = "id";
  public static final String FIF_UPDATE_EXPRESSION = "expression";
  
  public static final String FIF_DELETE_CONTEXT = "context";
  public static final String FIF_DELETE_NAME = "name";
  public static final String FIF_DELETE_ID = "id";
  
  public static final String FIF_MASS_DELETE_MASK = "mask";
  public static final String FIF_MASS_DELETE_EVENT = "event";
  public static final String FIF_MASS_DELETE_START_DATE = "startDate";
  public static final String FIF_MASS_DELETE_END_DATE = "endDate";
  
  public static final String FIF_GET_MASK = "mask";
  public static final String FIF_GET_EVENT = "event";
  public static final String FIF_GET_FILTER = "filter";
  public static final String FIF_GET_FROM_DATE = "fromDate";
  public static final String FIF_GET_TO_DATE = "toDate";
  public static final String FIF_GET_DATA_AS_TABLE = "dataAsTable";
  public static final String FIF_GET_SORT_FIELD = "sortField";
  public static final String FIF_GET_SORT_ORDER = "sortOrder";
  public static final String FIF_GET_LIMIT = "limit";
  public static final String FIF_GET_ADDITIONAL_CRITERIA = "additionalCriteria";
  
  public static final String FOF_INIT_HISTORY_FOUND = "found";
  public static final String FOF_INIT_HISTORY_SEVERITY = "severity";
  public static final String FOF_INIT_HISTORY_ERROR_CODE = "errorCode";
  
  public static final String FOF_GET_HISTORY_DATA = "data";
  public static final String FOF_GET_HISTORY_ACKNOWLEDGEMENTS = "acknowledgements";
  public static final String FOF_GET_HISTORY_ENRICHMENTS = "enrichments";
  public static final String FOF_GET_HISTORY_LEVEL = "level";
  public static final String FOF_GET_HISTORY_COUNT = "count";
  public static final String FOF_GET_HISTORY_NAME = "name";
  public static final String FOF_GET_HISTORY_CONTEXT = "context";
  public static final String FOF_GET_HISTORY_CREATIONTIME = "creationtime";
  public static final String FOF_GET_HISTORY_ID = "id";
  public static final String FOF_GET_HISTORY_INDEX = "index";
  
  public static final String EF_ACKNOWLEDGEMENT_EVENT = "event";
  public static final String EF_ENRICHMENT_EVENT = "event";
  public static final String EF_REMOVAL_EVENT = "event";
  
  public static final String FIF_FIRE_EVENT_CONTEXT = "context";
  public static final String FIF_FIRE_EVENT_EVENT = "event";
  public static final String FIF_FIRE_EVENT_DATA = "data";
  
  public static final Integer ERROR_LOW_MEMORY = -1;

  public static final String FIELD_E_ID = "eId";
  public static final String FIELD_E_CREATIONTIME = "eCreationtime";
  public static final String FIELD_E_EXPIRATIONTIME = "eExpirationtime";
  public static final String FIELD_E_CONTEXT = "eContext";
  public static final String FIELD_E_NAME = "eName";
  public static final String FIELD_E_LEVEL = "eLevel";
  public static final String FIELD_E_COUNT = "eCount";
  public static final String FIELD_E_ACKNOWLEDGEMENTS = "eAcknowledgements";
  public static final String FIELD_E_ENRICHMENTS = "eEnrichments";
  public static final String FIELD_E_DATA = "eData";

  static TableFormat buildGetEventsBaseFormat()
  {
    TableFormat rf = new TableFormat();
    rf.addField(FieldFormat.create("<" + FIELD_E_ID + "><L><D=" + Cres.get().getString("evtEventID") + ">"));
    rf.addField(FieldFormat.create("<" + FIELD_E_CREATIONTIME + "><D><D=" + Cres.get().getString("evtCreationTime") + ">"));
    rf.addField(FieldFormat.create("<" + FIELD_E_EXPIRATIONTIME + "><D><F=N><D=" + Cres.get().getString("evtExpirationTime") + ">"));
    rf.addField(FieldFormat.create("<" + FIELD_E_CONTEXT + "><S><D=" + Cres.get().getString("context") + ">"));
    rf.addField(FieldFormat.create("<" + FIELD_E_NAME + "><S><D=" + Cres.get().getString("name") + ">"));

    FieldFormat ff = FieldFormat.create("<" + FIELD_E_LEVEL + "><I><D=" + Cres.get().getString("level") + ">");
    ff.setSelectionValues(EventLevel.getSelectionValues());
    rf.addField(ff);

    rf.addField(FieldFormat.create("<" + FIELD_E_COUNT + "><I><D=" + Cres.get().getString("number") + ">"));

    rf.addField(FieldFormat.create("<" + FIELD_E_ACKNOWLEDGEMENTS + "><T><D=" + Cres.get().getString("acknowledgements") + ">"));

    rf.addField(FieldFormat.create("<" + FIELD_E_ENRICHMENTS + "><T><D=" + Cres.get().getString("enrichments") + ">"));

    return rf;
  }
}
