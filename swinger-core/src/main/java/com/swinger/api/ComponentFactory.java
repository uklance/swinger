package com.swinger.api;

import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.ParameterTemplateNode;

import java.util.List;

public interface ComponentFactory {
    ComponentResources create(String name, List<ParameterTemplateNode> parameters, List<ComponentTemplateNode> components) throws Exception;
}
