package com.swinger.test;

import com.swinger.api.ComponentParser;
import com.swinger.api.Registry;
import com.swinger.impl.DefaultSwingWriter;
import com.swinger.model.RenderCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.awt.Component;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class KitchenSinkTest {
    private ComponentParser componentParser;

    @BeforeEach
    public void beforeEach() {
        Registry registry = new TestRegistryBuilder().build();
        componentParser = registry.get(ComponentParser.class);
    }

    @Test
    public void testKitchenSink() throws Exception {
        RenderCommand renderCommand = componentParser.parse(KitchenSink.class);
        DefaultSwingWriter writer = new DefaultSwingWriter();
        renderCommand.render(writer);
        List<Component> rootElements = writer.getRootElements();
        assertThat(rootElements).hasSize(4);
    }
}
