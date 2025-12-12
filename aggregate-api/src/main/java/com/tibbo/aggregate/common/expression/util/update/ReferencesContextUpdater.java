package com.tibbo.aggregate.common.expression.util.update;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;
import com.tibbo.aggregate.common.Log;
import com.tibbo.aggregate.common.expression.Expression;
import com.tibbo.aggregate.common.expression.ExpressionUtils;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.util.StringUtils;
import com.tibbo.aggregate.common.util.SyntaxErrorException;

/**
 * @author Vladimir Plizga
 * @since 06.12.2022
 */
public class ReferencesContextUpdater
{
    private final String oldContext;
    private final String newContext;

    public ReferencesContextUpdater(String oldContext, String newContext)
    {
        Preconditions.checkArgument(oldContext != null, "Old context must not be null");
        Preconditions.checkArgument(newContext != null, "New context must not be null");
        // The following check defends not only from useless work but also from endless looping in the search logic
        Preconditions.checkArgument(!oldContext.equals(newContext), "New context must not be equal " +
                "to the old one: %s", oldContext);

        this.oldContext = oldContext;
        this.newContext = newContext;
    }

    public Expression updateContextInExpressionReferences(@Nonnull Expression sourceExpression) throws SyntaxErrorException
    {
        if (StringUtils.isEmpty(sourceExpression.getText()))
        {
            return sourceExpression;
        }

        String[] sourceExpressionLines = sourceExpression.getText().split("\n\r?");

        String updatedExpressionText;       // Internal expression representation where the changes are actually made
        Expression updatedExpression;       // An expression instance to internally update and then (eventually) return

        if (sourceExpressionLines.length == 1)      // the expression is single-line already
        {
            updatedExpressionText = sourceExpression.getText();
            // The following lets us re-use the astRoot if the expression has been parsed before
            updatedExpression = sourceExpression;
        }
        else
        {
            // remove all the line breaks to simplify further locating of replaceable substrings
            updatedExpressionText = String.join(" ", sourceExpressionLines);
            updatedExpression = new Expression(updatedExpressionText);
        }

        boolean madeAnyChanges = false;

        while (true)
        {
            ASTStart astRoot;
            if (updatedExpression.getRootNode() == null)
            {
                // Here we can produce a race between threads trying to parse the same expression, but it's
                // considered non-critical as the result for all threads will be the same and no blocking occurs.
                astRoot = ExpressionUtils.parse(updatedExpression, true);
                updatedExpression.setRootNode(astRoot);     // to allow Evaluator skip parsing of this expression again
            }
            else
            {
                astRoot = updatedExpression.getRootNode();  // re-use previously built AST
            }

            SingleContextFinderVisitor visitor = new SingleContextFinderVisitor(
                    updatedExpressionText,
                    oldContext,
                    newContext);
            try
            {
                astRoot.jjtAccept(visitor, null);
                // Coming here means that the visitor hasn't found any references to needed context. It can be either
                // because there was no such references initially or because all of them have been successfully updated.
                break;
            }
            catch (SingleContextFinderVisitor.SinglePassFinishedException e)
            {
                if (Log.EXPRESSIONS.isDebugEnabled())
                {
                    Log.EXPRESSIONS.debug(String.format("Updating expression:\n%s\nto:\n%s", updatedExpressionText,
                            e.getUpdatedExpression()));
                }
                updatedExpressionText = e.getUpdatedExpression();
                updatedExpression = new Expression(updatedExpressionText);
                madeAnyChanges = true;
            }
        }
        // check for changes to prevent source expression 'spoiling' with line breaks removal
        return madeAnyChanges
                ? updatedExpression
                : sourceExpression;
    }
}
