package com.swinger;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class ComponentResources {
    private final String id;
    private final Object controller;
    private final Component component;
    private final Object constraints;
}
