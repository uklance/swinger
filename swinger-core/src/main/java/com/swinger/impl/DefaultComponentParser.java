package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.io.Resource;
import com.swinger.sax.ComponentTemplate;
import com.swinger.sax.ComponentTemplateParser;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@AllArgsConstructor
public class DefaultComponentParser implements ComponentParser {
    private static final ComponentTemplate EMPTY_TEMPLATE = () -> null;
    private final ComponentTemplateParser templateParser;
    private final List<ControllerFieldHandler> fieldHandlers;
    private final List<ControllerMethodHandler> methodHandlers;
    private final Map<Class<?>, Consumer<Controller>> initializerCache = new ConcurrentHashMap<>();

    @Override
    public ComponentResources parse(Class<? extends Controller> type) throws Exception {
        Controller controller = createController(type);
        String templatePath = type.getName().replace('.', '/') + ".xml";
        Resource templateResource = new ClassloaderResource(type.getClassLoader(), templatePath);
        ComponentTemplate template;
        if (templateResource.exists()) {
            template = templateParser.parse(templateResource);
        } else {
            template = EMPTY_TEMPLATE;
        }
        return new DefaultComponentResources(controller, template);
    }

    protected Controller createController(Class<? extends Controller> type) throws Exception {
        Controller controller = type.getDeclaredConstructor().newInstance();
        Consumer<Controller> initializer = initializerCache.computeIfAbsent(controller.getClass(), this::createInitializer);
        initializer.accept(controller);
        return controller;
    }

    protected Consumer<Controller> createInitializer(Class<?> type) {
        List<Consumer<Controller>> consumers = new ArrayList<>();
        for (Class<?> currentType = type; currentType != null; currentType = currentType.getSuperclass()) {
            for (Field field : currentType.getDeclaredFields()) {
                fieldHandlers.stream()
                        .filter(h -> h.supportsField(field))
                        .findFirst()
                        .ifPresent(h -> consumers.add(controller -> h.handleField(field, controller)));
            }
        }
        for (Method method : type.getMethods()) {
            methodHandlers.stream()
                    .filter(h -> h.supportsMethod(method))
                    .findFirst()
                    .ifPresent(h -> consumers.add(controller -> h.handleMethod(method, controller)));
        }
        if (consumers.isEmpty()) {
            return controller -> {};
        }
        return controller -> {
            for (Consumer<Controller> consumer : consumers) {
                consumer.accept(controller);
            }
        };
    }
}
