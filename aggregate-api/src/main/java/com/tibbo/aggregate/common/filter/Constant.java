package com.tibbo.aggregate.common.filter;

public class Constant extends Identifier {

    public Constant(String name, Object value) {
        super(name, () -> value);
    }
}
