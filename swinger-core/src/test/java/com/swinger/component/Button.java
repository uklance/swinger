package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

public class Button implements Controller {
    @Getter
    private JButton button;

    @Setter
    private String text;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        button = new JButton();
        button.setText(text);
        writer.push(button);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
