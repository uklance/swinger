package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

public class Panel {
    @Getter
    private JPanel panel;

    @Setter
    private LayoutManager layout;

    @BeforeRenderBody
    public boolean beforeRenderBody(SwingWriter writer) {
        panel = new JPanel();
        if (layout != null) {
            panel.setLayout(layout);
        }
        writer.push(panel);
        return true;
    }

    @AfterRenderBody
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
