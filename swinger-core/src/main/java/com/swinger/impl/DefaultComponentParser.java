package com.swinger.impl;

import com.swinger.LocationException;
import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.io.Resource;
import com.swinger.model.RenderCommand;
import com.swinger.sax.*;
import lombok.AllArgsConstructor;
import org.xml.sax.Attributes;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
public class DefaultComponentParser implements ComponentParser {
    private static final String SWINGER_NAMESPACE = "swinger";
    private final ComponentTemplateParser templateParser;
    private final BindingSourceRegistry bindingSourceRegistry;
    private final ComponentTypeResolver componentTypeResolver;
    private final ComponentDefinitionSource componentDefinitionSource;

    @Override
    public RenderCommand parse(Class<?> type) throws Exception {
        return componentRenderCommand(type, Collections.emptyList());
    }

    protected RenderCommand componentRenderCommand(Class<?> type, List<ComponentTemplateNode> bodyNodes) throws Exception {
        ComponentTemplate template = resolveComponentTemplate(type);
        ComponentTemplateNode templateNode = template.getRootNode();
        Map<String, String> attributeProperties = new LinkedHashMap<>();
        String id = null;
        Binding key = null;
        Attributes attributes = templateNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            if (SWINGER_NAMESPACE.equals(attributes.getURI(i))) {
                if ("id".equals(name)) {
                    id = attributes.getValue(i);
                } else if ("key".equals(name)) {
                    key = asBinding(attributes.getValue(i), "literal");
                } else {
                    throw new LocationException(templateNode.getLocation(), "Unsupported attribute: " + attributes.getQName(i));
                }
            } else {
                attributeProperties.put(name, attributes.getValue(i));
            }
        }
        List<PropertyBinding> propertyBindings = new ArrayList<>();
        RenderCommand templateCommand = asRenderCommand(List.of(templateNode));
        ComponentDefinition definition = componentDefinitionSource.get(type, templateCommand, id, key);
        Map<String, PropertyDefinition> propertyDefinitions = definition.getPropertyDefinitions().stream()
                .collect(toMap(PropertyDefinition::getName, Function.identity()));
        for (ParameterTemplateNode parameterNode : templateNode.getParameters()) {
            String name = parameterNode.getName();
            if (!propertyDefinitions.containsKey(name)) {
                throw new LocationException(templateNode.getLocation(), "Unexpected property: " + name);
            }
            RenderCommand renderCommand = asRenderCommand(parameterNode.getComponents());
            propertyBindings.add(new DefaultPropertyBinding(name, instance -> renderCommand));
        }
        for (String name : attributeProperties.keySet()) {
            PropertyDefinition propertyDefinition = propertyDefinitions.get(name);
            if (propertyDefinition == null) {
                throw new LocationException(templateNode.getLocation(), "Unexpected property: " + name);
            }
            Binding binding = asBinding(attributeProperties.get(name), propertyDefinition.getDefaultBindingPrefix());
            propertyBindings.add(new DefaultPropertyBinding(name, binding));
        }
        RenderCommand bodyCommand = asRenderCommand(bodyNodes);
        return writer -> {
            ComponentInstance instance = definition.createInstance(propertyBindings);
            writer.startComponent(instance);
            Controller controller = definition.getController();
            controller.setupRender(instance, writer);
            controller.beginRender(instance, writer);
            controller.beforeRenderTemplate(instance, writer);
            templateCommand.render(writer);
            controller.afterRenderTemplate(instance, writer);
            controller.beforeRenderBody(instance, writer);
            bodyCommand.render(writer);
            controller.afterRenderBody(instance, writer);
            controller.afterRender(instance, writer);
            controller.cleanupRender(instance, writer);
            writer.endComponent();
        };
    }

    private RenderCommand asRenderCommand(List<ComponentTemplateNode> templateNodes) throws Exception {
        List<RenderCommand> commands = new ArrayList<>(templateNodes.size());
        for (ComponentTemplateNode node : templateNodes) {
            Class<?> componentType = componentTypeResolver.getComponentType(node.getName());
            RenderCommand renderCommand = componentRenderCommand(componentType, node.getComponents());
            commands.add(renderCommand);
        }
        return writer -> {
            for (RenderCommand command : commands) {
                command.render(writer);
            }
        };
    }

    protected Binding asBinding(String value, String defaultPrefix) {
        int colonIndex = value.indexOf(':');
        String prefix;
        String sValue;
        if (colonIndex >= 0) {
            prefix = value.substring(0, colonIndex);
            sValue = value.substring(colonIndex + 1);
        } else {
            prefix = defaultPrefix;
            sValue = value;
        }
        return bindingSourceRegistry.get(prefix).create(sValue);
    }

    /*
    protected RenderCommand asRenderCommand(List<TemplateNode> nodes) {
        return writer -> {
            for (TemplateNode node : nodes) {
                ComponentTemplateNode componentNode = (ComponentTemplateNode) node;
                Class<?> componentType = componentTypeResolver.getComponentType(componentNode.getName());
                RenderCommand renderCommand = asRenderCommand(componentNode);
                renderCommand.render(writer);`
            }
        };
    }*/

    protected ComponentTemplate resolveComponentTemplate(Class<?> type) throws Exception {
        String templatePath = type.getName().replace('.', '/') + ".xml";
        Resource templateResource = new ClassloaderResource(type, templatePath);
        return templateResource.exists()
                ? templateParser.parse(templateResource)
                : null;
    }
}
