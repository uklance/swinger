package com.swinger;

public interface Binding {
    Object resolve(String value, SwingerContext context) throws Exception;
}
