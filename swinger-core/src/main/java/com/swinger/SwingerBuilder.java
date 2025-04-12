package com.swinger;

import javax.xml.parsers.DocumentBuilder;
import java.util.*;

public class SwingerBuilder {
    private DocumentBuilder documentBuilder;
    private ClassLoader classLoader;
    private Converter attributeConverter;
    private BindingSource bindingSource;
    private final Map<String, TagHandler> tagHandlers = new LinkedHashMap<>();
    private final List<FieldResolver> fieldResolvers = new ArrayList<>();

    public SwingerBuilder documentBuilder(DocumentBuilder documentBuilder) {
        this.documentBuilder = documentBuilder;
        return this;
    }

    public SwingerBuilder classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public SwingerBuilder tagHandler(String tag, TagHandler handler) {
        tagHandlers.put(tag, handler);
        return this;
    }

    public SwingerBuilder bindingSource(BindingSource bindingSource) {
        this.bindingSource = bindingSource;
        return this;
    }

    public SwingerBuilder fieldResolver(FieldResolver resolver) {
        this.fieldResolvers.add(resolver);
        return this;
    }

    public SwingerBuilder attributeConverter(Converter attributeConverter) {
        this.attributeConverter = attributeConverter;
        return this;
    }

    public Swinger build() {
        Objects.requireNonNull(documentBuilder, "documentBuilder");
        Objects.requireNonNull(classLoader, "classLoader");
        Objects.requireNonNull(attributeConverter, "attributeConverter");
        Objects.requireNonNull(bindingSource, "bindingSource");
        return new Swinger(documentBuilder, classLoader, attributeConverter, bindingSource, tagHandlers, fieldResolvers);
    }
}
