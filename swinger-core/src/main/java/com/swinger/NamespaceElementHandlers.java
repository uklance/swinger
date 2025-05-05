package com.swinger;

import lombok.AllArgsConstructor;
import org.w3c.dom.Element;

import java.util.Map;

@AllArgsConstructor
public class NamespaceElementHandlers implements ElementHandlers {
    private final ElementHandlers defaultElementHandlers;
    private final Map<String, ElementHandlers> handlersByNamespace;

    @Override
    public ElementHandler getElementHandler(Element element) {
        String namespace = element.getNamespaceURI();
        ElementHandlers namespaceHandlers = namespace == null
                ? defaultElementHandlers
                : handlersByNamespace.get(namespace);
        if (namespaceHandlers == null) {
            throw new RuntimeException(String.format("No element handlers for namespace '%s'", namespace));
        }
        return namespaceHandlers.getElementHandler(element);
    }

    /*
    public static class DefaultElementHandlersBuilder {
        private final Map<String, Map<String, ElementHandler>> handlersByNamespace = new LinkedHashMap<>();

        public DefaultElementHandlersBuilder handler(String namespace, String tag, ElementHandler handler) {
            Map<String, ElementHandler> handlers = handlersByNamespace.computeIfAbsent(namespace, ns -> new LinkedHashMap<>());
            handlers.put(tag, handler);
            return this;
        }

        public DefaultElementHandlersBuilder handler(String tag, ElementHandler handler) {
            return handler(null, tag, handler);
        }

        public NamespaceElementHandlers build() {
            return new NamespaceElementHandlers(handlersByNamespace);
        }
    }

    private final Map<String, Map<String, ElementHandler>> handlersByNamespace;

    public static DefaultElementHandlersBuilder builder() {
        return new DefaultElementHandlersBuilder();
    }

    @Override
    public ElementHandler getElementHandler(Element element) {
        String namespace = element.getNamespaceURI();
        String tag = element.getTagName();
        Map<String, ElementHandler> handlers = handlersByNamespace.get(namespace);
        if (handlers == null) {
            throw new RuntimeException(String.format("No element handlers for namespace '%s'", namespace));
        }
        ElementHandler handler = handlers.get(tag);
        if (handler == null) {
            throw new RuntimeException(String.format("No element handler for tag '%s'", tag));
        }
        return handler;
    }*/
}
