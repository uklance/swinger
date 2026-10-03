package com.swinger.api;

public interface PropertyValueConverter {
    Object convert(Object value, Class<?> targetType);
}
