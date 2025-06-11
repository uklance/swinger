package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.sax.ComponentTemplate;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.SaxComponentTemplateParser;
import com.swinger.test.Panel1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultComponentRendererTest {
    private ComponentRenderer componentRenderer;
    private ComponentTemplateParser templateParser;
    private ComponentFactory componentFactory;

    @BeforeEach
    public void beforeEach() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);

        templateParser = new SaxComponentTemplateParser(saxParserFactory);
        ComponentFactory[] componentFactoryRef = new ComponentFactory[1];
        ComponentFactory componentFactoryProxy = templateNode -> componentFactoryRef[0].create(templateNode);
        componentRenderer = new DefaultComponentRenderer(componentFactoryProxy);

        MemberAccessor memberAccessor = new ReflectionMemberAccessor();
        List<ControllerFieldHandler> fieldHandlers = List.of();
        List<ControllerMethodHandler> methodHandlers = List.of();
        ClassLoader classLoader = getClass().getClassLoader();
        Set<String> packages = Set.of("com.swinger.component");
        EventManager eventManager = new DefaultEventManager();
        BindingSourceRegistry bindingSourceRegistry = new DefaultBindingSourceRegistry(Map.of(
                "new", new NewBindingSource(),
                "event", new EventBindingSource(eventManager),
                "prop", new PropertyBindingSource(memberAccessor),
                "literal", new LiteralBindingSource()
        ));

        componentFactory = new PackageComponentFactory(
                memberAccessor,
                componentRenderer,
                bindingSourceRegistry,
                templateParser,
                fieldHandlers,
                methodHandlers,
                classLoader,
                packages
        );
        componentFactoryRef[0] = componentFactory;
    }

    @Test
    public void test() throws Exception {
        ComponentTemplate template = parseTemplate("com/swinger/test/Panel1.xml");
        Controller controller = new Panel1();
        ComponentResources component = new DefaultComponentResources(controller, template);

        DefaultSwingWriter writer = new DefaultSwingWriter();
        componentRenderer.render(component, writer);
        assertThat(writer.getRoots()).hasSize(1);
        JPanel panel = (JPanel) writer.getRoots().get(0);
        assertThat(panel.getComponents())
                .extracting(c -> c.getClass().getSimpleName())
                .containsExactly("JLabel", "JButton", "JSplitPane", "JButton");

        JLabel label0 = (JLabel) panel.getComponents()[0];
        assertThat(label0.getText()).isEqualTo("foo");
    }

    private ComponentTemplate parseTemplate(String path) throws Exception {
        return templateParser.parse(new ClassloaderResource(getClass(), path));
    }
}