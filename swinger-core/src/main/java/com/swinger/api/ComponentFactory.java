package com.swinger.api;

import com.swinger.sax.ComponentTemplateNode;

public interface ComponentFactory {
    ComponentResources create(ComponentTemplateNode templateNode) throws Exception;
}
