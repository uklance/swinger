package com.swinger.test;

import java.awt.event.ActionListener;

public class Panel1 {

    public ActionListener getButton1Listener() {
        return event -> System.out.println("button1: " + event);
    }
}
