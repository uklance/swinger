package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.sax.ComponentTemplateNode;
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
import java.util.concurrent.TimeUnit;

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
        ComponentFactory componentFactoryProxy = new ComponentFactory() {
            @Override
            public ComponentResources create(ComponentResources resources, ComponentTemplateNode templateNode) throws Exception {
                return componentFactoryRef[0].create(resources, templateNode);
            }

            @Override
            public ComponentResources create(Class<? extends Controller> type) throws Exception {
                return componentFactoryRef[0].create(type);
            }
        };
        componentRenderer = new DefaultComponentRenderer(componentFactoryProxy);

        MemberAccessor memberAccessor = new ReflectionMemberAccessor();
        EventManager eventManager = new DefaultEventManager();
        List<ControllerFieldHandler> fieldHandlers = List.of();
        List<ControllerMethodHandler> methodHandlers = List.of(
                new OnEventControllerMethodHandler(eventManager)
        );
        ClassLoader classLoader = getClass().getClassLoader();
        Set<String> packages = Set.of("com.swinger.component");
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
        ComponentResources component = componentFactory.create(Panel1.class);
        DefaultSwingWriter writer = new DefaultSwingWriter();
        componentRenderer.render(component, writer);
        assertThat(writer.getRoots()).hasSize(1);
        JPanel panel = (JPanel) writer.getRoots().get(0);
        assertThat(panel.getComponents())
                .extracting(c -> c.getClass().getSimpleName())
                .containsExactly("JLabel", "JButton", "JSplitPane", "JButton");

        Panel1 root = (Panel1) component.getController();
        JLabel label1 = (JLabel) panel.getComponents()[0];
        JButton button1 = (JButton) panel.getComponents()[1];
        JButton button2 = (JButton) panel.getComponents()[3];
        assertThat(label1.getText()).isEqualTo("label1");
        button1.doClick();
        button2.doClick();
        assertThat(root.getEvents().poll(1, TimeUnit.SECONDS)).isEqualTo("button1");
        assertThat(root.getEvents().poll(1, TimeUnit.SECONDS)).isEqualTo("button2");
    }
}