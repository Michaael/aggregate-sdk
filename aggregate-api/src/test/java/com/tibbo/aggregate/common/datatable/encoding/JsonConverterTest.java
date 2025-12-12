package com.tibbo.aggregate.common.datatable.encoding;

import com.tibbo.aggregate.common.context.*;
import com.tibbo.aggregate.common.datatable.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonConverterTest
{

  private final JsonConverter jsonConverter;

  public JsonConverterTest()
  {
    jsonConverter = new JsonConverter();
  }

  @Test
  public void testJsonConverter() throws ContextException
  {
    TableFormat nullableFormat = new TableFormat();
    nullableFormat.addField(FieldFormat.create("test", FieldFormat.DATATABLE_FIELD, null, null, true));

    TableFormat nonNullFormat = new TableFormat();
    nonNullFormat.addField(FieldFormat.create("test", FieldFormat.DATATABLE_FIELD, null, new SimpleDataTable(), false));

    assertJsonDataTable("test=aaa=111, bbb=222", "[{\"test\":[{\"aaa\":\"111\", \"bbb\":\"222\"}]}]", nullableFormat);
    assertJsonDataTable("test=aaa=111, bbb=222", "[{\"test\":[{\"aaa\":\"111\", \"bbb\":\"222\"}]}]", nonNullFormat);
  }

  private void assertJsonDataTable(String expected, String payload, TableFormat format) throws ContextException
  {
    DataTable dataTable = jsonConverter.createDataTable(format, payload);
    assertEquals(expected, dataTable.toString());
  }
}