package com.swinger.api;

import com.swinger.model.EventContext;

public interface EventListener {
    void onEvent(EventContext context);
}