package com.tibbo.aggregate.common.filter;

import com.tibbo.aggregate.common.Cres;

public class SmartFilterIllegalOperandException extends SmartFilterEvaluationException {

    private final Operation operation;

    public SmartFilterIllegalOperandException(Operation operation, Class<?> ... expectedTypes) {
        super(Cres.get().getString("smartFilterOperation") + ": " + operation.getDescription() + " " + Cres.get().getString("smartFilterIllegalOperandType") + ": " + makeClassList(expectedTypes));
        this.operation = operation;
    }

    public SmartFilterIllegalOperandException(Operation operation, String message) {
        super(Cres.get().getString("smartFilterOperation") + ": " + operation.getDescription() + " " + message);
        this.operation = operation;
    }

    private static String makeClassList(Class<?>[] classes) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Class<?> cl : classes) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(cl.getName());
        }
        return sb.toString();
    }
}
