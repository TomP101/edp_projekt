package org.example.edp.model;

public class Fact {
    private String id;
    private String text;
    private String source;
    private String date;

    public Fact(String id, String text, String source, String date) {
        this.id = id;
        this.text = text;
        this.source = source;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getSource() {
        return source;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return text + " (" + source + ")";
    }
}
