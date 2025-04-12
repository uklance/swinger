package com.swinger;

import com.swinger.test.Panel1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

public class SwingerTest {
    private Swinger swinger;

    @BeforeEach
    public void beforeEach() throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Binding literalBinding = new LiteralBinding();
        Map<String, Binding> bindingMap = Map.of("literal", literalBinding, "prop", new PropBinding());
        BindingSource bindingSource = new DefaultBindingSource(bindingMap, literalBinding);
        swinger = Swinger.builder()
                .classLoader(SwingerTest.class.getClassLoader())
                .documentBuilder(documentBuilder)
                .attributeConverter(new DefaultConverter())
                .bindingSource(bindingSource)
                .tagHandler("label", new DefaultTagHandler(JLabel.class))
                .tagHandler("panel", new DefaultTagHandler(JPanel.class))
                .tagHandler("button", new DefaultTagHandler(JButton.class))
                .tagHandler("textField", new DefaultTagHandler(JTextField.class))
                .build();
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