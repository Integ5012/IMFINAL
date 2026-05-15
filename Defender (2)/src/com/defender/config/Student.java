package com.defender.config;

public class Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;

    public Student(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-15s | %s", firstName, lastName, email);
    }
}