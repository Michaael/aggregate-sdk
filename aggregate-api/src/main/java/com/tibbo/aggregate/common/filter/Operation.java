package com.tibbo.aggregate.common.filter;

import java.util.Objects;

public abstract class Operation implements Expression {

    private final String name;

    public Operation(String name) {
        Objects.requireNonNull(name, "Operation name is null");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation name is empty: " + this.getClass().getName());
        }
        this.name = name;
    }

    public final String getName() {
        return name;
    }
    public abstract int getOperandCount();
}
