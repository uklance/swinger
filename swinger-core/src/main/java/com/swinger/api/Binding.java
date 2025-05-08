package com.swinger.api;

public interface Binding {
    Object resolve(Object controller, String value) throws Exception;
}
