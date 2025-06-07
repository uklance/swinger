package com.swinger.sax;

import com.swinger.model.Location;
import org.xml.sax.Attributes;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ComponentTemplateNode extends AbstractTemplateNode {
    private List<ParameterTemplateNode> parameters;
    private List<ComponentTemplateNode> components;

    public ComponentTemplateNode(String name, Attributes attributes, Location location) {
        super(name, attributes, location);
    }

    @Override
    public void onChild(TemplateNode child) {
        switch (child.getType()) {
            case COMPONENT:
                if (components == null) {
                    components = new LinkedList<>();
                }
                components.add((ComponentTemplateNode) child);
                break;
            case PARAMETER:
                if (parameters == null) {
                    parameters = new LinkedList<>();
                }
                parameters.add((ParameterTemplateNode) child);
                break;
            default:
                throw new RuntimeException(String.format("Unsupported node type %s", child.getType()));
        }
    }

    public List<ParameterTemplateNode> getParameters() {
        return parameters == null ? Collections.emptyList() : parameters;
    }

    public List<ComponentTemplateNode> getComponents() {
        return components == null ? Collections.emptyList() : components;
    }

    @Override
    public Type getType() {
        return Type.COMPONENT;
    }
}
