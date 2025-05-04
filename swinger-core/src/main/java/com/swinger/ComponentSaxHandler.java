package com.swinger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class ComponentSaxHandler extends DefaultHandler {
    private static final short NODE_TYPE_COMPONENT = 1;
    private static final short NODE_TYPE_PARAMETER = 2;
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

    @AllArgsConstructor
    static abstract class StackNode {
        private final short nodeType;

        public boolean isComponentNode() {
            return nodeType == NODE_TYPE_COMPONENT;
        }
        public boolean isParameterNode() {
            return nodeType == NODE_TYPE_PARAMETER;
        }
    }

    @Getter
    static class ComponentNode extends StackNode {
        private final ComponentResources componentResources;
        public ComponentNode(ComponentResources componentResources) {
            super(NODE_TYPE_COMPONENT);
            this.componentResources = componentResources;
        }

        public Object getController() {
            return componentResources.getController();
        }

        public Component getComponent() {
            return componentResources.getComponent();
        }
    }

    @Getter
    static class ParameterNode extends StackNode {
        private final List<Object> parameters = new ArrayList<>();

        public ParameterNode() {
            super(NODE_TYPE_PARAMETER);
        }

        public void addParameter(Object parameter) {
            parameters.add(parameter);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (PARAMETER_NAMESPACE.equals(uri)) {
                stack.push(new ParameterNode());
                return;
            }

            int idIndex = attributes.getIndex("id");
            String id = idIndex == -1 ? idGenerator.nextId(localName) : attributes.getValue(idIndex);
            String constraints = attributes.getValue("constraints");
            ComponentResources componentResources = componentFactory.create(localName, id, constraints, locator);
            if (stack.isEmpty()) {
                root = componentResources;
            }
            stack.push(new ComponentNode(componentResources));

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
        } catch (Exception e) {
            throw wrapException(localName, e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            StackNode currentNode = stack.pop();
            StackNode parentNode = stack.peek();
            if (parentNode != null) {
                if (parentNode.isComponentNode() && currentNode.isComponentNode()) {
                    Container parentContainer = (Container) ((ComponentNode) parentNode).getComponent();
                    ComponentResources currentResources = ((ComponentNode) currentNode).getComponentResources();
                    parentContainer.add(currentResources.getComponent(), currentResources.getConstraints());
                }
                if (parentNode.isParameterNode()) {
                    ParameterNode parameterNode = (ParameterNode) parentNode;
                    ComponentNode componentNode = (ComponentNode) currentNode;
                    parameterNode.addParameter(componentNode.getComponent());
                }
                if (currentNode.isParameterNode()) {
                    List<Object> parameterValues = ((ParameterNode) currentNode).getParameters();
                    if (parameterValues.size() == 1) {
                        Object controller = ((ComponentNode) parentNode).getController();
                        memberAccessor.setProperty(controller, localName, parameterValues.get(0));
                    } else {
                        String msg = String.format("Expected a single element, found %s", parameterValues.size());
                        throw new RuntimeException(msg);
                    }
                }
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