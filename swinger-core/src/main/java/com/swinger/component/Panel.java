package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class Panel implements Controller {
    @Getter
    private JPanel panel;

    @Setter
    private LayoutManager layout;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        panel = new JPanel();
        if (layout != null) {
            panel.setLayout(layout);
        }
        writer.push(panel);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
