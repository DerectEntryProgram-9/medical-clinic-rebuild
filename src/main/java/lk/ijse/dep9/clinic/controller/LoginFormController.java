package lk.ijse.dep9.clinic.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import lk.ijse.dep9.clinic.security.SecurityContextHolder;
import lk.ijse.dep9.clinic.security.User;
import lk.ijse.dep9.clinic.security.UserRole;
import misc.CryptoUtil;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {
    public JFXTextField txtUsername;
    public JFXPasswordField txtPassword;
    public Button btnLogin;

    public void initialize() {
        btnLogin.setDefaultButton(true);
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws ClassNotFoundException, IOException {
        // Implement login logic data validations
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username.isBlank()) {
            new Alert(Alert.AlertType.ERROR,"Username cannot be empty").show();
            txtUsername.requestFocus();
            txtUsername.selectAll();
            return;
        } else if (password.isBlank()) {
            new Alert(Alert.AlertType.ERROR,"Password cannot be empty").show();
            txtPassword.requestFocus();
            txtPassword.selectAll();
            return;
        }
        if (!username.matches("[0-9A-Za-z]+")) { // "[A-z0-9]"
            new Alert(Alert.AlertType.ERROR,"Invalid login credentials").show();
            txtUsername.requestFocus();
            txtUsername.selectAll();
            return;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        // Connect to the medical_clinic database
        try(Connection connection = DriverManager.
                getConnection("jdbc:mysql://localhost:3306/medical_clinic", "root", "mysql")) {
            System.out.println(connection);

            /* Regular Statement */
//            String sql = "SELECT role FROM User WHERE username = '%s' AND password='%s'";
//            sql = String.format(sql, username, password);
//
//            Statement stm = connection.createStatement();
//            ResultSet resultSet = stm.executeQuery(sql);


            /* Prepared Statement */
//            String sql = "SELECT role FROM User WHERE username=? AND password=?"; // ? - (Positional Parameters)values can be changed
                                                                                    // parameters index from 1 (?1, ?2, ?3 ...)
            String sql = "SELECT role FROM User WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1,username);
//            stm.setString(2,password);
            ResultSet resultSet = stm.executeQuery(sql);

            if (resultSet.next()) {
                String cipherText = resultSet.getString("password");
                if (!CryptoUtil.getSha256Hex(password).equals(cipherText)) { // Verify sha values form database and user input value
                    new Alert(Alert.AlertType.ERROR,"Invalid login credentials").show();
                    txtUsername.requestFocus();
                    txtUsername.selectAll();
                    return;
                }

                String role = resultSet.getString("role");

                SecurityContextHolder.setPrinciple(new User(username, UserRole.valueOf(role)));
                Scene scene = null;
                System.out.println(role);
                switch (role) {
                    case "Admin":
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/AdminDashboardForm.fxml")));
                        break;
                    case "Doctor":
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/DoctorDashboardForm.fxml")));
                        break;
                    default:
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/ReceptionistDashboardForm.fxml")));
                }
                Stage stage = new Stage();
                stage.setTitle("Open source medical clinic");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
                btnLogin.getScene().getWindow().hide();
            }
            else {
                new Alert(Alert.AlertType.ERROR,"Invalid login credentials").show();
                txtUsername.requestFocus();
                txtUsername.selectAll();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to connect with the database, try again").show();
        }
    }
}
