package ch.bosshard.matteo.javapasswordmanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    PasswordManager manager = new PasswordManager();
    @Override
    public void start(Stage mainStage) throws IOException {
        mainStage.setTitle("Password Manager");
        Label titleLabel = new Label("Java Password Manager");

        TextField masterPassword = new TextField("Set your master password");
        Button setMasterPwBtn = new Button("Submit");
        setMasterPwBtn.setOnAction(e -> setMasterPassword(masterPassword.getText()));

        TextField login = new TextField("Enter your master password");
        Button loginBtn = new Button("Submit");
        loginBtn.setOnAction(e -> checkMasterPassword(login.getText()));

        VBox root = new VBox(10);
        root.getChildren().addAll(titleLabel, masterPassword, setMasterPwBtn, login, loginBtn);

        Scene scene = new Scene(root, 1200, 700);
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void setMasterPassword(String masterPassword) {
        manager.setMasterPassword(masterPassword);
        showAlert("Success", "Master password set successfully!");
        System.out.println("Hashed Password: " + manager.getHashedPassword());
        System.out.println("Salt: " + manager.getSalt());
    }

    private void checkMasterPassword(String masterPassword) {
        if (manager.validateMasterPassword(masterPassword)) {
            showAfterLogin();
        } else {
            showAlert("Error", "Master Password Incorrect");
        }
    }

    private void showAfterLogin() {
        System.out.println("login successful");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}