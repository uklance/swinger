package com.swinger.test;

import com.swinger.api.*;
import com.swinger.impl.*;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.SaxComponentTemplateParser;

import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.HashMap;
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
        Map<Class<?>, Object> registryInstances = new HashMap<>();
        Registry registry = new Registry() {
            @Override
            public <T> T get(Class<T> type) {
                Object instance = registryInstances.get(type);
                if (instance == null) {
                    throw new IllegalArgumentException("No instance registered for type: " + type.getName());
                }
                return type.cast(instance);
            }
        };
        registryInstances.put(EventManager.class, new DefaultEventManager());

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
        PropertyValueConverter propertyValueConverter = new DefaultPropertyValueConverter();
        PropertyDefinitionSource propertyDefinitionSource = new DefaultPropertyDefinitionSource(propertyValueConverter);
        ComponentInstanceSource componentInstanceSource = new DefaultComponentInstanceSource(
                List.of(new InjectComponentDecorator(registry))
        );
        ComponentDefinitionSource definitionSource = new DefaultComponentDefinitionSource(
                controllerSource,
                propertyDefinitionSource,
                componentInstanceSource
        );
        ComponentParser componentParser = new DefaultComponentParser(templateParser, bindingSourceRegistry, componentTypeResolver, definitionSource);

        registryInstances.put(ComponentParser.class, componentParser);
        registryInstances.put(BindingSourceRegistry.class, bindingSourceRegistry);
        registryInstances.put(ComponentTypeResolver.class, componentTypeResolver);
        registryInstances.put(ComponentDefinitionSource.class, definitionSource);
        registryInstances.put(PropertyDefinitionSource.class, propertyDefinitionSource);
        registryInstances.put(ComponentInstanceSource.class, componentInstanceSource);
        registryInstances.put(ControllerSource.class, controllerSource);
        registryInstances.put(PropertyValueConverter.class, propertyValueConverter);
        registryInstances.put(MemberAccessor.class, memberAccessor);
        registryInstances.put(ComponentTemplateParser.class, templateParser);
        return registry;
    }
}
