package com.swinger.impl;

import com.swinger.LocationException;
import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.io.Resource;
import com.swinger.model.RenderCommand;
import com.swinger.sax.ComponentTemplate;
import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.ParameterTemplateNode;
import lombok.AllArgsConstructor;
import org.xml.sax.Attributes;

import java.util.*;

@AllArgsConstructor
public class DefaultComponentParser implements ComponentParser {
    private static final String SWINGER_NAMESPACE = "swinger";
    private final ComponentTemplateParser templateParser;
    private final BindingSourceRegistry bindingSourceRegistry;
    private final ComponentTypeResolver componentTypeResolver;
    private final ComponentDefinitionSource componentDefinitionSource;

    @Override
    public RenderCommand parse(Class<?> type) throws Exception {
        return componentRenderCommand(type, null, false);
    }

    protected RenderCommand componentRenderCommand(
            Class<?> type,
            ComponentTemplateNode invocationNode,
            boolean templateRoot
    ) throws Exception {
        try {
            ComponentTemplate template = resolveComponentTemplate(type);
            ComponentTemplateNode templateNode = template == null ? null : template.getRootNode();
            Map<String, String> attributeProperties = new LinkedHashMap<>();
            String id = null;
            Binding key = null;
            if (invocationNode != null) {
                Attributes attributes = invocationNode.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    String name = attributes.getLocalName(i);
                    if (SWINGER_NAMESPACE.equals(attributes.getURI(i))) {
                        if (!templateRoot && "id".equals(name)) {
                            id = attributes.getValue(i);
                        } else if (!templateRoot && "key".equals(name)) {
                            key = asBinding(attributes.getValue(i), "literal");
                        } else if ("id".equals(name) || "key".equals(name)) {
                            continue;
                        } else {
                            throw new LocationException(invocationNode.getLocation(),
                                    "Unsupported attribute: " + attributes.getQName(i));
                        }
                    } else {
                        attributeProperties.put(name, attributes.getValue(i));
                    }
                }
            }

            RenderCommand templateCommand = templateNode == null
                    ? emptyRenderCommand()
                    : componentNodeRenderCommand(templateNode, true);
            RenderCommand bodyCommand = invocationNode == null
                    ? emptyRenderCommand()
                    : asRenderCommand(invocationNode.getComponents());
            ComponentDefinition definition = componentDefinitionSource.get(type, id, key, templateCommand, bodyCommand);
            PropertyDefinitions propertyDefinitions = definition.getPropertyDefinitions();
            List<PropertyBinding> propertyBindings = new ArrayList<>();

            if (invocationNode != null) {
                for (ParameterTemplateNode parameterNode : invocationNode.getParameters()) {
                    if (!propertyDefinitions.contains(parameterNode.getName())) {
                        throw new LocationException(parameterNode.getLocation(),
                                "Unexpected property: " + parameterNode.getName());
                    }
                    PropertyDefinition propertyDefinition = propertyDefinitions.get(parameterNode.getName());
                    RenderCommand renderCommand = asRenderCommand(parameterNode.getComponents());
                    propertyBindings.add(new DefaultPropertyBinding(propertyDefinition, instance -> renderCommand));
                }
                for (Map.Entry<String, String> attribute : attributeProperties.entrySet()) {
                    if (!propertyDefinitions.contains(attribute.getKey())) {
                        throw new LocationException(invocationNode.getLocation(),
                                "Unexpected property: " + attribute.getKey());
                    }
                    PropertyDefinition propertyDefinition = propertyDefinitions.get(attribute.getKey());
                    Binding binding = asBinding(attribute.getValue(), propertyDefinition.getDefaultBindingPrefix());
                    propertyBindings.add(new DefaultPropertyBinding(propertyDefinition, binding));
                }
            }

            return writer -> {
                DefaultComponentInstances renderedChildren = new DefaultComponentInstances();
                ComponentInstance instance = definition.createInstance(propertyBindings, writer.getRootInstance(), renderedChildren);
                writer.startComponent(instance, renderedChildren::add);
                Controller controller = definition.getController();
                RenderState state = RenderState.SETUP_RENDER;
                try {
                    while (state != null) {
                        boolean proceed;
                        switch (state) {
                            case SETUP_RENDER:
                                proceed = controller.setupRender(instance, writer);
                                state = proceed ? RenderState.BEGIN_RENDER : RenderState.CLEANUP_RENDER;
                                break;
                            case BEGIN_RENDER:
                                proceed = controller.beginRender(instance, writer);
                                state = proceed ? RenderState.BEFORE_RENDER_TEMPLATE : RenderState.AFTER_RENDER;
                                break;
                            case BEFORE_RENDER_TEMPLATE:
                                proceed = controller.beforeRenderTemplate(instance, writer);
                                if (proceed) {
                                    templateCommand.render(writer);
                                    state = RenderState.BEFORE_RENDER_BODY;
                                } else {
                                    state = RenderState.AFTER_RENDER_TEMPLATE;
                                }
                                break;
                            case AFTER_RENDER_TEMPLATE:
                                proceed = controller.afterRenderTemplate(instance, writer);
                                state = proceed ? RenderState.AFTER_RENDER : RenderState.BEFORE_RENDER_TEMPLATE;
                                break;
                            case BEFORE_RENDER_BODY:
                                proceed = controller.beforeRenderBody(instance, writer);
                                if (proceed) {
                                    bodyCommand.render(writer);
                                }
                                state = RenderState.AFTER_RENDER_BODY;
                                break;
                            case AFTER_RENDER_BODY:
                                proceed = controller.afterRenderBody(instance, writer);
                                state = proceed ? RenderState.AFTER_RENDER_TEMPLATE : RenderState.BEFORE_RENDER_BODY;
                                break;
                            case AFTER_RENDER:
                                proceed = controller.afterRender(instance, writer);
                                state = proceed ? RenderState.CLEANUP_RENDER : RenderState.BEGIN_RENDER;
                                break;
                            case CLEANUP_RENDER:
                                proceed = controller.cleanupRender(instance, writer);
                                state = proceed ? null : RenderState.SETUP_RENDER;
                                break;
                            default:
                                throw new IllegalStateException("Unknown render state " + state);
                        }
                    }
                } finally {
                    writer.endComponent();
                }
            };
        } catch (Exception e) {
            throw new Exception("Error with " + type.getSimpleName(), e);
        }
    }

    private RenderCommand emptyRenderCommand() {
        return writer -> { };
    }

    private RenderCommand componentNodeRenderCommand(ComponentTemplateNode node, boolean templateRoot) throws Exception {
        Class<?> componentType = componentTypeResolver.getComponentType(node.getName());
        return componentRenderCommand(componentType, node, templateRoot);
    }

    private enum RenderState {
        SETUP_RENDER,
        BEGIN_RENDER,
        BEFORE_RENDER_TEMPLATE,
        AFTER_RENDER_TEMPLATE,
        BEFORE_RENDER_BODY,
        AFTER_RENDER_BODY,
        AFTER_RENDER,
        CLEANUP_RENDER
    }

    private RenderCommand asRenderCommand(List<ComponentTemplateNode> templateNodes) throws Exception {
        List<RenderCommand> commands = new ArrayList<>(templateNodes.size());
        for (ComponentTemplateNode node : templateNodes) {
            RenderCommand renderCommand = componentNodeRenderCommand(node, false);
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

    protected ComponentTemplate resolveComponentTemplate(Class<?> type) throws Exception {
        String templatePath = type.getName().replace('.', '/') + ".xml";
        Resource templateResource = new ClassloaderResource(type, templatePath);
        return templateResource.exists()
                ? templateParser.parse(templateResource)
                : null;
    }
}
