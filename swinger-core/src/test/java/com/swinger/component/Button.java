package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class Button implements Controller {
    @Getter
    //@DelegateProperties
    private JButton button;

    private Object constraints;

    @Override
    public boolean beginRender(SwingWriter writer) {
        button = new JButton();
        return false;
    }

    @Override
    public boolean afterRender(SwingWriter writer) {
        writer.add(button, constraints);
        return true;
    }
}
