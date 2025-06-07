package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class Panel implements Controller {
    @Getter
    private JPanel panel;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        panel = new JPanel();
        writer.push(panel);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
