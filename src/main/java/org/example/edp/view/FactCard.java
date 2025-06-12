package org.example.edp.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class FactCard extends VBox {

    public FactCard(String type, String content, String author) {
        this.setSpacing(8);
        this.setPadding(new Insets(20));
        this.setMaxWidth(550);
        this.getStyleClass().add("fact-card");

        Label typeLabel = new Label(type.toUpperCase());
        typeLabel.getStyleClass().add("fact-card-type");

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);
        contentLabel.getStyleClass().add("fact-card-content");


        this.getChildren().addAll(typeLabel, contentLabel);

        if ("quote".equalsIgnoreCase(type) && author != null && !author.isBlank()) {
            Label authorLabel = new Label("— " + author);
            authorLabel.getStyleClass().add("fact-card-author");
            this.getChildren().add(authorLabel);
        }

    }
}