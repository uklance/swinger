package com.swinger;

public interface Converter {
    <T> T convert(Object value, Class<T> type);
}
