package com.swinger;

import com.swinger.annotations.OnEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
@AllArgsConstructor
public class OnEventMethodHandler implements MethodHandler {
    private final EventManager eventManager;

    @Override
    public boolean supportsMethod(Method method, SwingerContext context) {
        return method.getAnnotation(OnEvent.class) != null;
    }

    @Override
    public void handleMethod(Method method, SwingerContext context) {
        String event = method.getAnnotation(OnEvent.class).value();
        eventManager.subscribe(event, ev -> {
            try {
                method.invoke(context.getController());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
