package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

public class Label implements Controller {
    @Getter
    private JLabel label;

    @Setter
    private String text;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        label = new JLabel();
        label.setText(text);
        writer.push(label);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
