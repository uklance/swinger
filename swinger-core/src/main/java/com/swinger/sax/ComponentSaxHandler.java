package com.swinger.sax;

import com.swinger.model.Location;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.util.LinkedList;

public class ComponentSaxHandler extends DefaultHandler {
    private static final String PARAMETER_NAMESPACE = "swinger:parameter";

    private final LinkedList<AbstractTemplateNode> stack = new LinkedList<>();
    private Locator locator;

    @Getter private TemplateNode rootNode;

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes attributes) throws SAXException {
        AbstractTemplateNode node = PARAMETER_NAMESPACE.equals(uri)
                ? new ParameterTemplateNode(name, new AttributesImpl(attributes), new Location(locator))
                : new ComponentTemplateNode(name, new AttributesImpl(attributes), new Location(locator));
        stack.push(node);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        stack.peek().onCharacters(ch, start, length);
    }

    @Override
    public void endElement(String uri, String name, String qName) throws SAXException {
        AbstractTemplateNode node = stack.pop();
        if (stack.isEmpty()) {
            rootNode = node;
        } else {
            stack.peek().onChild(node);
        }
    }
}