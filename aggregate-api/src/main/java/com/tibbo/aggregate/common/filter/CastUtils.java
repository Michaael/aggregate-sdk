package com.tibbo.aggregate.common.filter;

public class CastUtils {

    public static Class<? extends Number> promoteTypes(Class<? extends Number> type1, Class<? extends Number> type2) {
        if (type1 == type2) {
            return type1;
        }

        if (type1 == Double.class) {
            return Double.class;
        }

        if (type1 == Float.class) {
            if (type2 == Double.class || type2 == Long.class) {
                return Double.class;
            } else {
                return Float.class;
            }
        } else if (type1 == Long.class) {
            if (type2 == Double.class || type2 == Float.class) {
                return Double.class;
            } else {
                return Long.class;
            }
        } else if (type1 == Integer.class) {
            if (type2 == Short.class || type2 == Byte.class) {
                return Integer.class;
            } else {
                return type2;
            }
        } else if (type1 == Short.class) {
            if (type2 == Byte.class) {
                return Short.class;
            } else {
                return type2;
            }
        } else {
            return type2;
        }
    }

    public static Number castNumberValue(Class<? extends Number> type, Number value) {
        if (value.getClass() == type) {
            return value;
        }

        if (type == Double.class) {
            return value.doubleValue();
        } else if (type == Float.class) {
            return value.floatValue();
        } else if (type == Long.class) {
            return value.longValue();
        } else if (type == Integer.class) {
            return value.intValue();
        } else if (type == Short.class) {
            return value.shortValue();
        } else if (type == Byte.class) {
            return value.byteValue();
        } else {
            throw new IllegalArgumentException("Illegal type to cast to: " + type.getName());
        }
    }
}
