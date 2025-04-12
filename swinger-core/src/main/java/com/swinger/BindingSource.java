package com.swinger;

public interface BindingSource {
    Binding getBinding(String name);
    Binding getDefaultBinding();
}
