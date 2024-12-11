package ch.bosshard.matteo.javapasswordmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    PasswordManager manager = new PasswordManager();
    BorderPane root = new BorderPane();

    @Override
    public void start(Stage mainStage) throws IOException {
        generateExamplePasswords();
        mainStage.setTitle("Password Manager");
        Label titleLabel = new Label("Java Password Manager");

        root.setTop(titleLabel);

        root.setCenter(getMasterPasswordPane());

        Scene scene = new Scene(root, 1200, 700);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private VBox getMasterPasswordPane() {
        VBox passwordPane = new VBox(10);

        Label label = new Label("Set your master password");

        TextField masterPassword = new TextField("Set your master password");
        Button setMasterPwBtn = new Button("Submit");
        setMasterPwBtn.setOnAction(e -> {
            String password = masterPassword.getText();
            if (password.isEmpty()) {
                showAlert("Error", "Master password cannot be empty!");
                return;
            }
            setMasterPassword(password);
            root.setCenter(getLoginPane());
        });

        passwordPane.getChildren().addAll(label, masterPassword, setMasterPwBtn);
        return passwordPane;
    }

    private void setMasterPassword(String masterPassword) {
        manager.setMasterPassword(masterPassword);
        System.out.println("Hashed Password: " + manager.getHashedPassword());
        System.out.println("Salt: " + manager.getSalt());
    }

    private VBox getLoginPane() {
        VBox loginPane = new VBox(10);
        Label label = new Label("Login with your master password");
        TextField login = new TextField("Enter your master password");
        Button loginBtn = new Button("Submit");
        loginBtn.setOnAction(e -> checkMasterPassword(login.getText()));
        loginPane.getChildren().addAll(label, login, loginBtn);
        return loginPane;
    }

    private void checkMasterPassword(String masterPassword) {
        if (manager.validateMasterPassword(masterPassword)) {
            root.setCenter(getAfterSuccessfulLoginPane());
        } else {
            showAlert("Error", "Master Password Incorrect");
        }
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

        TableColumn<PasswordEntry, String> hashedPasswordColumn = new TableColumn<>("Hashed Password");
        hashedPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("hashedPassword"));

        passwordTable.getColumns().addAll(serviceColumn, passwordColumn, hashedPasswordColumn);
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

        mainPane.getChildren().addAll(label, newPasswordHBox, passwordTable);

        return mainPane;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void generateExamplePasswords() {
        manager.addPassword("Google", "myGooglePassword.123");
        manager.addPassword("Facebook", "myFacebookPassword.456");
        manager.addPassword("Twitter", "myTwitterPassword.789");
    }

    public static void main(String[] args) {
        launch();
    }
}