package com.swinger;

public interface Binding {
    Object resolve(Object controller, String value) throws Exception;
}
