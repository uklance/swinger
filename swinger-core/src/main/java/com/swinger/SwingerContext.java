package com.swinger;

public interface SwingerContext {
    Object getController();
    ServiceRegistry getRegistry();
    Object getObject();
    Object getParentObject();
}
