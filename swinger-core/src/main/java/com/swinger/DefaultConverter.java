package com.swinger;

public class DefaultConverter implements Converter {
    @Override
    public <T> T convert(Object value, Class<T> type) {
        return type.cast(value);
    }
}
