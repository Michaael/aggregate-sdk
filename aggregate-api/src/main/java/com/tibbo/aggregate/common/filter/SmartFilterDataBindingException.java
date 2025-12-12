package com.tibbo.aggregate.common.filter;

public class SmartFilterDataBindingException extends SmartFilterRuntimeException {

    private final String identifierName;

    public SmartFilterDataBindingException(String identifierName, String message) {
        super(message);
        this.identifierName = identifierName;
    }


    public String getIdentifierName() {
        return identifierName;
    }
}
