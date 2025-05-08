package com.swinger;

import com.swinger.api.*;
import com.swinger.impl.*;
import com.swinger.model.ComponentResources;
import com.swinger.test.Panel1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.Map;

public class SwingerTest {
    private Swinger swinger;

    @BeforeEach
    public void beforeEach() throws Exception {
        EventManager eventManager = new DefaultEventManager();
        MemberAccessor memberAccessor = new ReflectionMemberAccessor();
        Map<String, ComponentSource> componentSources = Map.of(
                "panel", new SupplierComponentSource(JPanel::new),
                "label", new SupplierComponentSource(JLabel::new),
                "button", new SupplierComponentSource(JButton::new),
                "text-field", new SupplierComponentSource(JTextField::new),
                "text-area", new SupplierComponentSource(JTextArea::new),
                "split-pane", new SupplierComponentSource(JSplitPane::new)
        );
        List<ControllerFieldHandler> fieldHandlers = List.of();
        List<ControllerMethodHandler> methodHandlers = List.of(
                new OnEventComponentMethodHandler(eventManager)
        );
        ComponentFactory componentFactory = new DefaultComponentFactory(componentSources);
        BindingRegistry bindingRegistry = new DefaultBindingRegistry(Map.of(
                "prop", new PropertyBinding(memberAccessor),
                "event", new EventBinding(eventManager),
                "new", new NewBinding(),
                "literal", new LiteralBinding()
        ));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();

        swinger = new Swinger(componentFactory, bindingRegistry, memberAccessor, parser, fieldHandlers, methodHandlers);
    }

    @Test
    public void testPanel1() throws Exception {
        ComponentResources panel1Resources = swinger.createComponent(Panel1.class);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        frame.getContentPane().add(panel1Resources.getComponent());
        //frame.pack();
        frame.setVisible(true);

        Thread.sleep(20_000);
    }
}