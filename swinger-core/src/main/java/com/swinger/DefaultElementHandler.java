package com.swinger;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import javax.swing.*;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultElementHandler implements ElementHandler {
    private final BindingSource bindingSource;
    private final Converter converter;
    private final Class<? extends JComponent> type;
    private final Map<String, Method> setters;

    public DefaultElementHandler(BindingSource bindingSource, Converter converter, Class<? extends JComponent> type) {
        Map<String, Method> setters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Method method : type.getMethods()) {
            String name = method.getName();
            if (method.getParameterCount() == 1 && (name.startsWith("set") || name.startsWith("add"))) {
                String propName = name.substring(3);
                setters.put(propName, method);
            }
        }
        this.bindingSource = bindingSource;
        this.converter = converter;
        this.type = type;
        this.setters = setters;
    }

    private static final Pattern BINDING_PATTERN = Pattern.compile("(.+?):(.*)");

    @Override
    public ComponentSource handle(Element element, SwingerContext context) throws Exception {
        JComponent component = type.getConstructor().newInstance();
        String id = maybeRemoveAttribute(element, "id");
        String constraints = maybeRemoveAttribute(element, "constraints");

        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Attr attr = (Attr) attributes.item(i);
            if (attr.getName().startsWith("xmlns:")) {
                continue;
            }
            String propName = attr.getName();
            Method setter = setters.get(propName);
            if (setter == null) {
                String msg = String.format("No setter for attribute %s for id %s type %s", propName, id, type.getName());
                throw new RuntimeException(msg);
            }
            String attrValue = attr.getValue();
            Matcher matcher = BINDING_PATTERN.matcher(attrValue);
            Object bindingValue;
            if (matcher.matches()) {
                Binding binding = bindingSource.getBinding(matcher.group(1));
                bindingValue = binding.resolve(matcher.group(2), context);
            } else {
                Binding binding = bindingSource.getDefaultBinding();
                bindingValue = binding.resolve(attrValue, context);
            }
            Object convertedValue = converter.convert(bindingValue, setter.getParameterTypes()[0]);
            setter.invoke(component, convertedValue);
        }

        return new ComponentSource() {
            @Override
            public JComponent getComponent() {
                return component;
            }

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getConstraints() {
                return constraints;
            }
        };
    }

    private String maybeRemoveAttribute(Element element, String name) {
        Attr attr = element.getAttributeNode(name);
        if (attr == null) {
            return null;
        }
        String value = attr.getValue();
        element.removeAttributeNode(attr);
        return value;
    }
}
