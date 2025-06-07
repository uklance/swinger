package com.swinger.impl;

import com.swinger.api.ComponentFactory;
import com.swinger.api.ComponentParser;
import com.swinger.api.ComponentResources;
import com.swinger.api.Controller;
import com.swinger.sax.ComponentTemplateNode;
import com.swinger.sax.ParameterTemplateNode;
import lombok.AllArgsConstructor;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PackageComponentFactory implements ComponentFactory {
    private final ClassLoader classLoader;
    private final ComponentParser componentParser;
    private final List<String> packages;

    @Override
    public ComponentResources create(String name, List<ParameterTemplateNode> parameters, List<ComponentTemplateNode> components) throws Exception {
        Class<? extends Controller> type = null;
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
        return componentParser.parse(type);
    }
}
