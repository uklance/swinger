package com.swinger.component;

import com.swinger.annotation.AfterRenderBody;
import com.swinger.annotation.BeforeRenderBody;
import com.swinger.annotation.Property;
import com.swinger.annotation.SetupRender;
import com.swinger.api.Binding;
import com.swinger.api.ComponentInstance;

import javax.inject.Inject;
import java.util.Iterator;

public class ForEach {
    @Inject
    private ComponentInstance componentInstance;

    @Property(required = true, defaultBindingPrefix = "prop")
    private Iterable<?> items;

    @Property(required = true, defaultBindingPrefix = "prop")
    private Binding item;

    private Iterator<?> iterator;

    @SetupRender
    public void setupRender() {
        iterator = items.iterator();
    }

    @BeforeRenderBody
    public boolean beforeRenderBody() throws Exception {
        if (!iterator.hasNext()) {
            return false;
        }
        item.set(componentInstance, iterator.next());
        return true;
    }

    @AfterRenderBody
    public boolean afterRenderBody() {
        return !iterator.hasNext();
    }
}
