package com.swinger;

import java.lang.reflect.Method;

public class PropBinding implements Binding {
    @Override
    public Object resolve(String value, SwingerContext context) throws Exception {
        Object controller = context.getController();
        String getter = "get" + Character.toUpperCase(value.charAt(0)) + value.substring(1);
        Method method = controller.getClass().getMethod(getter);
        return method.invoke(controller);
    }
}
