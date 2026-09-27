package com.swinger.test;

import com.swinger.annotation.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class KitchenSink {
    @AllArgsConstructor
    @Getter
    public static class Person {
        private String firstName;
        private String lastName;
    }

    @Getter @Setter
    private Person person;

    @Getter
    private final List<Person> people = List.of(
        new Person("Alice", "Smith"),
        new Person("Bob", "Johnson"),
        new Person("Charlie", "Brown")
    );
}
