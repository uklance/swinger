package com.swinger.impl;

import com.swinger.annotation.OnEvent;
import com.swinger.api.Controller;
import com.swinger.api.ControllerMethodHandler;
import com.swinger.api.EventManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@AllArgsConstructor
@Slf4j
public class OnEventControllerMethodHandler implements ControllerMethodHandler {
    private final EventManager eventManager;

    @Override
    public boolean supportsMethod(Method method) {
        return method.getAnnotation(OnEvent.class) != null;
    }

    @Override
    public void handleMethod(Method method, Controller controller) {
        String event = method.getAnnotation(OnEvent.class).value();
        eventManager.subscribe(event, ev -> {
            try {
                method.invoke(controller);
            } catch (Exception ex) {
                log.error("event={}", event, ex);
            }
        });
    }
}

