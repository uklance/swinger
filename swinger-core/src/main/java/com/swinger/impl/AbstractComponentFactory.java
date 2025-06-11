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
    public ComponentResources create(ComponentTemplateNode templateNode) throws Exception {
        Class<? extends Controller> type = resolveControllerType(templateNode);
        Controller controller = type.getDeclaredConstructor().newInstance();
        ComponentTemplate template = resolveComponentTemplate(type);
        ComponentResources resources = new DefaultComponentResources(controller, template);
        applyFieldHandlers(resources);
        applyMethodHandlers(resources);
        applyProperties(resources);
        return resources;
    }

    protected ComponentTemplate resolveComponentTemplate(Class<? extends Controller> type) throws Exception {
        String templatePath = type.getName().replace('.', '/') + ".xml";
        Resource templateResource = new ClassloaderResource(type, templatePath);
        return templateResource.exists()
                ? templateParser.parse(templateResource)
                : null;
    }

    protected void applyFieldHandlers(ComponentResources resources) {
        Controller controller = resources.getController();
        for (Class<?> currentType = controller.getClass(); currentType != null; currentType = currentType.getSuperclass()) {
            for (Field field : currentType.getDeclaredFields()) {
                fieldHandlers.stream()
                        .filter(h -> h.supportsField(field))
                        .findFirst()
                        .ifPresent(h -> h.handleField(field, controller));
            }
        }
    }

    protected void applyMethodHandlers(ComponentResources resources) {
        Controller controller = resources.getController();
        for (Class<?> currentType = controller.getClass(); currentType != null; currentType = currentType.getSuperclass()) {
            for (Method method : currentType.getDeclaredMethods()) {
                methodHandlers.stream()
                        .filter(h -> h.supportsMethod(method))
                        .findFirst()
                        .ifPresent(h -> h.handleMethod(method, controller));
            }
        }
    }

    protected void applyProperties(ComponentResources resources) throws Exception {
        if (resources.getTemplate() == null) {
            return;
        }
        Controller controller = resources.getController();
        ComponentTemplateNode rootNode = resources.getTemplate().getRootNode();
        Attributes attributes = rootNode.getAttributes();
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
                memberAccessor.setProperty(controller, attName, attValue);
            } else {
                String msg = String.format("Unsupported uri %s for attribute %s", attUri, attName);
                throw new LocationException(rootNode.getLocation(), msg);
            }
        }
        for (ParameterTemplateNode parameter : rootNode.getParameters()) {
            if (parameter.getComponent() == null) {
                String msg = String.format("No components for parameter %s of component %s", parameter.getName(), rootNode.getName());
                throw new LocationException(parameter.getLocation(), msg);
            }
            ComponentResources parameterResources = create(parameter.getComponent());
            DefaultSwingWriter swingWriter = new DefaultSwingWriter();
            componentRenderer.render(parameterResources, swingWriter);
            if (swingWriter.getRoots().size() != 1) {
                String msg = String.format("Expected 1 component for parameter %s of component %s found %s",
                        parameter.getName(), rootNode.getName(), swingWriter.getRoots().size());
                throw new LocationException(parameter.getLocation(), msg);
            }
            memberAccessor.setProperty(controller, parameter.getName(), swingWriter.getRoots().get(0));
        }
    }
}
