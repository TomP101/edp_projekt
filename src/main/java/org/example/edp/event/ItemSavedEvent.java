package org.example.edp.event;


public class ItemSavedEvent {
    private final String type;
    private final String content;
    private final String author;

    public ItemSavedEvent(String type, String content, String author) {
        this.type = type;
        this.content = content;
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "ItemSavedEvent{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}