package ch.bosshard.matteo.javapasswordmanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    PasswordManager manager = new PasswordManager();
    BorderPane root = new BorderPane();
    String masterPassword; // Store the master password after login

    @Override
    public void start(Stage mainStage) throws IOException {
        // Load data on startup
        manager.loadMasterPassword();

        mainStage.setTitle("Password Manager");
        Label titleLabel = new Label("Java Password Manager");
        root.setTop(titleLabel);

        // Check if master password is already set
        if (manager.getHashedPassword() != null && manager.getSalt() != null) {
            root.setCenter(getLoginPane());
        } else {
            root.setCenter(getMasterPasswordPane());
        }

        Scene scene = new Scene(root, 1200, 700);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private VBox getMasterPasswordPane() {
        VBox passwordPane = new VBox(10);

        Label label = new Label("Set your master password");
        TextField masterPasswordField = new TextField();
        masterPasswordField.setPromptText("Set your master password");
        Button setMasterPwBtn = new Button("Submit");
        setMasterPwBtn.setOnAction(e -> {
            if (manager.getHashedPassword() != null && manager.getSalt() != null) {
                showAlert("Error", "Master password already exists! Please log in.");
                return;
            }

            masterPassword = masterPasswordField.getText();
            if (masterPassword.isEmpty()) {
                showAlert("Error", "Master password cannot be empty!");
                return;
            }
            manager.setMasterPassword(masterPassword);
            root.setCenter(getLoginPane());
        });

        passwordPane.getChildren().addAll(label, masterPasswordField, setMasterPwBtn);
        return passwordPane;
    }

    private VBox getLoginPane() {
        VBox loginPane = new VBox(10);

        Label label = new Label("Login with your master password");
        PasswordField loginField = new PasswordField();
        loginField.setPromptText("Enter your master password");
        Button loginBtn = new Button("Login");
        loginBtn.setOnAction(e -> {
            masterPassword = loginField.getText();
            if (manager.validateMasterPassword(masterPassword)) {
                manager.loadPasswordEntries(masterPassword);
                root.setCenter(getAfterSuccessfulLoginPane());
            } else {
                showAlert("Error", "Master password is incorrect!");
            }
        });

        loginPane.getChildren().addAll(label, loginField, loginBtn);
        return loginPane;
    }

    private VBox getAfterSuccessfulLoginPane() {
        VBox mainPane = new VBox(10);

        Label label = new Label("Welcome Back!");
        Label yourPasswords = new Label("Your Passwords:");
        TableView<PasswordEntry> passwordTable = new TableView<>();

        TableColumn<PasswordEntry, String> serviceColumn = new TableColumn<>("Service");
        serviceColumn.setCellValueFactory(new PropertyValueFactory<>("service"));

        TableColumn<PasswordEntry, String> passwordColumn = new TableColumn<>("Password");
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("plaintextPassword"));

        TableColumn<PasswordEntry, String> hashColumn = new TableColumn<>("Hash");
        hashColumn.setCellValueFactory(new PropertyValueFactory<>("hashedPassword"));

        passwordTable.getColumns().addAll(serviceColumn, passwordColumn, hashColumn);
        passwordTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        passwordTable.getItems().addAll(manager.getPasswordEntries());

        TextField serviceField = new TextField();
        serviceField.setPromptText("Service");

        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");

        Button addPasswordBtn = new Button("Add Password");
        addPasswordBtn.setOnAction(e -> {
            String service = serviceField.getText();
            String password = passwordField.getText();
            if (!service.isEmpty() && !password.isEmpty()) {
                manager.addPassword(service, password);
                passwordTable.getItems().clear();
                passwordTable.getItems().addAll(manager.getPasswordEntries());
                serviceField.clear();
                passwordField.clear();
            } else {
                showAlert("Error", "Service and password cannot be empty!");
            }
        });

        HBox newPasswordHBox = new HBox(10);
        newPasswordHBox.getChildren().addAll(serviceField, passwordField, addPasswordBtn);

        mainPane.getChildren().addAll(label, yourPasswords, newPasswordHBox, passwordTable);

        return mainPane;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        // Save all data (master password and password entries) on app close
        if (masterPassword != null) {
            manager.savePasswordEntries(masterPassword);
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
