package com.swinger.test.component;

import com.swinger.annotation.InjectComponents;
import com.swinger.annotation.OnEvent;
import com.swinger.api.ComponentsRef;
import com.swinger.test.model.Person;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class KitchenSink {
    @Getter @Setter
    private Person person;

    @Getter
    private final List<Person> people = List.of(
        new Person(1L, "Alice", "Smith"),
        new Person(2L, "Bob", "Johnson"),
        new Person(3L, "Charlie", "Brown")
    );

    @InjectComponents("person")
    private ComponentsRef<Long, PersonForm> personForms;

    @OnEvent("saveSingle")
    public void onSaveSingle(Long personId) {
        log.info("Person forms: {}", personForms.keySet());
        log.info("Saving single person: {}", personId);
    }

    @OnEvent("saveAll")
    public void onSaveAll() {
        log.info("Saving all persons");
    }
}
