package com.swinger.impl;

import com.swinger.api.Binding;
import com.swinger.api.BindingSource;
import com.swinger.api.ComponentResources;
import com.swinger.api.EventManager;
import lombok.AllArgsConstructor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@AllArgsConstructor
public class EventBindingSource implements BindingSource {
    private final EventManager eventManager;

    @Override
    public Binding create(String name, ComponentResources resources) {
        return () -> new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eventManager.publish(name);
            }
        };
    }
}