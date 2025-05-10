package com.swinger.test;

import com.swinger.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ButtonABC {
    @OnEvent("buttonA")
    public void onButtonA() {
        log.info("buttonA");
    }

    @OnEvent("buttonB")
    public void onButtonB() {
        log.info("buttonB");
    }

    @OnEvent("buttonC")
    public void onButtonC() {
        log.info("buttonC");
    }
}
