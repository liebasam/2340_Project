package controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ControllerUtils
{
    private ControllerUtils() {}
    
    /**
     * Helper method for testing if a group of strings is non-null & non-empty
     * @param fields String(s) to test
     * @return true if ANY entry is empty, false otherwise
     */
    public static boolean isEmpty(String... fields) {
        for (String field : fields) {
            if (field == null || field.length() == 0) {
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
    public static void createErrorMessage(Stage stage, String header, String message) {
        createMessage(stage, "Error", header, message, Alert.AlertType.ERROR);
    }
    
    /**
     * Creates and shows an alert with the given title, header, message, and alertType
     * @param title The message's title
     * @param header The message's header
     * @param message The message's body text (ie message)
     * @param alertType The type of alert the message is
     */
    public static void createMessage(Stage stage, String title, String header, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Returns whether the provided string is a valid username
     * @param name The username
     * @return True if valid, false otherwise
     */
    public static boolean isValidUsername(String name) {
        final boolean[] isValid = {(name.length() <= 15)};
        name.chars().forEach(e -> isValid[0] = isValid[0] && ((e >= 48 && e <= 57) || (e >= 97 && e <= 122)));
        return isValid[0];
    }

    /**
     * Returns whether the provided string is a valid password
     * @param pass The password
     * @return True if valid, false otherwise
     */
    public static boolean isValidPassword(String pass) {
        return pass.length() >= 5;
    }
}
