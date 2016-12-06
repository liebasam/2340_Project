package controller;
/**
 * Controller for 'deleteUserWindow.fxml' Controller Class
 */

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import jdbc.DataAccessObject;

import java.sql.SQLDataException;
import java.sql.SQLException;

public class DeleteUserWindowController extends Controller {

    @FXML
    Button cancelButton;
    @FXML // fx:id="deleteNameUser"
    private TextField deleteNameUser; // Value injected by FXMLLoader

    @FXML
    void onCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onDeleteUser() {
        String uid = deleteNameUser.getText().replaceAll("\\s+","");
        if (uid.equals(model.getCurrentUser().getUsername())) {
            Alert selfDelErr = new Alert(Alert.AlertType.ERROR);
            selfDelErr.setHeaderText("Self-Deletion not possible.");
            selfDelErr.setContentText("You cannot delete yourself.");
            selfDelErr.showAndWait();
            deleteNameUser.setText("");
        } else {
            try {
                DataAccessObject.deleteUser(uid);
                Alert deleteSucccess = new Alert(Alert.AlertType.INFORMATION);
                deleteSucccess.setHeaderText("Deleted \'" +
                        uid +"\' successfully!");
                deleteSucccess.setContentText("I have suspended " +
                        "the account of \'" + uid + "\'");
                deleteSucccess.showAndWait();
                deleteNameUser.setText("");
            } catch (SQLException e) {
                Alert noSuchUser = new Alert(Alert.AlertType.ERROR);
                noSuchUser.setHeaderText("An SQL error occured");
                noSuchUser.setContentText("Please check the name");
                noSuchUser.showAndWait();
                deleteNameUser.setText("");

            } catch (Exception e) {
                Alert databaseErr = new Alert(Alert.AlertType.ERROR);
                databaseErr.setHeaderText("Cannot delete user \'" + uid + "\'");
                databaseErr.setContentText("Please contact" +
                        " the database provider" +
                        ", or check the internet connection");
                databaseErr.showAndWait();
                deleteNameUser.setText("");
            }
        }

    }

}
