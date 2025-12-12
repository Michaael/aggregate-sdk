package com.tibbo.aggregate.common.structure;

import static com.tibbo.aggregate.common.structure.OriginKind.REFERENCE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import com.tibbo.aggregate.common.expression.Reference;

class PinpointTest
{

  @Test
  @DisplayName("Upon creation, pinpoint has default (empty) fields")
  void emptinessOnBirth()
  {
    // given
    Pinpoint pinpoint = new Pinpoint(new Reference());

    // when

    // then
    assertAll("Just created pinpoint",
        () -> assertNull(pinpoint.getOriginKind()),
        () -> assertNull(pinpoint.getScope()),
        () -> assertTrue(pinpoint.getCallLocations().isEmpty()),
        () -> assertNotNull(pinpoint.getOrigin()),
        () -> assertTrue(pinpoint.getOrigin().getImage().isEmpty())
    );
  }

  @Test
  void withOriginRow()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference());
    int row = 5;

    // when
    Pinpoint pinpoint2 = pinpoint1.withOriginRow(row);

    // then
    assertNotEquals(pinpoint1, pinpoint2);
    assertNull(pinpoint1.getOrigin().getRow());
    assertEquals(row, pinpoint2.getOrigin().getRow());
  }

  @Test
  void withOriginField()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference());
    String field = "someField";

    // when
    Pinpoint pinpoint2 = pinpoint1.withOriginField(field);

    // then
    assertNotEquals(pinpoint1, pinpoint2);
    assertNull(pinpoint1.getOrigin().getField());
    assertEquals(field, pinpoint2.getOrigin().getField());
  }

  @Test
  void withOriginFieldAndKind()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference());
    String field = "someField";
    OriginKind kind = REFERENCE;

    // when
    Pinpoint pinpoint2 = pinpoint1.withOriginField(field, kind);

    // then
    assertNotEquals(pinpoint1, pinpoint2);
    assertNull(pinpoint1.getOrigin().getField());
    assertEquals(field, pinpoint2.getOrigin().getField());
    assertNull(pinpoint1.getOriginKind());
    assertEquals(kind, pinpoint2.getOriginKind());
  }

  @Test
  void withScope()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference());
    String scope = "someScope";

    // when
    Pinpoint pinpoint2 = pinpoint1.withScope(scope);

    // then
    assertNotEquals(pinpoint1, pinpoint2);
    assertNull(pinpoint1.getScope());
    assertEquals(scope, pinpoint2.getScope());
  }

  @Test
  void withNestedCell()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference());
    String outerField = "outerField";
    int outerRow = 2;
    String innerField = "innerField";
    int innerRow = 3;
    OriginKind kind = REFERENCE;

    // when
    Pinpoint pinpoint2 = pinpoint1.withOriginField(outerField)
        .withOriginRow(outerRow)
        .withNestedCell(innerField, innerRow, kind);

    // then
    assertNotEquals(pinpoint1, pinpoint2);
    Reference newOrigin = pinpoint2.getOrigin();
    assertEquals(outerField, newOrigin.getField());
    assertEquals(outerRow, newOrigin.getRow());
    assertEquals(2, newOrigin.getFields().size());
    assertEquals(innerField, newOrigin.getFields().get(1).getFirst());
    assertEquals(innerRow, newOrigin.getFields().get(1).getSecond());
    assertEquals(kind, pinpoint2.getOriginKind());
  }

  @Test
  @DisplayName("Copied pinpoint contains exactly the same data as the original one but it's not the same object")
  void copy()
  {
    // given
    Pinpoint pinpoint1 = new Pinpoint(new Reference("users.admin.models.visModel:ruleSets$rules[1].condition[1]"))
        .withScope("someScope")
        .withOriginField("outerField")
        .withOriginRow(1)
        .withNestedCell("innerField", 2, REFERENCE);
    pinpoint1.pushLocation(new CallLocation(CallKind.FUNCTION, 3, 5, "someText"));
    pinpoint1.pushLocation(new CallLocation(CallKind.REFERENCE, 7, 9, "someOtherText"));

    // when
    Pinpoint pinpoint2 = pinpoint1.copy();

    // then
    assertNotSame(pinpoint1, pinpoint2);
    assertEquals(pinpoint1, pinpoint2);
  }

  @Test
  void pushPopLocation()
  {
    // given
    Pinpoint pinpoint = new Pinpoint(new Reference());
    CallLocation location = new CallLocation(CallKind.FUNCTION, 1, 2, "callText");

    // when 1
    pinpoint.pushLocation(location);
    // then 1
    assertEquals(1, pinpoint.getCallLocations().size());
    assertEquals(location, pinpoint.getCallLocations().getLast());

    // when 2
    pinpoint.popLocation();
    // then 2
    assertTrue(pinpoint.getCallLocations().isEmpty());

    // when 3
    Executable pop = pinpoint::popLocation;
    // then 3
    assertThrows(NoSuchElementException.class, pop);
  }


}