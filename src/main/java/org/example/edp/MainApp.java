package org.example.edp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.example.edp.controller.MainController;
import org.example.edp.controller.HistoryListController;
import org.example.edp.view.HistoryList;

public class MainApp extends Application {

    private MainController mainController;
    private HistoryListController historyListController;

    private VBox contentBox;
    private Button refreshButton;
    private Button saveButton;
    private Button showFavoritesButton;
    private Label saveConfirmationLabel;
    private ProgressIndicator loadingIndicator;

    @Override
    public void start(Stage primaryStage) {
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

        saveButton = new Button("Zapisz do ulubionych");
        saveButton.setId("saveButton");
        saveButton.setDisable(true);

        showFavoritesButton = new Button("Pokaż ulubione");
        showFavoritesButton.setId("showFavoritesButton");

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


        mainController = new MainController(contentBox, saveConfirmationLabel, loadingIndicator, saveButton, refreshButton);

        refreshButton.setOnAction(e -> mainController.handleRefreshButton());
        saveButton.setOnAction(e -> mainController.handleSaveButton());
        showFavoritesButton.setOnAction(e -> showHistoryList());

        mainController.initialize();
    }

    private void showHistoryList() {
        Stage stage = new Stage();
        HistoryList historyList = new HistoryList();


        historyListController = new HistoryListController(historyList.getCardsContainer(), historyList.getFilterBox());
        historyListController.initialize();


        historyList.getFilterBox().setOnAction(e -> historyListController.handleFilterChange());

        Scene scene = new Scene(historyList, 700, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("Ulubione");
        stage.setScene(scene);


        stage.setOnHidden(e -> {
            if (historyListController != null) {
                historyListController.unregister();
                historyListController = null;
                System.out.println("HistoryListController unregistered from EventBus.");
            }
        });

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (mainController != null) {
            mainController.unregister();
            System.out.println("MainController unregistered from EventBus.");
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}