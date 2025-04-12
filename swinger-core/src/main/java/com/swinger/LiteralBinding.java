package com.swinger;

public class LiteralBinding implements Binding {
    @Override
    public Object resolve(String value, SwingerContext context) {
        return value;
    }
}
