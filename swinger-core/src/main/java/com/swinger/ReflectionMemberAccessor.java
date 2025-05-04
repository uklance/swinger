package com.swinger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ReflectionMemberAccessor implements MemberAccessor {
    private final Map<String, Method> setters = new LinkedHashMap<>();
    private final Map<String, Method> getters = new LinkedHashMap<>();

    @Override
    public void setProperty(Object target, String name, Object value) throws Exception {
        String key = target.getClass().getName() + "#" + name;
        Method setter = setters.get(key);
        if (setter == null) {
            Optional<Method> setterOption = findMethod(target.getClass(), "set", name, 1);
            if (setterOption.isEmpty()) {
                setterOption = findMethod(target.getClass(), "add", name, 1);
                if (setterOption.isEmpty()) {
                    String msg = String.format("No setter for '%s' in %s", name, target.getClass().getName());
                    throw new RuntimeException(msg);
                }
            }
            setter = setterOption.get();
            setters.put(key, setter);
        }
        setter.invoke(target, value);
    }

    @Override
    public Object getProperty(Object target, String name) throws Exception {
        String key = target.getClass().getName() + "#" + name;
        Method getter = getters.get(key);
        if (getter == null) {
            Optional<Method> getterOption = findMethod(target.getClass(), "get", name, 0);
            if (getterOption.isEmpty()) {
                String msg = String.format("No getter for '%s' in %s", name, target.getClass().getName());
                throw new RuntimeException(msg);
            }
            getter = getterOption.get();
            setters.put(key, getter);
        }
        return getter.invoke(target);
    }

    private Optional<Method> findMethod(Class<?> type, String prefix, String propName, int argumentCount) {
        String methodName = prefix + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
        return Arrays.stream(type.getMethods())
                .filter(m -> m.getParameterCount() == argumentCount)
                .filter(m -> m.getName().equals(methodName))
                .findFirst();
    }
}
