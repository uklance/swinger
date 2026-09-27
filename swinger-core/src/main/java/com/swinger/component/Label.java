package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class Label {
    @Getter
    @ProxyProperties(exclude = "setDisplayedMnemonic")
    private JLabel label = new JLabel();

    @Property
    private Object constraints;

    @BeforeRenderBody
    public void beforeRenderBody(SwingWriter writer) {
        writer.startElement(label, constraints);
    }

    @AfterRenderBody
    public void afterRenderBody(SwingWriter writer) {
        writer.endElement();
    }
}
