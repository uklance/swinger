package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Optional;

public class Button {
    @Getter
    private JButton button;

    @Setter
    private String text;

    @Setter
    private ActionListener actionListener;

    @Setter
    private Object constraints;

    @BeforeRenderBody
    public boolean beforeRenderBody(SwingWriter writer) {
        button = new JButton();
        Optional.ofNullable(text).ifPresent(button::setText);
        Optional.ofNullable(actionListener).ifPresent(button::addActionListener);
        writer.startElement(button, constraints);
        return true;
    }

    @AfterRenderBody
    public boolean afterRenderBody(SwingWriter writer) {
        writer.endElement();
        return true;
    }
}
