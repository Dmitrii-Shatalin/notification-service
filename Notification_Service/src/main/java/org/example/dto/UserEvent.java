package org.example.dto;

public class UserEvent {
    public static final String CREATE = "CREATE";
    public static final String DELETE = "DELETE";

    private String email;
    private String operation;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}