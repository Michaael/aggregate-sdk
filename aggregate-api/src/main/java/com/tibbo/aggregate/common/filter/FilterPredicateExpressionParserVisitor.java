package com.tibbo.aggregate.common.filter;

import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.BEGINS_WITH;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.CONTAINS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.END_WITH;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.IN;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.IS_NOT_NULL;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.IS_NULL;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.LAST_X_DAYS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.LAST_X_HOURS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.LAST_X_MONTHS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.LAST_X_WEEKS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.LAST_X_YEARS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.NEXT_X_DAYS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.NEXT_X_HOURS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.NEXT_X_MONTHS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.NEXT_X_WEEKS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.NEXT_X_YEARS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.ON;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.ON_OR_AFTER;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.ON_OR_BEFORE;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.SIMPLE_CONTAINS;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.THIS_HOUR;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.THIS_MONTH;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.THIS_WEEK;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.THIS_YEAR;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.TODAY;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.TOMORROW;
import static com.tibbo.aggregate.common.filter.FunctionOperation.FilterFunctions.YESTERDAY;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.tibbo.aggregate.common.Cres;
import com.tibbo.aggregate.common.expression.AttributedObject;
import com.tibbo.aggregate.common.expression.EvaluationEnvironment;
import com.tibbo.aggregate.common.expression.function.DefaultFunctions;
import com.tibbo.aggregate.common.expression.parser.ASTAddNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseAndNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseNotNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseOrNode;
import com.tibbo.aggregate.common.expression.parser.ASTBitwiseXorNode;
import com.tibbo.aggregate.common.expression.parser.ASTConditionalNode;
import com.tibbo.aggregate.common.expression.parser.ASTDivNode;
import com.tibbo.aggregate.common.expression.parser.ASTEQNode;
import com.tibbo.aggregate.common.expression.parser.ASTFalseNode;
import com.tibbo.aggregate.common.expression.parser.ASTFloatConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTFunctionNode;
import com.tibbo.aggregate.common.expression.parser.ASTGENode;
import com.tibbo.aggregate.common.expression.parser.ASTGTNode;
import com.tibbo.aggregate.common.expression.parser.ASTLENode;
import com.tibbo.aggregate.common.expression.parser.ASTLTNode;
import com.tibbo.aggregate.common.expression.parser.ASTLeftShiftNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalAndNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalNotNode;
import com.tibbo.aggregate.common.expression.parser.ASTLogicalOrNode;
import com.tibbo.aggregate.common.expression.parser.ASTLongConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTModNode;
import com.tibbo.aggregate.common.expression.parser.ASTMulNode;
import com.tibbo.aggregate.common.expression.parser.ASTNENode;
import com.tibbo.aggregate.common.expression.parser.ASTNullNode;
import com.tibbo.aggregate.common.expression.parser.ASTRegexMatchNode;
import com.tibbo.aggregate.common.expression.parser.ASTRightShiftNode;
import com.tibbo.aggregate.common.expression.parser.ASTStart;
import com.tibbo.aggregate.common.expression.parser.ASTStringConstNode;
import com.tibbo.aggregate.common.expression.parser.ASTSubtractNode;
import com.tibbo.aggregate.common.expression.parser.ASTTrueNode;
import com.tibbo.aggregate.common.expression.parser.ASTUnaryNode;
import com.tibbo.aggregate.common.expression.parser.ASTUnsignedRightShiftNode;
import com.tibbo.aggregate.common.expression.parser.ASTValueReferenceNode;
import com.tibbo.aggregate.common.expression.parser.ExpressionParserVisitor;
import com.tibbo.aggregate.common.expression.parser.SimpleNode;

public class FilterPredicateExpressionParserVisitor implements ExpressionParserVisitor {

    public static int STACK_SIZE = 100;
    private Expression[] stack = new Expression[STACK_SIZE];
    int sp = -1;
    private static final Map<String, Class<? extends FunctionOperation>> filterFunctions = new HashMap<>();
    private static final Map<String, DefaultFunctions> funcDefs = new HashMap<>();

    static {
        registerFunction(CONTAINS, ContainsFunctionOperation.class);
        registerFunction(BEGINS_WITH, BeginWithFunctionOperation.class);
        registerFunction(END_WITH, EndsWithFunctionOperation.class);
        registerFunction(IN, InFunctionOperation.class);
        registerFunction(ON, OnFunctionOperation.class);
        registerFunction(ON_OR_AFTER, OnAfterFunctionOperation.class);
        registerFunction(ON_OR_BEFORE, OnBeforeFunctionOperation.class);
        registerFunction(YESTERDAY, YesterdayFunctionOperation.class);
        registerFunction(TODAY, TodayFunctionOperation.class);
        registerFunction(TOMORROW, TomorrowFunctionOperation.class);
        registerFunction(THIS_HOUR, ThisHourFunctionOperation.class);
        registerFunction(THIS_WEEK, ThisWeekFunctionOperation.class);
        registerFunction(THIS_MONTH, ThisMonthFunctionOperation.class);
        registerFunction(THIS_YEAR, ThisYearFunctionOperation.class);
        registerFunction(LAST_X_HOURS, LastHoursFunctionOperation.class);
        registerFunction(NEXT_X_HOURS, NextHoursFunctionOperation.class);
        registerFunction(LAST_X_DAYS, LastDaysFunctionOperation.class);
        registerFunction(NEXT_X_DAYS, NextDaysFunctionOperation.class);
        registerFunction(LAST_X_WEEKS, LastWeeksFunctionOperation.class);
        registerFunction(NEXT_X_WEEKS, NextWeeksFunctionOperation.class);
        registerFunction(LAST_X_MONTHS, LastMonthsFunctionOperation.class);
        registerFunction(NEXT_X_MONTHS, NextMonthsFunctionOperation.class);
        registerFunction(LAST_X_YEARS, LastYearsFunctionOperation.class);
        registerFunction(NEXT_X_YEARS, NextYearsFunctionOperation.class);
        registerFunction(IS_NULL, IsNullFunctionOperation.class);
        registerFunction(IS_NOT_NULL, IsNotNullFunctionOperation.class);

        registerWrappedFunction(DefaultFunctions.DATE);
        registerWrappedFunction(DefaultFunctions.RECORDS);
        registerWrappedFunction(DefaultFunctions.ENCODE);
        registerFunction(SIMPLE_CONTAINS, ContainsExFunctionOperation.class);
    }

    private static void registerFunction(FunctionOperation.FilterFunctions def, Class<? extends FunctionOperation> funcClass) {
        filterFunctions.put(def.getName(), funcClass);
    }

    private static void registerWrappedFunction(DefaultFunctions defaultFunction) {
        funcDefs.put(defaultFunction.getName(), defaultFunction);
    }

    public Expression getRootExpression() {
        if (sp != 0) {
            throw new IllegalStateException("Stack pointer does not point to root expression");
        }
        return stack[sp];
    }

    @Override
    public AttributedObject visit(SimpleNode node, EvaluationEnvironment data) {
        return null;
    }

    @Override
    public AttributedObject visit(ASTStart node, EvaluationEnvironment data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        return null;
    }

    @Override
    public AttributedObject visit(ASTConditionalNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTLogicalOrNode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, LogicalOrOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTLogicalAndNode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, LogicalAndOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTBitwiseOrNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTBitwiseXorNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTBitwiseAndNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTEQNode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, EqualsOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTNENode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, DoesNotEqualOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTRegexMatchNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTLTNode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, LessThanOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTGTNode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, GreaterThanOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTLENode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, LessOrEqualThanOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTGENode node, EvaluationEnvironment data) {
        processBinaryVisit(node, data, GreaterOrEqualThanOperation::new);
        return null;
    }

    @Override
    public AttributedObject visit(ASTRightShiftNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTUnsignedRightShiftNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTLeftShiftNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTAddNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTSubtractNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTMulNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTDivNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTModNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTUnaryNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTLogicalNotNode node, EvaluationEnvironment data) {
        node.jjtGetChild(0).jjtAccept(this, data);
        Expression expression = pop();
        push(new LogicalNotOperation(expression));
        return null;
    }

    @Override
    public AttributedObject visit(ASTBitwiseNotNode node, EvaluationEnvironment data) {
        throw new SmartFilterParseException(Cres.get().getString("smartFilterOperationNotSupported") + ": " + node);
    }

    @Override
    public AttributedObject visit(ASTFunctionNode node, EvaluationEnvironment data) {
        String funcName = node.name;
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            node.jjtGetChild(i).jjtAccept(this, data);
        }

        Expression[] operands = new Expression[node.jjtGetNumChildren()];
        for (int i = node.jjtGetNumChildren() - 1; i >= 0; i--) {
            operands[i] = pop();
        }

        Class<? extends FunctionOperation> funcClass = filterFunctions.get(funcName);
        DefaultFunctions aggFunction;
        if (funcClass != null) {
            try {
                Constructor<? extends FunctionOperation> constructor = funcClass.getConstructor(operands.getClass());
                FunctionOperation functionOperation = constructor.newInstance((Object) operands);
                push(functionOperation);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else if ((aggFunction = funcDefs.get(funcName)) != null) {
            WrappingFunction wf = new WrappingFunction(aggFunction.impl, operands);
            push(wf);
        } else {
            throw new SmartFilterParseException(Cres.get().getString("smartFilterUnknownFunction") + ": " + funcName);
        }
        return null;
    }

    @Override
    public AttributedObject visit(ASTValueReferenceNode node, EvaluationEnvironment data) {
        push(new ColumnName(node.uriImage));
        return null;
    }

    @Override
    public AttributedObject visit(ASTLongConstNode node, EvaluationEnvironment data) {
        push(new NumberLiteral(node.val));
        return null;
    }

    @Override
    public AttributedObject visit(ASTFloatConstNode node, EvaluationEnvironment data) {
        Number result = node.floatVal;
        if (result == null)
        {
            result = node.doubleVal;
        }
        push(new NumberLiteral(result));
        return null;
    }

    @Override
    public AttributedObject visit(ASTStringConstNode node, EvaluationEnvironment data) {
        push(new StringLiteral(node.val));
        return null;
    }

    @Override
    public AttributedObject visit(ASTNullNode node, EvaluationEnvironment data) {
        push(new NullLiteral());
        return null;
    }

    @Override
    public AttributedObject visit(ASTTrueNode node, EvaluationEnvironment data) {
        push(new BooleanLiteral(true));
        return null;
    }

    @Override
    public AttributedObject visit(ASTFalseNode node, EvaluationEnvironment data) {
        push(new BooleanLiteral(false));
        return null;
    }

    private void push(Expression expression) {
        stack[++sp] = expression;
    }

    private Expression pop() {
        return stack[sp--];
    }

    private void processBinaryVisit(SimpleNode node, EvaluationEnvironment data, BiFunction<Expression, Expression, Expression> expressionProvider) {
        node.jjtGetChild(0).jjtAccept(this, data);
        node.jjtGetChild(1).jjtAccept(this, data);

        Expression right = pop();
        Expression left = pop();
        push(expressionProvider.apply(left, right));
    }

}
