package com.swinger.test;

import com.swinger.annotation.OnEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class Panel1 {
    @Getter
    private BlockingQueue<String> events = new LinkedBlockingQueue<>();

    public ActionListener getButton1Listener() {
        return event -> events.add("button1");
    }

    @OnEvent("button2")
    public void onButton2() {
        events.add("button2");
    }
}
