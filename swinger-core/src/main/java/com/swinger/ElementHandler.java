package com.swinger;

import org.w3c.dom.Element;

public interface ElementHandler {
    ComponentSource handle(Element element, SwingerContext context) throws Exception;
}
