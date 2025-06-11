package com.swinger.impl;

import com.swinger.LocationException;
import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.io.Resource;
import com.swinger.sax.ComponentTemplate;
import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.ParameterTemplateNode;
import lombok.AllArgsConstructor;
import org.xml.sax.Attributes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public abstract class AbstractComponentFactory implements ComponentFactory {
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([^:]*):(.*)");
    private final MemberAccessor memberAccessor;
    private final ComponentRenderer componentRenderer;
    private final BindingSourceRegistry bindingSourceRegistry;
    private final ComponentTemplateParser templateParser;
    private final List<ControllerFieldHandler> fieldHandlers;
    private final List<ControllerMethodHandler> methodHandlers;

    protected abstract Class<? extends Controller> resolveControllerType(ComponentTemplateNode templateNode);

    @Override
    public ComponentResources create(Class<? extends Controller> type) throws Exception {
        Controller childController = type.getDeclaredConstructor().newInstance();
        applyFieldHandlers(childController);
        applyMethodHandlers(childController);
        ComponentTemplate template = resolveComponentTemplate(type);
        return new DefaultComponentResources(null, childController, template);
    }

    @Override
    public ComponentResources create(ComponentResources resources, ComponentTemplateNode componentNode) throws Exception {
        Class<? extends Controller> type = resolveControllerType(componentNode);
        Controller childController = type.getDeclaredConstructor().newInstance();
        applyFieldHandlers(childController);
        applyMethodHandlers(childController);
        ComponentTemplate template = resolveComponentTemplate(type);
        ComponentResources childResources = new DefaultComponentResources(resources, childController, template);
        applyAttributeProperties(childResources, componentNode);
        applyParameterProperties(childResources, componentNode);
        return childResources;
    }

    protected ComponentTemplate resolveComponentTemplate(Class<? extends Controller> type) throws Exception {
        String templatePath = type.getName().replace('.', '/') + ".xml";
        Resource templateResource = new ClassloaderResource(type, templatePath);
        return templateResource.exists()
                ? templateParser.parse(templateResource)
                : null;
    }

    protected void applyFieldHandlers(Controller controller) {
        for (Class<?> currentType = controller.getClass(); currentType != null; currentType = currentType.getSuperclass()) {
            for (Field field : currentType.getDeclaredFields()) {
                fieldHandlers.stream()
                        .filter(h -> h.supportsField(field))
                        .findFirst()
                        .ifPresent(h -> h.handleField(field, controller));
            }
        }
    }

    protected void applyMethodHandlers(Controller controller) {
        for (Class<?> currentType = controller.getClass(); currentType != null; currentType = currentType.getSuperclass()) {
            for (Method method : currentType.getDeclaredMethods()) {
                methodHandlers.stream()
                        .filter(h -> h.supportsMethod(method))
                        .findFirst()
                        .ifPresent(h -> h.handleMethod(method, controller));
            }
        }
    }

    protected void applyAttributeProperties(ComponentResources resources, ComponentTemplateNode componentNode) throws Exception {
        Attributes attributes = componentNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            String attUri = attributes.getURI(i);
            String attName = attributes.getLocalName(i);
            String attStringValue = attributes.getValue(i);
            Object attValue;

            if (attUri == null || attUri.isEmpty()) {
                // default xml namespace
                Matcher matcher = ATTRIBUTE_PATTERN.matcher(attStringValue);
                if (matcher.matches()) {
                    BindingSource bindingSource = bindingSourceRegistry.get(matcher.group(1));
                    Binding binding = bindingSource.create(matcher.group(2), resources);
                    attValue = binding.get();
                } else {
                    attValue = attStringValue;
                }
                memberAccessor.setProperty(resources.getController(), attName, attValue);
            } else {
                String msg = String.format("Unsupported uri %s for attribute %s", attUri, attName);
                throw new LocationException(componentNode.getLocation(), msg);
            }
        }
    }

    protected void applyParameterProperties(ComponentResources resources, ComponentTemplateNode componentNode) throws Exception {
        for (ParameterTemplateNode parameter : componentNode.getParameters()) {
            if (parameter.getComponent() == null) {
                String msg = String.format("No components for parameter %s of component %s", parameter.getName(), componentNode.getName());
                throw new LocationException(parameter.getLocation(), msg);
            }
            ComponentResources parameterResources = create(resources, parameter.getComponent());
            DefaultSwingWriter swingWriter = new DefaultSwingWriter();
            componentRenderer.render(parameterResources, swingWriter);
            if (swingWriter.getRoots().size() != 1) {
                String msg = String.format("Expected 1 component for parameter %s of component %s found %s",
                        parameter.getName(), componentNode.getName(), swingWriter.getRoots().size());
                throw new LocationException(parameter.getLocation(), msg);
            }
            memberAccessor.setProperty(resources.getController(), parameter.getName(), swingWriter.getRoots().get(0));
        }
    }
}
