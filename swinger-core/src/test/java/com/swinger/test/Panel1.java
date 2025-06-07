package com.swinger.test;

import com.swinger.api.Controller;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionListener;

@Slf4j
public class Panel1 implements Controller {

    public ActionListener getButton1Listener() {
        return event -> log.info("button1");
    }

    //@OnEvent("button2")
    public void onButton2() {
        log.info("button2");
    }
}
