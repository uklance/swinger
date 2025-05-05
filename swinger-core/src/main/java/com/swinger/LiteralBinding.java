package com.swinger;

public class LiteralBinding implements Binding {
    @Override
    public Object resolve(Object controller, String value) throws Exception {
        return value;
    }
}
