package com.tibbo.aggregate.common.filter;

import java.util.function.Supplier;

public class Identifier implements Expression {

    private final String name;
    private final Supplier<?> valueSupplier;

    public Identifier(String name, Supplier<?> valueSupplier) {
        this.name = name;
        this.valueSupplier = valueSupplier;
    }

    public Identifier(String name) {
        this(name, null);
    }

    @Override
    public final Object evaluate() {
        return valueSupplier != null ? valueSupplier.get() : fetchValue();
    }

    @Override
    public final Expression[] getChildren() {
        return new Expression[0];
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public String getDescription() {
        return getName();
    }

    public final String getName() {
        return name;
    }

    protected Object fetchValue() {
        return null;
    }
}
