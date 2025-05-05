package com.swinger;

import lombok.AllArgsConstructor;
import org.w3c.dom.Element;

import java.util.Map;

@AllArgsConstructor
public class TagElementHandlers implements ElementHandlers {
    private final Map<String, ElementHandler> handlersByTag;

    @Override
    public ElementHandler getElementHandler(Element element) {
        String tag = element.getTagName();
        ElementHandler handler = handlersByTag.get(tag);
        if (handler == null) {
            throw new RuntimeException(String.format("No element handlers for tag '%s'", tag));
        }
        return handler;
    }
}
