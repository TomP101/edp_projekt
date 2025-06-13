package org.example.edp.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.example.edp.event.ItemSavedEvent;
import org.example.edp.event.Subscribe;
import org.example.edp.service.ApiClient;
import org.example.edp.model.DatabaseService;
import org.example.edp.model.Fact;
import org.example.edp.model.Quote;
import org.example.edp.service.EventBus;
import org.example.edp.view.FactCard;

public class MainController {

    private final VBox contentBox;
    private final Label saveConfirmationLabel;
    private final ProgressIndicator loadingIndicator;
    private final Button saveButton;
    private final Button refreshButton;

    private Fact currentFact;
    private Quote currentQuote;

    public MainController(VBox contentBox, Label saveConfirmationLabel, ProgressIndicator loadingIndicator,
                          Button saveButton, Button refreshButton) {
        this.contentBox = contentBox;
        this.saveConfirmationLabel = saveConfirmationLabel;
        this.loadingIndicator = loadingIndicator;
        this.saveButton = saveButton;
        this.refreshButton = refreshButton;
        EventBus.getInstance().register(this);
    }

    public void initialize() {
        DatabaseService.init();
        loadDataAsync();
    }

    public void handleRefreshButton() {
        loadDataAsync();
    }

    public void handleSaveButton() {
        if (currentFact != null && currentQuote != null) {
            DatabaseService.saveFact(currentFact);
            DatabaseService.saveQuote(currentQuote);
            Platform.runLater(() -> saveButton.setDisable(true));

            EventBus.getInstance().post(new ItemSavedEvent("fact", currentFact.getText(), currentFact.getSource()));
            EventBus.getInstance().post(new ItemSavedEvent("quote", currentQuote.getContent(), currentQuote.getAuthor()));
        }
    }

    private void loadDataAsync() {
        Platform.runLater(() -> {
            saveButton.setDisable(true);
            refreshButton.setDisable(true);
            contentBox.getChildren().setAll(loadingIndicator);
        });

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                currentFact = ApiClient.fetchRandomFact();
                currentQuote = ApiClient.fetchRandomQuote();
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                contentBox.getChildren().clear();
                contentBox.getChildren().addAll(
                        new FactCard("Fact", currentFact.getText(), currentFact.getSource()),
                        new FactCard("Quote", currentQuote.getContent(), currentQuote.getAuthor())
                );
                saveButton.setDisable(false);
                refreshButton.setDisable(false);
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {

                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Błąd połączenia");
                alert.setHeaderText("Nie udało się pobrać danych");
                alert.setContentText("Sprawdź połączenie z internetem lub spróbuj ponownie później.");
                alert.showAndWait();

                contentBox.getChildren().clear();
                contentBox.getChildren().add(new Label("Wystąpił błąd podczas ładowania danych."));
            });
            Platform.runLater(() -> refreshButton.setDisable(false));
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @Subscribe
    public void onItemSavedConfirmation(ItemSavedEvent event) {
        Platform.runLater(() -> {
            saveConfirmationLabel.setText("Zapisano!");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> saveConfirmationLabel.setText("")));
            timeline.play();
        });
    }

    public void unregister() {
        EventBus.getInstance().unregister(this);
    }
}