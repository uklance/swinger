package com.swinger;

import javax.swing.*;

public interface ComponentSource {
    String getId();
    JComponent getComponent();
    String getConstraints();
}
