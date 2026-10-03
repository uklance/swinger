package com.swinger.api;

public interface ComponentDecorator {
    void decorate(Object component, ComponentInstance componentInstance) throws Exception;
}
