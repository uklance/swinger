package com.swinger;

import lombok.AllArgsConstructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@AllArgsConstructor
public class EventBinding implements Binding {
    private final EventManager eventManager;

    @Override
    public Object resolve(Object controller, String value) throws Exception {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventManager.publish(value);
            }
        };
    }
}