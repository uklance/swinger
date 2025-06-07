package com.swinger.impl;

import com.swinger.api.*;
import com.swinger.io.ClassloaderResource;
import com.swinger.sax.ComponentTemplate;
import com.swinger.sax.ComponentTemplateParser;
import com.swinger.sax.SaxComponentTemplateParser;
import com.swinger.test.Panel1;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultComponentRendererTest {
    private ComponentRenderer componentRenderer;
    private ComponentTemplateParser templateParser;
    private ComponentParser componentParser;
    private ComponentFactory componentFactory;

    @BeforeEach
    public void beforeEach() {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setNamespaceAware(true);

        templateParser = new SaxComponentTemplateParser(saxParserFactory);
        componentParser = new DefaultComponentParser(templateParser, List.of(), List.of());
        componentFactory = new PackageComponentFactory(getClass().getClassLoader(), componentParser, List.of("com.swinger.component"));
        componentRenderer = new DefaultComponentRenderer(componentFactory);
    }

    @Test
    public void test() throws Exception {
        ComponentTemplate template = parseTemplate("com/swinger/test/Panel1.xml");
        Controller controller = new Panel1();
        ComponentResources component = new DefaultComponentResources(controller, template);

        TestSwingWriter writer = new TestSwingWriter();
        componentRenderer.render(component, writer);
        JPanel panel = (JPanel) writer.getRoot();
        assertThat(panel.getComponents())
                .extracting(c -> c.getClass().getSimpleName())
                .containsExactly("foo");
    }

    private ComponentTemplate parseTemplate(String path) throws Exception {
        return templateParser.parse(new ClassloaderResource(getClass(), path));
    }

    public static class TestSwingWriter implements SwingWriter {
        @Getter private Component root;
        private final LinkedList<Component> stack = new LinkedList<>(); 

        @Override
        public void push(Component component) {
            push(component, null);
        }

        @Override
        public void push(Component component, Object constraints) {
            if (stack.isEmpty()) {
                if (root != null) {
                    throw new RuntimeException("Multiple roots");
                }
                root = component;
            } else {
                Container container = (Container) stack.peek();
                if (constraints != null) {
                    container.add(component, constraints);
                } else {
                    container.add(component);
                }
            }
            stack.add(component); 
        }
        
        @Override
        public Component pop() {
            return stack.pop();
        }
    }
}