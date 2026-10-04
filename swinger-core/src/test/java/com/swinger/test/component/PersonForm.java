package com.swinger.test.component;

import com.swinger.annotation.Property;
import com.swinger.test.model.Person;
import lombok.Getter;

public class PersonForm {
    @Getter
    @Property(defaultBindingPrefix = "prop")
    private Person person;
}
