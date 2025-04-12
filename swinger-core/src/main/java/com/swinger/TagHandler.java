package com.swinger;

import org.w3c.dom.Element;

import javax.swing.*;

public interface TagHandler {
    ComponentSource handle(Element element, SwingerContext context) throws Exception;
}
