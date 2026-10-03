package com.swinger.impl;

import com.swinger.annotation.*;
import com.swinger.api.ComponentInstance;
import com.swinger.api.Controller;
import com.swinger.api.ControllerSource;
import com.swinger.api.SwingWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultControllerSource implements ControllerSource {
    private interface LifecycleHandler {
        boolean handle(ComponentInstance instance, SwingWriter writer) throws Exception;
    }

    @Override
    public Controller get(Class<?> type) throws Exception {
        Map<Class<? extends Annotation>, List<Method>> methodsByAnnotation = new LinkedHashMap<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    List<Method> group = methodsByAnnotation.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>());
                    group.add(method);
                }
            }
        }

        LifecycleHandler setupRender = createLifecycleHandler(methodsByAnnotation, SetupRender.class);
        LifecycleHandler beginRender = createLifecycleHandler(methodsByAnnotation, BeginRender.class);
        LifecycleHandler beforeRenderTemplate = createLifecycleHandler(methodsByAnnotation, BeforeRenderTemplate.class);
        LifecycleHandler beforeRenderBody = createLifecycleHandler(methodsByAnnotation, BeforeRenderBody.class);
        LifecycleHandler afterRenderBody = createLifecycleHandler(methodsByAnnotation, AfterRenderBody.class);
        LifecycleHandler afterRenderTemplate = createLifecycleHandler(methodsByAnnotation, AfterRenderTemplate.class);
        LifecycleHandler afterRender = createLifecycleHandler(methodsByAnnotation, AfterRender.class);
        LifecycleHandler cleanupRender = createLifecycleHandler(methodsByAnnotation, CleanupRender.class);

        return new Controller() {
            @Override
            public boolean setupRender(ComponentInstance instance, SwingWriter writer) throws Exception {
                return setupRender.handle(instance, writer);
            }

            @Override
            public boolean beginRender(ComponentInstance instance, SwingWriter writer) throws Exception {
                return beginRender.handle(instance, writer);
            }

            @Override
            public boolean beforeRenderTemplate(ComponentInstance instance, SwingWriter writer) throws Exception {
                return beforeRenderTemplate.handle(instance, writer);
            }

            @Override
            public boolean beforeRenderBody(ComponentInstance instance, SwingWriter writer) throws Exception {
                return beforeRenderBody.handle(instance, writer);
            }

            @Override
            public boolean afterRenderBody(ComponentInstance instance, SwingWriter writer) throws Exception {
                return afterRenderBody.handle(instance, writer);
            }

            @Override
            public boolean afterRenderTemplate(ComponentInstance instance, SwingWriter writer) throws Exception {
                return afterRenderTemplate.handle(instance, writer);
            }

            @Override
            public boolean afterRender(ComponentInstance instance, SwingWriter writer) throws Exception {
                return afterRender.handle(instance, writer);
            }

            @Override
            public boolean cleanupRender(ComponentInstance instance, SwingWriter writer) throws Exception {
                return cleanupRender.handle(instance, writer);
            }
        };
    }

    private LifecycleHandler createLifecycleHandler(
            Map<Class<? extends Annotation>, List<Method>> methodsByAnnotation,
            Class<? extends Annotation> annotation
    ) {
        List<Method> methods = methodsByAnnotation.get(annotation);
        if (methods == null || methods.isEmpty()) {
            return (instance, writer) -> true;
        }
        if (methods.size() != 1) {
            throw new IllegalArgumentException(
                    "Multiple methods found for lifecycle annotation " + annotation.getName() + ": " + methods
            );
        }
        Method method = methods.get(0);
        if (Arrays.stream(method.getParameterTypes()).anyMatch(param -> param != SwingWriter.class)) {
            throw new IllegalArgumentException(
                    "Lifecycle method parameters must all be SwingWriter: " + method
            );
        }
        if (method.getReturnType() != void.class && method.getReturnType() != boolean.class) {
            throw new IllegalArgumentException(
                    "Lifecycle method must return void or boolean: " + method
            );
        }
        method.setAccessible(true);
        return (instance, writer) -> {
            Object[] arguments = new Object[method.getParameterCount()];
            Arrays.fill(arguments, writer);
            Object result = method.invoke(instance.getComponent(), arguments);
            if (method.getReturnType() == void.class) {
                return Boolean.TRUE;
            }
            return Boolean.TRUE.equals(result);
        };
    }
}
