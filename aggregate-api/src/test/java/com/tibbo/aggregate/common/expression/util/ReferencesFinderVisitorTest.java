package com.tibbo.aggregate.common.expression.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

/**
 * @author Vladimir Plizga
 * @since 05.12.2022
 */
public class ReferencesFinderVisitorTest
{
    @Test
    public void referencesAreFoundOnMultipleLines() throws SyntaxErrorException
    {
        // given
        ReferencesFinderVisitor sut = new ReferencesFinderVisitor();
        Expression expression = new Expression("{.:table$int[2]} +\n" +
                " {.:table$int[1]}");
        ASTStart cachedRootNode = ExpressionUtils.parse(expression, true);

        // when
        cachedRootNode.jjtAccept(sut, null);
        List<Reference> result = sut.getIdentifiers();

        // then
        assertEquals(2, result.size());
    }
}