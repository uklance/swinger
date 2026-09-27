package com.swinger.test;

import com.swinger.annotation.Property;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class KitchenSink {
    @AllArgsConstructor
    @Getter
    public static class Person {
        private String firstName;
        private String lastName;
    }

    @Property
    private Person person;

    @Property
    private List<Person> people = List.of(
        new Person("Alice", "Smith"),
        new Person("Bob", "Johnson"),
        new Person("Charlie", "Brown")
    );
}
