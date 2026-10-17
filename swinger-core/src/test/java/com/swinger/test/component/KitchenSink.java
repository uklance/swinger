package com.swinger.test.component;

import com.swinger.annotation.OnEvent;
import com.swinger.test.model.Person;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class KitchenSink {
    @Getter @Setter
    private Person person;

    @Getter
    private final List<Person> people = List.of(
        new Person("Alice", "Smith"),
        new Person("Bob", "Johnson"),
        new Person("Charlie", "Brown")
    );

    @OnEvent("saveSingle")
    public void onSaveSingle(Person person) {
        log.info("Saving single person: {}", person);
    }

    @OnEvent("saveAll")
    public void onSaveAll() {
        log.info("Saving all persons");
    }
}
