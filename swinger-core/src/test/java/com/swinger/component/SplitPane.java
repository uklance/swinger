package com.swinger.component;

import com.swinger.api.Controller;
import com.swinger.api.SwingWriter;
import lombok.Getter;

import javax.swing.*;

public class SplitPane implements Controller {
    @Getter
    private JSplitPane splitPane;

    @Override
    public boolean beforeRenderBody(SwingWriter writer) {
        splitPane = new JSplitPane();
        writer.push(splitPane);
        return true;
    }

    @Override
    public boolean afterRenderBody(SwingWriter writer) {
        writer.pop();
        return true;
    }
}
