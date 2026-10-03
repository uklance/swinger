package com.swinger.component;

import com.swinger.annotation.*;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;

import javax.inject.Inject;
import java.util.Iterator;

public class ForEach {
    @Inject
    private ComponentInstance instance;

    @Property(required = true, defaultBindingPrefix = "prop")
    private Iterable<?> items;

    @Property(required = true, defaultBindingPrefix = "prop")
    private Binding item;

    private Iterator<?> iterator;

    @SetupRender
    boolean setupRender() {
        iterator = items.iterator();
        return iterator.hasNext();
    }

    @BeforeRenderBody
    void beforeRenderBody() throws Exception {
        item.set(instance.getRootComponent(), iterator.next());
    }

    @AfterRenderBody
    boolean afterRenderBody() {
        return !iterator.hasNext();
    }

    @AfterRender
    void afterRender() throws Exception {
        iterator = null;
        item.set(instance.getRootComponent(), null);
    }
}
