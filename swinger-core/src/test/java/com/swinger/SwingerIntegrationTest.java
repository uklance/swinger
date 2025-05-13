package com.swinger;

import com.swinger.annotation.OnEvent;
import com.swinger.model.ComponentResources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SwingerIntegrationTest {

    private Swinger swinger;

    @BeforeEach
    public void beforeEach() {
        swinger = new TestSwingerBuilder().build();
    }

    public static class Events {
        private final List<String> events = new LinkedList<>();

        @OnEvent("button1")
        public void onButton1() {
            events.add("b1");
        }

        @OnEvent("button2")
        public void onButton2() {
            events.add("b2");
        }

        public List<String> getEvents() {
            return events;
        }
    }

    @Test
    public void testEvents() throws Exception {
        ComponentResources resources = swinger.createComponent(Events.class);
        assertThat(resources.getComponent()).isInstanceOf(JPanel.class);
        assertThat(resources.getController()).isInstanceOf(Events.class);
        Events events = (Events) resources.getController();
        JPanel panel = (JPanel) resources.getComponent();
        List<JButton> buttons = findChildComponents(panel, JButton.class);
        assertThat(buttons).hasSize(2);
        JButton button1 = buttons.get(0);
        JButton button2 = buttons.get(1);
        assertThat(button1.getText()).isEqualTo("button1");
        assertThat(button2.getText()).isEqualTo("button2");
        assertThat(events.getEvents()).hasSize(0);
        button1.doClick();
        button2.doClick();
        assertThat(events.getEvents()).containsExactly("b1", "b2");
    }

    private <T> List<T> findChildComponents(Container container, Class<T> componentClass) {
        return Arrays.stream(container.getComponents())
                .filter(c -> componentClass.isAssignableFrom(c.getClass()))
                .map(componentClass::cast)
                .collect(Collectors.toList());
    }
}
