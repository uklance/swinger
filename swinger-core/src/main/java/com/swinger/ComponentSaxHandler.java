package com.swinger;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ComponentSaxHandler extends DefaultHandler {
    private static final String PARAMETER_NAMESPACE = "swinger:parameter";
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([^:]*):(.*)");
    private static final Set<String> RESERVED_ATTRIBUTES = Set.of("id", "constraints");

    private final Object controller;
    private final ComponentFactory componentFactory;
    private final BindingRegistry bindingRegistry;
    private final MemberAccessor memberAccessor;

    private final IdGenerator idGenerator;
    private final LinkedList<StackNode> stack = new LinkedList<>();
    private Locator locator;

    @Getter
    private ComponentResources root;

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    interface StackNode {
        void onChild(StackNode child) throws Exception;
    }

    @RequiredArgsConstructor
    @Getter
    class ComponentNode implements StackNode {
        private final ComponentResources componentResources;

        @Override
        public void onChild(StackNode childNode) throws Exception {
            if (childNode instanceof ComponentNode) {
                ComponentResources childResources = ((ComponentNode) childNode).getComponentResources();
                Container parentContainer = (Container) componentResources.getComponent();
                parentContainer.add(childResources.getComponent(), childResources.getConstraints());
            } else if (childNode instanceof ParameterNode) {
                ParameterNode parameterNode = (ParameterNode) childNode;
                if (!parameterNode.isSet()) {
                    String msg = String.format("Found zero elements for parameter '%s', expected 1", parameterNode.getName());
                    throw new RuntimeException(msg);
                }
                memberAccessor.setProperty(componentResources.getController(), parameterNode.getName(), parameterNode.getComponent());
            } else {
                throw new RuntimeException();
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    static class ParameterNode implements StackNode {
        private final String name;
        private boolean isSet = false;
        private ComponentResources componentResources;

        @Override
        public void onChild(StackNode child) {
            if (isSet) {
                String msg = String.format("Found multiple elements for parameter '%s', expected 1", name);
                throw new RuntimeException(msg);
            }
            componentResources = ((ComponentNode) child).getComponentResources();
            isSet = true;
        }

        public Component getComponent() {
            return componentResources.getComponent();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            StackNode stackNode = PARAMETER_NAMESPACE.equals(uri)
                    ? startParameter(uri, localName, qName, attributes)
                    : startComponent(uri, localName, qName, attributes);
            stack.push(stackNode);
        } catch (Exception e) {
            throw wrapException(localName, e);
        }
    }

    protected ParameterNode startParameter(String uri, String localName, String qName, Attributes attributes) {
        return new ParameterNode(localName);
    }

    protected ComponentNode startComponent(String uri, String localName, String qName, Attributes attributes) throws Exception {
        int idIndex = attributes.getIndex("id");
        String id = idIndex == -1 ? idGenerator.nextId(localName) : attributes.getValue(idIndex);
        String constraints = attributes.getValue("constraints");
        ComponentResources componentResources = componentFactory.create(localName, id, constraints, locator);

        if (root == null) {
            root = componentResources;
        }

        for (int i = 0; i < attributes.getLength(); i++) {
            String propName = attributes.getLocalName(i);
            if (RESERVED_ATTRIBUTES.contains(propName)) {
                continue;
            }
            String stringValue = attributes.getValue(i);
            Object value;
            Matcher matcher = ATTRIBUTE_PATTERN.matcher(stringValue);
            if (matcher.matches()) {
                Binding binding = bindingRegistry.get(matcher.group(1));
                value = binding.resolve(controller, matcher.group(2));
            } else {
                value = stringValue;
            }
            memberAccessor.setProperty(componentResources.getComponent(), propName, value);
        }
        return new ComponentNode(componentResources);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            StackNode currentNode = stack.pop();
            StackNode parentNode = stack.peek();
            if (parentNode != null) {
                parentNode.onChild(currentNode);
            }
        } catch (Exception e) {
            throw wrapException(localName, e);
        }
    }

    private SAXException wrapException(String localName, Exception e) {
        String msg = String.format("Error processing '%s'", localName);
        if (e instanceof LocationException) {
            return new SAXException(msg, e);
        } else {
            Location location = new Location(locator);
            return new SAXException(new LocationException(location, msg, e));
        }
    }
}