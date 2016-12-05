package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Model;

/**
 * Superclass for other controller classes to allow polymorphism and shared
 * helper methods
 */
abstract class Controller
{
    Stage stage = null;
    public void setStage(Stage stage) { this.stage = stage; }
    
    Model model = null;
    public void setModel(Model model) { this.model = model; }
    
    /**
     * Helper method for testing if a group of strings is non-null & non-empty
     * @param fields String(s) to test
     * @return true if ANY entry is empty, false otherwise
     */
    static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if ((field == null) || field.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Creates and shows an error alert with the given header and message
     * @param header The error's header
     * @param message The error's message
     */
    void createErrorMessage(String header, String message) {
        createMessage("Error", header, message, Alert.AlertType.ERROR);
    }
    
    /**
     * Creates and shows an alert with the given title, header, message, and alertType
     * @param title The message's title
     * @param header The message's header
     * @param message The message's body text (ie message)
     * @param alertType The type of alert the message is
     */
    Alert createMessage(String title, String header, String message,
                               Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
        return alert;
    }
}
