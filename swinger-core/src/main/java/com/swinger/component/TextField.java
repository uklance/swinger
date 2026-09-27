package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.annotation.Property;
import com.swinger.annotation.ProxyProperties;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class TextField {
    @Getter
    @ProxyProperties(exclude = "setDisplayedMnemonic", defaultBindingPrefix = "literal")
    private JTextField textField = new JTextField();

    @Property
    private Object constraints;

    @Property
    private boolean bind;

    @BeforeRenderBody
    public void beforeRenderBody(SwingWriter writer) {
        writer.startElement(textField, constraints);
    }

    @AfterRenderBody
    public void afterRenderBody(SwingWriter writer) {
        writer.endElement();
    }
}
