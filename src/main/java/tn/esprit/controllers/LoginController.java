package tn.esprit.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import tn.esprit.utils.MyDataBase;
import tn.esprit.utils.SessionManager;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private Label loginMessageLabel;
    @FXML private ImageView brandingImageView;
    @FXML private TextField emailTextField;
    @FXML private PasswordField enterPasswordField;
    @FXML private Button signupButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            File brandingFile = new File("images/Logo.png");
            Image brandingImage = new Image(brandingFile.toURI().toString());
            brandingImageView.setImage(brandingImage);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }

    @FXML
    private void loginButtonOnAction(javafx.event.ActionEvent event) {
        loginMessageLabel.setText("");
        if (emailTextField.getText().isBlank() || enterPasswordField.getText().isBlank()) {
            loginMessageLabel.setText("Please enter email and password");
            return;
        }
        validateLogin();
    }

    private void validateLogin() {
        String verifyLogin = "SELECT id, name, email, phonenumber, roles, password FROM user WHERE email = ?";

        try (Connection connectDB = MyDataBase.getInstance().getCnx();
             PreparedStatement statement = connectDB.prepareStatement(verifyLogin)) {

            statement.setString(1, emailTextField.getText().trim());

            try (ResultSet queryResult = statement.executeQuery()) {
                if (queryResult.next()) {
                    String storedPassword = queryResult.getString("password");
                    String roles = queryResult.getString("roles");

                    if (storedPassword != null && storedPassword.equals(enterPasswordField.getText())) {
                        // Create user object from database result
                        ManageUserController.User loggedInUser = new ManageUserController.User(
                                queryResult.getInt("id"),
                                queryResult.getString("name"),
                                queryResult.getString("email"),
                                queryResult.getString("phonenumber"),
                                roles
                        );

                        // Store user in session
                        SessionManager.loginUser(loggedInUser);

                        // Redirect based on role
                        if (roles.contains("ROLE_ADMIN")) {
                            loadDashboard("/AdminDashboard.fxml", "Admin Dashboard", loggedInUser);
                        } else {
                            loadDashboard("/HomePage.fxml", "Home Page", loggedInUser);
                            // After login
                            System.out.println("Logged in with role: " + SessionManager.getCurrentUser().getRole());
                        }
                    } else {
                        loginMessageLabel.setText("Invalid credentials");
                    }
                } else {
                    loginMessageLabel.setText("User not found");
                }
            }
        } catch (SQLException e) {
            loginMessageLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            loginMessageLabel.setText("System error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDashboard(String fxmlPath, String title, ManageUserController.User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Pass user data to dashboard controller if needed
            if (loader.getController() instanceof AdminDashboardController) {
                ((AdminDashboardController) loader.getController()).setCurrentUser(user);
            } else if (loader.getController() instanceof HomePageController) {
                ((HomePageController) loader.getController()).setCurrentUser(user);
            }

            Stage stage = (Stage) emailTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {
            loginMessageLabel.setText("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }



    @FXML
    private void handleSignupButton(javafx.event.ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/signup.fxml"));
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Account");
            stage.show();
        } catch (Exception e) {
            loginMessageLabel.setText("Error loading signup form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleForgotPassword(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reset_password.fxml"));
            Stage stage = (Stage) emailTextField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reset Password");
            stage.show();
        } catch (Exception e) {
            loginMessageLabel.setText("Error loading reset password form");
            e.printStackTrace();
        }
    }

    // Keep debug method but removed for brevity
    private void debugFileLocation(String filename) { /* ... */ }
}