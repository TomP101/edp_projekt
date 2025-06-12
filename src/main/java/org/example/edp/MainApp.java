package org.example.edp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.edp.event.Subscribe;
import org.example.edp.model.DatabaseService;
import org.example.edp.model.Fact;
import org.example.edp.model.Quote;
import org.example.edp.service.ApiClient;
import org.example.edp.view.FactCard;
import org.example.edp.view.HistoryList;
import org.example.edp.service.EventBus;
import org.example.edp.event.ItemSavedEvent;

public class MainApp extends Application {

    private VBox contentBox;
    private Button refreshButton;
    private Button saveButton;
    private Button showFavoritesButton;

    private Fact currentFact;
    private Quote currentQuote;

    private Label saveConfirmationLabel;
    private ProgressIndicator loadingIndicator;

    @Override
    public void start(Stage primaryStage) {
        DatabaseService.init();

        saveConfirmationLabel = new Label();
        saveConfirmationLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);

        contentBox = new VBox();
        contentBox.setSpacing(15);
        contentBox.setPadding(new Insets(15, 20, 15, 20));
        contentBox.getStyleClass().add("vbox-content-card");
        contentBox.setAlignment(Pos.CENTER);

        refreshButton = new Button("Losuj ponownie");
        refreshButton.setId("refreshButton");
        refreshButton.setOnAction(e -> loadDataAsync());

        saveButton = new Button("Zapisz do ulubionych");
        saveButton.setId("saveButton");
        saveButton.setDisable(true);
        saveButton.setOnAction(e -> {
            if (currentFact != null && currentQuote != null) {
                DatabaseService.saveFact(currentFact);
                DatabaseService.saveQuote(currentQuote);
                saveButton.setDisable(true);

                EventBus.getInstance().post(new ItemSavedEvent("fact", currentFact.getText(), currentFact.getSource()));
                EventBus.getInstance().post(new ItemSavedEvent("quote", currentQuote.getContent(), currentQuote.getAuthor()));
            }
        });

        showFavoritesButton = new Button("Pokaż ulubione");
        showFavoritesButton.setId("showFavoritesButton");
        showFavoritesButton.setOnAction(e -> showHistoryList());

        HBox bottomButtonsBox = new HBox(15);
        bottomButtonsBox.setAlignment(Pos.CENTER);
        bottomButtonsBox.getStyleClass().add("bottom-buttons-box");
        bottomButtonsBox.getChildren().addAll(saveButton, showFavoritesButton, saveConfirmationLabel);

        VBox root = new VBox(25, contentBox, refreshButton, bottomButtonsBox);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 600, 350);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("FunFact & Quote of the Day");
        primaryStage.show();

        EventBus.getInstance().register(this);

        loadDataAsync();
    }

    @Subscribe
    public void onItemSavedConfirmation(ItemSavedEvent event) {
        Platform.runLater(() -> {
            saveConfirmationLabel.setText("Zapisano!");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> saveConfirmationLabel.setText("")));
            timeline.play();
        });
    }

    private void loadDataAsync() {
        saveButton.setDisable(true);
        refreshButton.setDisable(true);
        contentBox.getChildren().setAll(loadingIndicator);

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                Fact fact = ApiClient.fetchRandomFact();
                Quote quote = ApiClient.fetchRandomQuote();

                currentFact = fact;
                currentQuote = quote;

                return fact.getText() + "\n\n" + quote.toString();
            }
        };

        task.setOnSucceeded(e -> {
            contentBox.getChildren().clear();
            contentBox.getChildren().addAll(
                    new FactCard("Fact", currentFact.getText(), currentFact.getSource()),
                    new FactCard("Quote", currentQuote.getContent(), currentQuote.getAuthor())
            );
            saveButton.setDisable(false);
            refreshButton.setDisable(false);
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                task.getException().printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Błąd połączenia");
                alert.setHeaderText("Nie udało się pobrać danych");
                alert.setContentText("Sprawdź połączenie z internetem lub spróbuj ponownie później.");
                alert.showAndWait();

                contentBox.getChildren().clear();
                contentBox.getChildren().add(new Label("Wystąpił błąd podczas ładowania danych."));
            });
            refreshButton.setDisable(false);
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void showHistoryList() {
        Stage stage = new Stage();
        HistoryList historyList = new HistoryList();
        Scene scene = new Scene(historyList, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Ulubione");
        stage.setScene(scene);

        stage.setOnHidden(e -> {
            EventBus.getInstance().unregister(historyList);
            System.out.println("HistoryList unregistered from EventBus.");
        });

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        EventBus.getInstance().unregister(this);
    }

    public static void main(String[] args) {
        launch(args);
    }
}