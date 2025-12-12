package com.tibbo.aggregate.common.event;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.context.ContextUtils;
import com.tibbo.aggregate.common.context.Contexts;
import com.tibbo.aggregate.common.data.Event;
import com.tibbo.aggregate.common.datatable.DataTableBindingProvider;
import com.tibbo.aggregate.common.datatable.DataTableConversion;
import com.tibbo.aggregate.common.datatable.FieldFormat;
import com.tibbo.aggregate.common.datatable.SimpleDataTable;
import com.tibbo.aggregate.common.datatable.TableFormat;
import com.tibbo.aggregate.common.datatable.converter.DefaultFormatConverter;
import com.tibbo.aggregate.common.datatable.field.LongFieldFormat;
import com.tibbo.aggregate.common.datatable.field.StringFieldFormat;
import com.tibbo.aggregate.common.datatable.validator.TableKeyFieldsValidator;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.server.UtilitiesContextConstants;
import com.tibbo.aggregate.common.util.TimeHelper;

public class EventProcessingRule
{
  private static final String FIELD_MASK = "mask";
  private static final String FIELD_EVENT = "event";
  private static final String FIELD_PREFILTER = "prefilter";
  private static final String FIELD_DEDUPLICATOR = "deduplicator";
  private static final String FIELD_QUEUE = "queue";
  private static final String FIELD_DUPLICATE_DISPATCHING = "duplicateDispatching";
  private static final String FIELD_PERIOD = "period";
  private static final String FIELD_ENRICHMENTS = "enrichments";
  
  public static final TableFormat FORMAT = new TableFormat();
  static
  {
    FORMAT.addTableValidator(new TableKeyFieldsValidator());
    
    FORMAT.addField(FieldFormat.create("<" + FIELD_MASK + "><S><F=NK><D=" + Cres.get().getString("conContextMask") + "><H=" + Cres.get().getString("eventProcessingRulesContextMask") + "><E=contextmask>"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_EVENT + "><S><F=EK><V=<L=1 " + Integer.MAX_VALUE + ">><D=" + Cres.get().getString("efEventName") + "><H=" + Cres.get().getString("eventProcessingRulesEventName") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_PREFILTER + "><S><D=" + Cres.get().getString("efPrefilter") + "><H=" + Cres.get().getString("efPrefilterHelp") + "><E="
        + StringFieldFormat.EDITOR_EXPRESSION + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_DEDUPLICATOR + "><S><D=" + Cres.get().getString("efDeduplicator") + "><H=" + Cres.get().getString("eventProcessingRulesDeduplicator") + "><E=" + StringFieldFormat.EDITOR_EXPRESSION + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_QUEUE + "><I><A=100><D=" + Cres.get().getString("efMemoryQueue") + "><H=" + Cres.get().getString("eventProcessingRulesRam") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_DUPLICATE_DISPATCHING + "><B><D=" + Cres.get().getString("efDuplicateDispatching") + "><H=" + Cres.get().getString("eventProcessingRulesDeduplicationDispatching") + ">"));
    FORMAT.addField(FieldFormat.create("<" + FIELD_PERIOD + "><L><A=" + Event.DEFAULT_EVENT_EXPIRATION_PERIOD + "><D=" + Cres.get().getString("confExpirationPeriod") + "><H=" + Cres.get().getString("eventProcessingRulesExpPeriod") + "><E="
        + LongFieldFormat.EDITOR_PERIOD + "><O=" + LongFieldFormat.encodePeriodEditorOptions(TimeHelper.HOUR, TimeHelper.YEAR) + ">"));
    
    FieldFormat ff = FieldFormat.create("<" + FIELD_ENRICHMENTS + "><T><D=" + Cres.get().getString("enrichments") + "><H=" + Cres.get().getString("eventProcessingRulesEnrichments") + ">");
    ff.setDefault(new SimpleDataTable(EventEnrichmentRule.FORMAT));
    FORMAT.addField(ff);
    
    String ref = FIELD_EVENT + "#" + DataTableBindingProvider.PROPERTY_CHOICES;
    String exp = "{" + Contexts.CTX_UTILITIES + ":" + UtilitiesContextConstants.F_EVENTS_BY_MASK + "('{" + FIELD_MASK + "}')}";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_PREFILTER + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
    exp = DefaultFunctions.EXPRESSION_EDITOR_OPTIONS + "({" + FIELD_MASK + "}, {" + FIELD_EVENT + "}, " + ContextUtils.ENTITY_EVENT + ")";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_DEDUPLICATOR + "#" + DataTableBindingProvider.PROPERTY_OPTIONS;
    exp = DefaultFunctions.EXPRESSION_EDITOR_OPTIONS + "({" + FIELD_MASK + "}, {" + FIELD_EVENT + "}, " + ContextUtils.ENTITY_EVENT + ")";
    FORMAT.addBinding(ref, exp);
    
    ref = FIELD_QUEUE + "#" + DataTableBindingProvider.PROPERTY_ENABLED;
    exp = "length({" + FIELD_DEDUPLICATOR + "}) > 0";
    FORMAT.addBinding(ref, exp);
    
    FORMAT.setReorderable(true);
    
    DataTableConversion.registerFormatConverter(new DefaultFormatConverter(EventProcessingRule.class, FORMAT));
  }
  
  private String mask;
  private String event;
  private String prefilter = new String();
  private String deduplicator = new String();
  private int queue;
  private boolean duplicateDispatching;
  private long period; // Ms
  private List<EventEnrichmentRule> enrichments = new LinkedList();
  
  // State
  private Expression prefilterExpression;
  private Expression deduplicatorExpression;
  
  // Statistics
  private long filtered;
  private long saved;
  private long duplicates;
  
  public EventProcessingRule()
  {
    super();
  }
  
  public EventProcessingRule(String mask, String event)
  {
    super();
    this.mask = mask;
    this.event = event;
  }
  
  public String getEvent()
  {
    return event;
  }
  
  public String getMask()
  {
    return mask;
  }
  
  public long getPeriod()
  {
    return period;
  }
  
  public void setEvent(String event)
  {
    this.event = event;
  }
  
  public void setMask(String mask)
  {
    this.mask = mask;
  }
  
  public void setPeriod(long period)
  {
    this.period = period;
  }
  
  public String getPrefilter()
  {
    return prefilter;
  }
  
  public void setPrefilter(String prefilter)
  {
    this.prefilter = prefilter;
    
    prefilterExpression = null;
  }
  
  public Expression getPrefilterExpression()
  {
    if (prefilterExpression == null)
    {
      prefilterExpression = (prefilter != null && prefilter.length() > 0) ? new Expression(prefilter) : null;
    }
    
    return prefilterExpression;
  }
  
  public String getDeduplicator()
  {
    return deduplicator;
  }
  
  public void setDeduplicator(String deduplicator)
  {
    this.deduplicator = deduplicator;
    
    deduplicatorExpression = null;
  }
  
  public Expression getDeduplicatorExpression()
  {
    if (deduplicatorExpression == null)
    {
      deduplicatorExpression = (deduplicator != null && deduplicator.length() > 0) ? new Expression(deduplicator) : null;
    }
    
    return deduplicatorExpression;
  }
  
  public List<EventEnrichmentRule> getEnrichments()
  {
    return enrichments;
  }
  
  public void setEnrichments(List<EventEnrichmentRule> enrichments)
  {
    this.enrichments = enrichments;
  }
  
  public int getQueue()
  {
    return queue;
  }
  
  public void setQueue(int queue)
  {
    this.queue = queue;
  }
  
  public boolean isDuplicateDispatching()
  {
    return duplicateDispatching;
  }
  
  public void setDuplicateDispatching(boolean duplicateDispatching)
  {
    this.duplicateDispatching = duplicateDispatching;
  }
  
  public void addFiltered()
  {
    filtered++;
  }
  
  public void addSaved()
  {
    saved++;
  }
  
  public void addDuplicate()
  {
    duplicates++;
  }
  
  public long getFiltered()
  {
    return filtered;
  }
  
  public long getSaved()
  {
    return saved;
  }
  
  public long getDuplicates()
  {
    return duplicates;
  }
  
  @Override
  public String toString()
  {
    return "EventProcessingRule [event=" + event + ", mask=" + mask + "]";
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((event == null) ? 0 : event.hashCode());
    result = prime * result + ((mask == null) ? 0 : mask.hashCode());
    return result;
  }

  /**
   * Compares two objects based on unique elements, event and mask, for equality.
   *
   * @param o Object to compare.
   * @return true if the objects are equal based on event and mask, false otherwise.
   */

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventProcessingRule that = (EventProcessingRule) o;
    return Objects.equals(mask, that.mask)
            && Objects.equals(event, that.event);
  }

  /**
   * Compares two objects with a comparison of all their elements.
   *
   * @param o Object to compare.
   * @return true if the objects are equal, including all their elements, false otherwise.
   */
  public boolean deepEquals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EventProcessingRule that = (EventProcessingRule) o;
    return queue == that.queue
            && duplicateDispatching == that.duplicateDispatching
            && period == that.period
            && filtered == that.filtered
            && saved == that.saved
            && duplicates == that.duplicates
            && Objects.equals(mask, that.mask)
            && Objects.equals(event, that.event)
            && Objects.equals(prefilter, that.prefilter)
            && Objects.equals(deduplicator, that.deduplicator)
            && Objects.equals(enrichments, that.enrichments)
            && Objects.equals(prefilterExpression, that.prefilterExpression)
            && Objects.equals(deduplicatorExpression, that.deduplicatorExpression);
  }
}
