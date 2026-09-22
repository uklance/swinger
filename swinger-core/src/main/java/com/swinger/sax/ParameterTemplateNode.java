package com.swinger.sax;

import com.swinger.model.Location;
import lombok.Getter;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

public class ParameterTemplateNode extends AbstractTemplateNode {
    @Getter
    private final List<ComponentTemplateNode> components = new ArrayList<>();

    public ParameterTemplateNode(String name, Attributes attributes, Location location) {
        super(name, attributes, location);
    }

    @Override
    public void onChild(TemplateNode child) {
        components.add((ComponentTemplateNode) child);
    }

    @Override
    public Type getType() {
        return Type.PARAMETER;
    }
}
