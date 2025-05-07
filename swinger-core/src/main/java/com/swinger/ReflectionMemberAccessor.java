package com.swinger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ReflectionMemberAccessor implements MemberAccessor {
    private final Map<Class<?>, Map<String, Method>> settersByDeclaringType = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<String, Method>> gettersByDeclaringType = new ConcurrentHashMap<>();

    @Override
    public void setProperty(Object target, String name, Object value) throws Exception {
        Class<?> targetType = target.getClass();
        Method setter = findMethod(targetType, name, settersByDeclaringType, this::initSetters);
        if (setter == null) {
            String msg = String.format("No setter for %s in %s", name, targetType.getName());
            throw new RuntimeException(msg);
        }
        setter.invoke(target, value);
    }

    @Override
    public Object getProperty(Object target, String name) throws Exception {
        Class<?> targetType = target.getClass();
        Method getter = findMethod(targetType, name, gettersByDeclaringType, this::initGetters);
        if (getter == null) {
            String msg = String.format("No getter for %s in %s", name, targetType.getName());
            throw new RuntimeException(msg);
        }
        return getter.invoke(target);
    }

    /**
     * Walks up the superclass hierarchy looking for the method by declaring class
     */
    protected Method findMethod(Class<?> type, String name, Map<Class<?>, Map<String, Method>> cache, Function<Class<?>, Map<String, Method>> initFunction) {
        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
            Map<String, Method> currentMethods = cache.computeIfAbsent(currentType, initFunction);
            Method method = currentMethods.get(name);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    protected Map<String, Method> initSetters(Class<?> type) {
        Map<String, Method> setters = new ConcurrentHashMap<>();
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 1 && name.length() > 3) {
                if (name.startsWith("set") || name.startsWith("add")) {
                    setters.put(lowerFirst(name, 3), method);
                }
            }
        }
        return setters;
    }

    protected Map<String, Method> initGetters(Class<?> type) {
        Map<String, Method> getters = new ConcurrentHashMap<>();
        for (Method method : type.getDeclaredMethods()) {
            String name = method.getName();
            if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0 && !void.class.equals(method.getReturnType())) {
                if (name.startsWith("get") && name.length() > 3) {
                    getters.put(lowerFirst(name, 3), method);
                } else if (name.startsWith("is") && name.length() > 2 && boolean.class.equals(method.getReturnType())) {
                    getters.put(lowerFirst(name, 2), method);
                }
            }
        }
        return getters;
    }

    protected String lowerFirst(String name, int startIndex) {
        return Character.toLowerCase(name.charAt(startIndex)) + name.substring(startIndex + 1);
    }
}
