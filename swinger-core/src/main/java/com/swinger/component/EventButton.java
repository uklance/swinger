package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.EventManager;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.inject.Inject;
import javax.swing.*;

public class EventButton {
    @Getter
    @ProxyProperties(exclude = "setMnemonic")
    private final JButton button = new JButton();

    @Property(required = true)
    private String event;

    @Property
    private Object context;

    @Property
    private Object constraints;

    @Inject
    private EventManager eventManager;

    @BeforeRenderBody
    public boolean beforeRenderBody(SwingWriter writer) {
        button.addActionListener(e -> eventManager.publish(event, context));
        writer.startElement(button, constraints);
        return true;
    }

    @AfterRenderBody
    public void afterRenderBody(SwingWriter writer) {
        writer.endElement();
    }
}
