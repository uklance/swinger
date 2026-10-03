package com.swinger.impl;

import com.swinger.api.PropertyValueConverter;

public class DefaultPropertyValueConverter implements PropertyValueConverter {
    @Override
    public Object convert(Object value, Class<?> targetType) {
        if (value == null || targetType.isInstance(value) || !(value instanceof String)) {
            return value;
        }

        String stringValue = (String) value;
        if (targetType == String.class || targetType == Object.class) {
            return stringValue;
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            if ("true".equalsIgnoreCase(stringValue)) {
                return true;
            }
            if ("false".equalsIgnoreCase(stringValue)) {
                return false;
            }
            throw new IllegalArgumentException("Cannot convert '" + stringValue + "' to boolean");
        }
        if (targetType == byte.class || targetType == Byte.class) {
            return Byte.valueOf(stringValue);
        }
        if (targetType == short.class || targetType == Short.class) {
            return Short.valueOf(stringValue);
        }
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.valueOf(stringValue);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.valueOf(stringValue);
        }
        if (targetType == float.class || targetType == Float.class) {
            return Float.valueOf(stringValue);
        }
        if (targetType == double.class || targetType == Double.class) {
            return Double.valueOf(stringValue);
        }
        if (targetType == char.class || targetType == Character.class) {
            if (stringValue.length() != 1) {
                throw new IllegalArgumentException("Cannot convert '" + stringValue + "' to char");
            }
            return stringValue.charAt(0);
        }
        if (targetType.isEnum()) {
            return Enum.valueOf(targetType.asSubclass(Enum.class), stringValue);
        }
        return value;
    }
}
