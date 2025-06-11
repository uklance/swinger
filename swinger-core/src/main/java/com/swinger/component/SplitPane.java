package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class SplitPane implements Controller {
    @Getter
    private JSplitPane splitPane;

    @Setter
    private Object constraints;

    @Setter
    private Component leftComponent;

    @Setter
    private Component rightComponent;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        splitPane = new JSplitPane();
        Optional.ofNullable(leftComponent).ifPresent(splitPane::setLeftComponent);
        Optional.ofNullable(rightComponent).ifPresent(splitPane::setRightComponent);
        writer.push(splitPane, constraints);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
