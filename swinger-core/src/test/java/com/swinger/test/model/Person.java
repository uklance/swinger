package com.swinger.test.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Person {
    private Long id;
    private String firstName;
    private String lastName;
}