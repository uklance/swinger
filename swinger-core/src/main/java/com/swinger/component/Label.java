package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

public class Label {
    @Getter
    private JLabel label;

    @Setter
    private String text;

    @BeforeRenderBody
    public boolean beforeRenderBody(SwingWriter writer) {
        label = new JLabel();
        label.setText(text);
        writer.push(label);
        return true;
    }

    @AfterRenderBody
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
