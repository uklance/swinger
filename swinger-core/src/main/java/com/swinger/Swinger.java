package com.swinger;

import lombok.AllArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@AllArgsConstructor
public class Swinger {
    private final DocumentBuilder documentBuilder;
    private final ClassLoader classLoader;
    private final ServiceRegistry serviceRegistry;
    private final List<FieldResolver> fieldResolvers;
    private final List<MethodHandler> methodHandlers;
    private final Map<String, TagHandler> tagHandlers;

    public <T> ComponentSource build(Class<T> type) throws Exception {
        String xmlPath = String.format("%s.xml", type.getName().replace('.', '/'));
        try (InputStream xmlIn = classLoader.getResourceAsStream(xmlPath)) {
            Objects.requireNonNull(xmlIn, xmlPath);
            InputSource xmlSource = new InputSource(xmlIn);
            xmlSource.setSystemId(xmlPath);
            xmlSource.setPublicId(xmlPath);

            Document document = documentBuilder.parse(xmlSource);
            Element element = document.getDocumentElement();
            T controller = type.getConstructor().newInstance();
            SwingerContext context = new SwingerContext() {
                @Override
                public Object getController() {
                    return controller;
                }

                @Override
                public ServiceRegistry getRegistry() {
                    return serviceRegistry;
                }
            };
            init(controller, context);
            return build(element, context);
        }
    }

    protected void init(Object controller, SwingerContext context) throws Exception {
        for (Class<?> type = controller.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                Optional<FieldResolver> resolverOption = fieldResolvers.stream()
                        .filter(r -> r.supportsField(field, context))
                        .findFirst();
                if (resolverOption.isPresent()) {
                    field.setAccessible(true);
                    Object value = resolverOption.get().resolveField(field, context);
                    field.set(controller, value);
                }
            }
        }
        for (Method method : controller.getClass().getMethods()) {
            methodHandlers.stream()
                    .filter(h -> h.supportsMethod(method, context))
                    .findFirst()
                    .ifPresent(h -> h.handleMethod(method, context));
        }
    }

    protected ComponentSource build(Element element, SwingerContext context) throws Exception {
        String tag = element.getTagName();
        NodeList children = element.getChildNodes();
        TagHandler tagHandler = tagHandlers.get(tag);
        Objects.requireNonNull(tagHandler, "Tag handler for " + tag);
        ComponentSource rootSource = tagHandler.handle(element, context);
        JComponent component = rootSource.getComponent();
        List<ComponentSource> childSources = children.getLength() == 0 ? Collections.emptyList() : new ArrayList<>();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                ComponentSource childSource = build((Element) child, context);
                childSources.add(childSource);
                component.add(childSource.getComponent(), childSource.getConstraints());
            }
        }
        return rootSource;
    }
}
