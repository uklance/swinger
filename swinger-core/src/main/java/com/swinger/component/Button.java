package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Optional;

public class Button implements Controller {
    @Getter
    private JButton button;

    @Setter
    private String text;

    @Setter
    private ActionListener actionListener;

    @Setter
    private Object constraints;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        button = new JButton();
        Optional.ofNullable(text).ifPresent(button::setText);
        Optional.ofNullable(actionListener).ifPresent(button::addActionListener);
        writer.push(button, constraints);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
