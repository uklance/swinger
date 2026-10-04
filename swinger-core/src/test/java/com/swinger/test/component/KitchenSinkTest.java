package com.swinger.test.component;

import com.swinger.api.ComponentParser;
import com.swinger.api.Registry;
import com.swinger.impl.DefaultSwingWriter;
import com.swinger.model.RenderCommand;
import com.swinger.test.TestRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class KitchenSinkTest {
    private ComponentParser componentParser;

    @BeforeEach
    public void beforeEach() {
        Registry registry = new TestRegistryBuilder().build();
        componentParser = registry.get(ComponentParser.class);
    }

    @Test
    public void testKitchenSink() throws Exception {
        RenderCommand renderCommand = componentParser.parse(KitchenSink.class);
        DefaultSwingWriter writer = new DefaultSwingWriter();
        renderCommand.render(writer);
        List<Component> rootElements = writer.getRootElements();
        assertThat(rootElements).hasSize(1);
        JPanel rootPanel = (JPanel) rootElements.get(0);
        assertThat(rootPanel.getComponents()).extracting(c -> c.getClass().getSimpleName())
                .containsExactly("JPanel", "JPanel", "JPanel", "JButton");
        JPanel person1Panel= (JPanel) rootPanel.getComponents()[1];
        Map<Class<?>, List<Component>> componentMap = Arrays.stream(person1Panel.getComponents())
                .collect(Collectors.groupingBy(Component::getClass));
        List<Component> labels = componentMap.get(JLabel.class);
        assertThat(labels)
                .hasSize(2)
                .extracting(c -> ((JLabel) c).getText())
                .containsExactly("First Name", "Last Name");
        List<Component> textFields = componentMap.get(JTextField.class);
        assertThat(textFields)
                .hasSize(2)
                .extracting(c -> ((JTextField) c).getText())
                .containsExactly("Bob", "Johnson");
        List<Component> buttons = componentMap.get(JButton.class);
        assertThat(buttons)
                .hasSize(1)
                .extracting(c -> ((JButton) c).getText())
                .containsExactly("Save Single");

        JFrame jframe = new JFrame();
        jframe.add(rootPanel);
        jframe.pack();
        jframe.setVisible(true);
        Thread.sleep(2_000);
    }
}
