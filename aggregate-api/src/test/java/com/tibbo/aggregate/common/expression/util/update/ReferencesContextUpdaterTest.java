package com.tibbo.aggregate.common.expression.util.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

/**
 * @author Vladimir Plizga
 * @since 06.12.2022
 */
public class ReferencesContextUpdaterTest
{

    @Test
    public void multipleReferencesUpdated() throws SyntaxErrorException
    {
        // given
        String oldContext = "users.admin.devices.runner";
        String newContext = "users.admin.devices.runner-clone";
        ReferencesContextUpdater sut = new ReferencesContextUpdater(oldContext, newContext);
        String sourceExpressionText =
                "  {users.admin.devices.runner:table$int[0]} + '_' \n" +
                "+ {users.admin.devices.runner_copy:table$int[1]} + '_' \n" +
                "+ {users.admin.devices.runner:table$int[2]}\n";
        Expression sourceExpression = new Expression(sourceExpressionText);

        // when
        Expression updatedExpression = sut.updateContextInExpressionReferences(sourceExpression);

        // then
        Expression expectedExpression = new Expression(
                "  {users.admin.devices.runner-clone:table$int[0]} + '_'  " +
                "+ {users.admin.devices.runner_copy:table$int[1]} + '_'  " +
                "+ {users.admin.devices.runner-clone:table$int[2]}");
        assertEquals(expectedExpression,  updatedExpression);
    }

    @Test
    public void noReferencesUpdated() throws SyntaxErrorException
    {
        // given
        String oldContext = "users.admin.devices.runner";
        String newContext = "users.admin.devices.runner-clone";
        ReferencesContextUpdater sut = new ReferencesContextUpdater(oldContext, newContext);
        String sourceExpressionText =
                "  {users.admin.devices.runner_copy:table$int[0]} + '_' \n" +
                "+ {users.admin.devices.runner_copy:table$int[1]} + '_' \n" +
                "+ {users.admin.devices.runner_copy:table$int[2]}\n";
        Expression sourceExpression = new Expression(sourceExpressionText);

        // when
        Expression updatedExpression = sut.updateContextInExpressionReferences(sourceExpression);

        // then
        assertEquals(sourceExpression,  updatedExpression);
    }

    @Test
    public void equalContextsAreProhibited()
    {
        // given
        String oldContext = "users.admin.devices.runner";
        String newContext = "users.admin.devices.runner";
        assertThrows(IllegalArgumentException.class, () -> {
            // when
            new ReferencesContextUpdater(oldContext, newContext);
        });
        // then
    }
}