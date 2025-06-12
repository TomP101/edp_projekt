package org.example.edp.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.example.edp.model.DatabaseService;
import org.example.edp.service.EventBus;
import org.example.edp.event.ItemSavedEvent;
import org.example.edp.event.Subscribe;

import java.util.List;

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

        filterBox.setOnAction(e -> loadData());

        loadData();

        EventBus.getInstance().register(this);
    }

    @Subscribe
    public void onItemSaved(ItemSavedEvent event) {
        System.out.println("HistoryList received ItemSavedEvent! Refreshing data.");
        Platform.runLater(this::loadData);
    }

    public void loadData() {
        cardsContainer.getChildren().clear();

        String selected = filterBox.getValue().toLowerCase();
        String typeFilter = switch (selected) {
            case "fakty" -> "fact";
            case "cytaty" -> "quote";
            default -> "all";
        };

        List<FactCardData> entries = DatabaseService.loadFavoritesAsCards(typeFilter);

        for (FactCardData entry : entries) {
            cardsContainer.getChildren().add(
                    new FactCard(entry.type(), entry.content(), entry.author())
            );
        }
    }

    public record FactCardData(String type, String content, String author) {}
}