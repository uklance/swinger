package com.swinger.impl;

import com.swinger.annotation.*;
import com.swinger.api.Controller;
import com.swinger.api.ControllerFactory;
import com.swinger.api.SwingWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultControllerFactory implements ControllerFactory {
    private enum Phase {
        SETUP, BEGIN, BEFORE_TEMPLATE, BEFORE_BODY, AFTER_BODY, AFTER_TEMPLATE, AFTER, CLEANUP
    }

    private static final Map<Phase, Class<? extends Annotation>> ANNOTATIONS = Map.of(
            Phase.SETUP, SetupRender.class,
            Phase.BEGIN, BeginRender.class,
            Phase.BEFORE_TEMPLATE, BeforeRenderTemplate.class,
            Phase.BEFORE_BODY, BeforeRenderBody.class,
            Phase.AFTER_BODY, AfterRenderBody.class,
            Phase.AFTER_TEMPLATE, AfterRenderTemplate.class,
            Phase.AFTER, AfterRender.class,
            Phase.CLEANUP, CleanupRender.class
    );

    private final Map<Class<?>, Controller> controllers = new ConcurrentHashMap<>();

    @Override
    public Controller create(Class<?> componentType) {
        return controllers.computeIfAbsent(componentType, this::createController);
    }

    private Controller createController(Class<?> componentType) {
        Map<Phase, List<Method>> methods = new EnumMap<>(Phase.class);
        for (Phase phase : Phase.values()) {
            methods.put(phase, findMethods(componentType, ANNOTATIONS.get(phase)));
        }
        return new ReflectiveController(methods);
    }

    private List<Method> findMethods(Class<?> type, Class<? extends Annotation> annotation) {
        List<Method> result = new ArrayList<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getAnnotation(annotation) != null) {
                    method.setAccessible(true);
                    result.add(method);
                }
            }
        }
        return result;
    }

    private static final class ReflectiveController implements Controller {
        private final Map<Phase, List<Method>> methods;

        private ReflectiveController(Map<Phase, List<Method>> methods) {
            this.methods = methods;
        }

        @Override public boolean setupRender(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.SETUP, instance, writer);
        }
        @Override public boolean beginRender(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.BEGIN, instance, writer);
        }
        @Override public boolean beforeRenderTemplate(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.BEFORE_TEMPLATE, instance, writer);
        }
        @Override public boolean beforeRenderBody(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.BEFORE_BODY, instance, writer);
        }
        @Override public boolean afterRenderBody(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.AFTER_BODY, instance, writer);
        }
        @Override public boolean afterRenderTemplate(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.AFTER_TEMPLATE, instance, writer);
        }
        @Override public boolean afterRender(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.AFTER, instance, writer);
        }
        @Override public boolean cleanupRender(Object instance, SwingWriter writer) throws Exception {
            return invoke(Phase.CLEANUP, instance, writer);
        }

        private boolean invoke(Phase phase, Object instance, SwingWriter writer) throws Exception {
            boolean proceed = true;
            for (Method method : methods.get(phase)) {
                Object result;
                try {
                    if (method.getParameterCount() == 0) {
                        result = method.invoke(instance);
                    } else if (method.getParameterCount() == 1
                            && SwingWriter.class.isAssignableFrom(method.getParameterTypes()[0])) {
                        result = method.invoke(instance, writer);
                    } else {
                        throw new IllegalArgumentException("Lifecycle method must accept no parameters or SwingWriter: " + method);
                    }
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof Exception) {
                        throw (Exception) cause;
                    }
                    throw e;
                }
                if (method.getReturnType() == void.class) {
                    continue;
                }
                if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
                    proceed &= Boolean.TRUE.equals(result);
                } else {
                    throw new IllegalArgumentException("Lifecycle method must return boolean or void: " + method);
                }
            }
            return proceed;
        }
    }
}
