package com.swinger;

import com.swinger.api.*;
import com.swinger.impl.IdGenerator;
import com.swinger.model.ComponentResources;
import lombok.AllArgsConstructor;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import java.io.InputStream;

@AllArgsConstructor
public class Swinger {
    private final ComponentFactory componentFactory;
    private final BindingRegistry bindingRegistry;
    private final MemberAccessor memberAccessor;
    private final SAXParser parser;

    public ComponentResources createComponent(Class<?> controllerType) throws Exception {
        return createComponent(controllerType, new IdGenerator());
    }

    public ComponentResources createComponent(Class<?> controllerType, IdGenerator idGenerator) throws Exception {
        String xmlPath = String.format("%s.xml", controllerType.getName().replace('.', '/'));
        Object controller = controllerType.getDeclaredConstructor().newInstance();
        ComponentSaxHandler saxHandler = new ComponentSaxHandler(controller, componentFactory, bindingRegistry, memberAccessor, idGenerator);
        try (InputStream in = controllerType.getClassLoader().getResourceAsStream(xmlPath)) {
            if (in == null) {
                throw new RuntimeException("No such resource " + xmlPath);
            }
            InputSource inputSource = new InputSource(in);
            inputSource.setSystemId(xmlPath);
            parser.parse(inputSource, saxHandler);
        }
        return saxHandler.getRoot();
    }
}
