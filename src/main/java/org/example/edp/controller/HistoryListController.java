package org.example.edp.controller;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import org.example.edp.event.ItemSavedEvent;
import org.example.edp.event.Subscribe;
import org.example.edp.model.DatabaseService;
import org.example.edp.service.EventBus;
import org.example.edp.view.FactCard;
import org.example.edp.view.HistoryList;

import java.util.List;

public class HistoryListController {

    private final VBox cardsContainer;
    private final ComboBox<String> filterBox;

    public HistoryListController(VBox cardsContainer, ComboBox<String> filterBox) {
        this.cardsContainer = cardsContainer;
        this.filterBox = filterBox;
        EventBus.getInstance().register(this);
    }

    public void initialize() {
        loadData();
    }

    public void handleFilterChange() {
        loadData();
    }

    @Subscribe
    public void onItemSaved(ItemSavedEvent event) {
        System.out.println("HistoryListController received ItemSavedEvent: " + event);
        Platform.runLater(this::loadData);
    }

    private void loadData() {
        cardsContainer.getChildren().clear();

        String selected = filterBox.getValue().toLowerCase();
        String typeFilter = switch (selected) {
            case "fakty" -> "fact";
            case "cytaty" -> "quote";
            default -> "all";
        };

        List<HistoryList.FactCardData> entries = DatabaseService.loadFavoritesAsCards(typeFilter);

        Platform.runLater(() -> {
            for (HistoryList.FactCardData entry : entries) {
                cardsContainer.getChildren().add(
                        new FactCard(entry.type(), entry.content(), entry.author())
                );
            }
        });
    }

    public void unregister() {
        EventBus.getInstance().unregister(this);
    }
}