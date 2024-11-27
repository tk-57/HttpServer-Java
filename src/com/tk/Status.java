package com.tk;

public enum Status {
    OK("200 OK"),
    NOT_FOUND("404 Not Found")
    ;
    
    private final String text;
    
    private Status(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
