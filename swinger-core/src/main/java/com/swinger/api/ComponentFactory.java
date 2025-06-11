package com.swinger.api;

import com.swinger.sax.ComponentTemplateNode;

public interface ComponentFactory {
    ComponentResources create(ComponentResources resources, ComponentTemplateNode templateNode) throws Exception;
    ComponentResources create(Class<? extends Controller> type) throws Exception;
}
