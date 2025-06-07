package com.swinger.sax;

import com.swinger.model.Location;
import lombok.Getter;
import org.xml.sax.Attributes;

public class ParameterTemplateNode extends AbstractTemplateNode {
    @Getter
    private ComponentTemplateNode component;

    public ParameterTemplateNode(String name, Attributes attributes, Location location) {
        super(name, attributes, location);
    }

    @Override
    public void onChild(TemplateNode child) {
        if (component != null) {
            throw new RuntimeException("Multiple children for parameter " + getName());
        }
        component = (ComponentTemplateNode) child;
    }

    @Override
    public Type getType() {
        return Type.PARAMETER;
    }
}
