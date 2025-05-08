package com.swinger;

import com.swinger.api.*;
import com.swinger.impl.IdGenerator;
import com.swinger.model.ComponentResources;
import lombok.AllArgsConstructor;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@AllArgsConstructor
public class Swinger {
    private final ComponentFactory componentFactory;
    private final BindingRegistry bindingRegistry;
    private final MemberAccessor memberAccessor;
    private final SAXParser parser;
    private final List<ControllerFieldHandler> fieldHandlers;
    private final List<ControllerMethodHandler> methodHandlers;
    private final Map<Class<?>, Consumer<Object>> initializerCache = new ConcurrentHashMap<>();

    public ComponentResources createComponent(Class<?> controllerType) throws Exception {
        return createComponent(controllerType, new IdGenerator());
    }

    public ComponentResources createComponent(Class<?> controllerType, IdGenerator idGenerator) throws Exception {
        String xmlPath = String.format("%s.xml", controllerType.getName().replace('.', '/'));
        Object controller = createController(controllerType);
        ComponentSaxHandler saxHandler = new ComponentSaxHandler(controller, componentFactory, bindingRegistry, memberAccessor, idGenerator);
        try (InputStream in = controllerType.getClassLoader().getResourceAsStream(xmlPath)) {
            if (in == null) {
                throw new RuntimeException("No such resource " + xmlPath);
            }
            InputSource inputSource = new InputSource(in);
            inputSource.setSystemId(xmlPath);
            parser.parse(inputSource, saxHandler);
        }
        return saxHandler.getRoot();
    }

    protected Object createController(Class<?> controllerType) throws Exception {
        Object controller = controllerType.getDeclaredConstructor().newInstance();
        Consumer<Object> initializer = initializerCache.computeIfAbsent(controller.getClass(), this::createInitializer);
        initializer.accept(controller);
        return controller;
    }

    protected Consumer<Object> createInitializer(Class<?> type) {
        List<Consumer<Object>> consumers = new ArrayList<>();
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
        return controller -> {
            for (Consumer<Object> consumer : consumers) {
                consumer.accept(controller);
            }
        };
    }
}
