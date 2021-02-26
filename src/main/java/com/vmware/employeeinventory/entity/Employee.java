package com.vmware.employeeinventory.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name="TBL_EMPLOYEES")
@Getter
@Setter
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name="name")
    private String name;

    @Column(name="age")
    private String age;

    public Employee() {
    }

    public Employee(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public Employee(UUID id, String name, String age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
