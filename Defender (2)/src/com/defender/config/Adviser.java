package com.defender.config;

public class Adviser {
    private int id;
    private String firstName;
    private String lastName;
    private String email;

    public Adviser(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s %s | Email: %s", id, firstName, lastName, email);

    }
}
