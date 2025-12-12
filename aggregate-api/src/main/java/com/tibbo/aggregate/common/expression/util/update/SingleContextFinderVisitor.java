package com.tibbo.aggregate.common.expression.util.update;

import com.tibbo.aggregate.common.expression.AttributedObject;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.Reference;
import com.tibbo.aggregate.common.expression.parser.ASTValueReferenceNode;
import com.tibbo.aggregate.common.expression.util.ReferencesFinderVisitor;

/**
 *
 * @author Vladimir Plizga
 * @since 05.12.2022
 */
class SingleContextFinderVisitor extends ReferencesFinderVisitor
{
    private final String expressionText;
    private final String oldContext;
    private final String newContext;

    SingleContextFinderVisitor(String expressionText,
                               String oldContext,
                               String newContext)
    {
        this.expressionText = expressionText;
        this.oldContext = oldContext;
        this.newContext = newContext;
    }

    @Override
    public AttributedObject visit(ASTValueReferenceNode node, EvaluationEnvironment data)
    {
        Reference currentReference = new Reference(node.uriImage);
        String currentContext = currentReference.getContext();

        if (!oldContext.equals(currentContext))
        {
            return null;        // just continue traversing
        }

        int beginLine = node.jjtGetFirstToken().beginLine;
        int beginColumn = node.jjtGetFirstToken().beginColumn;
        int endLine = node.jjtGetLastToken().endLine;
        int endColumn = node.jjtGetLastToken().endColumn;

        if (beginLine != endLine || beginLine != 1)     // multiline references are not supported so far
        {
            throw new IllegalArgumentException(String.format("Incorrect reference format (beginLine is %d " +
                    "and endLine is %d): %s", beginLine, endLine, node.uriImage));
        }

        currentReference.setContext(newContext);        // this resets the image in the reference
        String updatedReference = currentReference.getImage();      // this recreates the image

        String sourceReference = expressionText.substring(beginColumn - 1, endColumn);
        // the following replacement may produce multiple changes if the expression contains several equal references
        String updatedExpression = expressionText.replace(sourceReference, updatedReference);

        // the following is the only way to make the visitor stop traversing the AST
        throw new SinglePassFinishedException(updatedExpression);
    }

    static class SinglePassFinishedException extends RuntimeException
    {
        private final String updatedExpression;

        SinglePassFinishedException(String updatedExpression)
        {
            this.updatedExpression = updatedExpression;
        }

        String getUpdatedExpression()
        {
            return updatedExpression;
        }
    }
}
