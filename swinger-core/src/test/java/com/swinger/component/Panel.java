package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class Panel implements Controller {
    @Getter
    //@DelegateProperties
    private JPanel panel;

    private Object constraints;

    @Override
    public boolean beginRender(SwingWriter writer) {
        panel = new JPanel();
        return false;
    }

    @Override
    public boolean afterRender(SwingWriter writer) {
        writer.add(panel, constraints);
        return true;
    }
}
