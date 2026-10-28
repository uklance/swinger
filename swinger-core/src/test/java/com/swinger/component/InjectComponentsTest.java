package com.swinger.component;

import com.formdev.flatlaf.FlatDarkLaf;
import com.swinger.annotation.InjectComponents;
import com.swinger.api.ComponentParser;
import com.swinger.api.ComponentsRef;
import com.swinger.api.Registry;
import com.swinger.impl.DefaultSwingWriter;
import com.swinger.model.RenderCommand;
import com.swinger.test.TestRegistryBuilder;
import com.swinger.test.model.Person;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectComponentsTest {
    static {
        FlatDarkLaf.setup();
    }

    private ComponentParser componentParser;

    @BeforeEach
    public void beforeEach() {
        Registry registry = new TestRegistryBuilder().build();
        componentParser = registry.get(ComponentParser.class);
    }

    @Test
    public void test() throws Exception {
        RenderCommand renderCommand = componentParser.parse(Comp1.class);
        DefaultSwingWriter writer = new DefaultSwingWriter();
        renderCommand.render(writer);

        Comp1 test = (Comp1) writer.getRootInstance().getComponent();
        assertThat(test.getFirstNames().values())
                .extracting(l -> l.getLabel().getText())
                .containsExactly("Alice", "Bob", "Charlie");
        assertThat(test.getLastNames().values())
                .extracting(l -> l.getLabel().getText())
                .containsExactly("Smith", "Johnson", "Brown");
    }

    public static class Comp1 {
        @Getter
        @Setter
        private Person person;

        @Getter
        @InjectComponents("personFirstName")
        private ComponentsRef<Long, Label> firstNames;

        @Getter
        @InjectComponents("personLastName")
        private ComponentsRef<Long, Label> lastNames;

        @Getter
        private final List<Person> people = List.of(
                new Person(1L, "Alice", "Smith"),
                new Person(2L, "Bob", "Johnson"),
                new Person(3L, "Charlie", "Brown")
        );
    }
}