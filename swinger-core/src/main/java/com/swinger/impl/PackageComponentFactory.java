package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.ComponentTemplateParser;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PackageComponentFactory extends AbstractComponentFactory {
    private final ClassLoader classLoader;
    private final Set<String> packages;

    public PackageComponentFactory(
            MemberAccessor memberAccessor,
            ComponentRenderer componentRenderer,
            BindingSourceRegistry bindingSourceRegistry,
            ComponentTemplateParser templateParser,
            List<ControllerFieldHandler> fieldHandlers,
            List<ControllerMethodHandler> methodHandlers,
            ClassLoader classLoader,
            Set<String> packages
    ) {
        super(memberAccessor, componentRenderer, bindingSourceRegistry, templateParser, fieldHandlers, methodHandlers);
        this.classLoader = classLoader;
        this.packages = packages;
    }

    @Override
    protected Class<? extends Controller> resolveControllerType(ComponentTemplateNode templateNode) {
        Class<? extends Controller> type = null;
        String name = templateNode.getName();
        String simpleName = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Iterator<String> pkgIt = packages.iterator(); pkgIt.hasNext() && type == null; ) {
            try {
                type = (Class<? extends Controller>) classLoader.loadClass(pkgIt.next() + '.' + simpleName);
            } catch (ClassNotFoundException e) {}
        }
        if (type == null) {
            String msg = String.format("Could not find type %s in packages %s", simpleName, packages.stream().collect(Collectors.joining(", ")));
            throw new RuntimeException(msg);
        }
        return type;
    }
}
