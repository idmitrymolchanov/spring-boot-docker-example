package org.example.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Employee {
    private String id;
    private String firstName;
    private String lastName;
    private int age;

    @Override
    public String toString() {
        return "Employee [id=" + id + ", firstName=" +
                firstName + ", lastName = " + lastName + ", age = " + age + "]";
    }
}
