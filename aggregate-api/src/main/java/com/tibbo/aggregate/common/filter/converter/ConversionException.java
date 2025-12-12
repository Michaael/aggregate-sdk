package com.tibbo.aggregate.common.filter.converter;

import com.tibbo.aggregate.common.AggreGateException;
import com.tibbo.aggregate.common.AggreGateRuntimeException;

public class ConversionException extends AggreGateRuntimeException {
    public ConversionException(String message) {
        super(message);
    }
}
