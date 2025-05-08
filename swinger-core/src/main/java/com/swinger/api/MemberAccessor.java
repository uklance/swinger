package com.swinger.api;

public interface MemberAccessor {
    void setProperty(Object target, String name, Object value) throws Exception;
    Object getProperty(Object target, String name) throws Exception;
}
