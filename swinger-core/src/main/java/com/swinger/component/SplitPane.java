package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.api.SwingWriter;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class SplitPane {
    @Getter
    private JSplitPane splitPane;

    @Setter
    private Object constraints;

    @Setter
    private Component leftComponent;

    @Setter
    private Component rightComponent;

    @BeforeRenderBody
    public boolean beforeRenderBody(SwingWriter writer) {
        splitPane = new JSplitPane();
        Optional.ofNullable(leftComponent).ifPresent(splitPane::setLeftComponent);
        Optional.ofNullable(rightComponent).ifPresent(splitPane::setRightComponent);
        writer.startElement(splitPane, constraints);
        return true;
    }

    @AfterRenderBody
    public boolean afterRenderBody(SwingWriter writer) {
        writer.endElement();
        return true;
    }
}
