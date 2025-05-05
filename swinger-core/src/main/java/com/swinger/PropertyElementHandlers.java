package com.swinger;

import org.w3c.dom.Element;

public class PropertyElementHandlers implements ElementHandlers {
    @Override
    public ElementHandler getElementHandler(Element element) {
        return (el, context) -> {
            context.getParent()
        };
    }
}
