package com.swinger;

import com.swinger.test.Panel1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwingerTest {
    private Swinger swinger;

    @BeforeEach
    public void beforeEach() throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Binding literalBinding = new LiteralBinding();
        EventManager eventManager = new DefaultEventManager();
        Map<String, Binding> bindingMap = Map.of(
                "literal", literalBinding,
                "prop", new PropBinding(),
                "event", new EventBinding(eventManager)
        );
        BindingSource bindingSource = new DefaultBindingSource(bindingMap, literalBinding);
        Converter converter = new DefaultConverter();
        ServiceRegistry registry = new DefaultServiceRegistry(Collections.emptyMap());
        List<FieldResolver> fieldResolvers = Collections.emptyList();
        List<MethodHandler> methodHandlers = List.of(
                new OnEventMethodHandler(eventManager)
        );
        Map<String, TagHandler> tagHandlers = new HashMap<>();
        tagHandlers.put("label", new DefaultTagHandler(bindingSource, converter, JLabel.class));
        tagHandlers.put("panel", new DefaultTagHandler(bindingSource, converter, JPanel.class));
        tagHandlers.put("button", new DefaultTagHandler(bindingSource, converter, JButton.class));
        tagHandlers.put("textField", new DefaultTagHandler(bindingSource, converter, JTextField.class));
        swinger = new Swinger(documentBuilder, SwingerTest.class.getClassLoader(), registry, fieldResolvers, methodHandlers, tagHandlers);
    }

    @Test
    public void testPanel1() throws Exception {
        ComponentSource panel1Source = swinger.build(Panel1.class);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);

        frame.getContentPane().add(panel1Source.getComponent());
        //frame.pack();
        frame.setVisible(true);

        Thread.sleep(20_000);
    }
}