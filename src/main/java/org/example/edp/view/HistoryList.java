package org.example.edp.view;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class HistoryList extends VBox {

    private final ComboBox<String> filterBox;
    private final VBox cardsContainer;
    private final ScrollPane scrollPane;

    public HistoryList() {
        this.setSpacing(15);
        this.setPadding(new Insets(25));
        this.getStyleClass().add("history-list");

        filterBox = new ComboBox<>();
        filterBox.getStyleClass().add("combo-box");
        filterBox.getItems().addAll("Wszystko", "Fakty", "Cytaty");
        filterBox.setValue("Wszystko");

        cardsContainer = new VBox(15);
        cardsContainer.getStyleClass().add("cards-container-history");
        cardsContainer.setPadding(new Insets(0, 5, 0, 0));

        scrollPane = new ScrollPane();
        scrollPane.setContent(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("history-scroll-pane");

        this.getChildren().addAll(filterBox, scrollPane);

    }

    public ComboBox<String> getFilterBox() {
        return filterBox;
    }

    public VBox getCardsContainer() {
        return cardsContainer;
    }

    public record FactCardData(String type, String content, String author) {}
}