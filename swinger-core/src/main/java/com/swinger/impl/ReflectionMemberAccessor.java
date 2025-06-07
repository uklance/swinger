package com.swinger.impl;

import com.swinger.api.MemberAccessor;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This {@link MemberAccessor} is implemented with the understanding that it will be used to access properties on swing
 * components (eg {@link JLabel} and {@link JTextField}). To reduce the number of objects kept in cache this
 * implementation chooses to cache getters/setters by declaring class since most of the swing classes share common base
 * classes (eg {@link JComponent}). This means that finding the cached setter/getter might require walking up the
 * superclass hierarchy each time (eg {@link JTextField} -> {@link JTextComponent} -> {@link JComponent})
 */
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
     * @return the setter/getter method if found, null if not found
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

    /**
     * Find all setX(...) or addX(...) methods declared in type
     */
    protected Map<String, Method> initSetters(Class<?> type) {
        Map<String, Method> setters = new HashMap<>();
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

    /**
     * Find all getX(...) or isX(...) methods declared in type
     */
    protected Map<String, Method> initGetters(Class<?> type) {
        Map<String, Method> getters = new HashMap<>();
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

    /**
     * @return the name with the prefix removed and the first character after the prefix lowered
     */
    protected String lowerFirst(String name, int startIndex) {
        return Character.toLowerCase(name.charAt(startIndex)) + name.substring(startIndex + 1);
    }
}
