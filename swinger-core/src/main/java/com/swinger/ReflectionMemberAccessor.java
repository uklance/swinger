package com.swinger;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ReflectionMemberAccessor implements MemberAccessor {
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    static class CacheKey {
        private Class<?> declaringClass;
        private String name;

        public void init(Class<?> declaringClass, String name) {
            this.declaringClass = declaringClass;
            this.name = name;
        }
    }

    private final Map<CacheKey, Method> setters = new HashMap<>();
    private final Set<Class<?>> setterTypes = new HashSet<>();
    private final Map<CacheKey, Method> getters = new HashMap<>();
    private final Set<Class<?>> getterTypes = new HashSet<>();

    @Override
    public void setProperty(Object target, String name, Object value) throws Exception {
        maybeInitSetters(target.getClass());
        Method setter = findMethod(target.getClass(), name, setters);
        if (setter == null) {
            String msg = String.format("No setter for %s in %s", name, target.getClass().getName());
            throw new RuntimeException(msg);
        }
        setter.invoke(target, value);
    }

    @Override
    public Object getProperty(Object target, String name) throws Exception {
        maybeInitGetters(target.getClass());
        Method getter = findMethod(target.getClass(), name, getters);
        if (getter == null) {
            String msg = String.format("No setter for %s in %s", name, target.getClass().getName());
            throw new RuntimeException(msg);
        }
        return getter.invoke(target);
    }

    protected Method findMethod(Class<?> type, String name, Map<CacheKey, Method> cache) {
        CacheKey cacheKey = new CacheKey();
        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
            cacheKey.init(currentType, name);
            Method method = cache.get(cacheKey);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    protected void maybeInitSetters(Class<?> type) {
        for (Class<?> currentType = type; currentType != null && !setterTypes.contains(currentType); currentType = currentType.getSuperclass()) {
            for (Method method : currentType.getDeclaredMethods()) {
                String name = method.getName();
                if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 1 && name.length() > 3) {
                    if (name.startsWith("set") || name.startsWith("add")) {
                        CacheKey cacheKey = new CacheKey(currentType, lowerFirst(name, 3));
                        setters.put(cacheKey, method);
                    }
                }
            }
            setterTypes.add(currentType);
        }
    }

    protected void maybeInitGetters(Class<?> type) {
        for (Class<?> currentType = type; currentType != null && !getterTypes.contains(currentType); currentType = currentType.getSuperclass()) {
            for (Method method : currentType.getDeclaredMethods()) {
                String name = method.getName();
                if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
                    if (name.startsWith("get") && name.length() > 3) {
                        CacheKey cacheKey = new CacheKey(currentType, lowerFirst(name, 3));
                        getters.put(cacheKey, method);
                    } else if (name.startsWith("is") && name.length() > 2 && boolean.class.equals(method.getReturnType())) {
                        CacheKey cacheKey = new CacheKey(currentType, lowerFirst(name, 2));
                        getters.put(cacheKey, method);
                    }
                }
            }
            getterTypes.add(currentType);
        }
    }

    protected String lowerFirst(String name, int startIndex) {
        return Character.toLowerCase(name.charAt(startIndex)) + name.substring(startIndex + 1);
    }
}
