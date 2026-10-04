package com.swinger.test.component;

import com.swinger.test.model.Person;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class KitchenSink {
    @Getter @Setter
    private Person person;

    @Getter
    private final List<Person> people = List.of(
        new Person("Alice", "Smith"),
        new Person("Bob", "Johnson"),
        new Person("Charlie", "Brown")
    );
}
