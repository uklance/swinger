package com.swinger.test;

import com.swinger.api.*;
import com.swinger.impl.*;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.SaxComponentTemplateParser;

import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.Map;

public class TestRegistryBuilder {
    private List<String> packages = List.of("com.swinger.component", "com.swinger.test.component");
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public TestRegistryBuilder withPackages(List<String> packages) {
        this.packages = packages;
        return this;
    }

    public TestRegistryBuilder withClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public Registry build() {
        MemberAccessor memberAccessor = new ReflectionMemberAccessor();
        Map<String, BindingSource> bindingsSources = Map.of(
        "prop", new PropertyBindingSource(memberAccessor),
        "literal", new LiteralBindingSource(),
        "new", new NewBindingSource()
        );
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        ComponentTemplateParser templateParser = new SaxComponentTemplateParser(saxParserFactory);
        BindingSourceRegistry bindingSourceRegistry = new DefaultBindingSourceRegistry(bindingsSources);
        ComponentTypeResolver componentTypeResolver = new PackageComponentTypeResolver(classLoader, packages);
        ControllerSource controllerSource = new DefaultControllerSource();
        ComponentDefinitionSource definitionSource = new DefaultComponentDefinitionSource(controllerSource);
        ComponentParser componentParser = new DefaultComponentParser(templateParser, bindingSourceRegistry, componentTypeResolver, definitionSource);

        Map<Class<?>, Object> registry = Map.of(
            ComponentParser.class, componentParser,
            BindingSourceRegistry.class, bindingSourceRegistry,
            ComponentTypeResolver.class, componentTypeResolver,
            ComponentDefinitionSource.class, definitionSource,
            ControllerSource.class, controllerSource,
            MemberAccessor.class, memberAccessor,
            ComponentTemplateParser.class, templateParser
        );

        return new Registry() {
            @Override
            public <T> T get(Class<T> type) {
                if (registry.containsKey(type)) {
                    return type.cast(registry.get(type));
                }
                throw new IllegalArgumentException("No instance registered for type: " + type.getName());
            }
        };
    }
}
