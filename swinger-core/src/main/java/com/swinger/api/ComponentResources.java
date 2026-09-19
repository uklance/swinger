package com.swinger.api;

import com.swinger.sax.ComponentTemplate;

public interface ComponentResources {
    ComponentResources getRoot();
    ComponentResources getParent();
    Object getController();
    ComponentTemplate getTemplate();
}
