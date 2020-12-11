package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class NewFileController {
    private Manager manager;

    public NewFileController() {
        manager = Manager.getInstance();
    }

    @FXML
    private TextField textField;

    @FXML
    private Button createBtn;

    @FXML
    private Text actionText;

    @FXML
    private void submit() throws Exception{
        if (textField.getText().equals("")){
            actionText.setFill(Color.RED);
            actionText.setText("Did not enter name.");
        }
        else {
            // get roster name and close window
            String roster_name = textField.getText();
            int roster_id = manager.insertRoster(roster_name);
            Stage stage = (Stage) createBtn.getScene().getWindow();
            stage.close();

            // open up new roster window
            FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/roster.fxml"));
            RosterController rc = new RosterController(roster_id,roster_name);
            loader.setController(rc);
            Parent root = loader.load();
            final Stage rosterStage = new Stage();
            rosterStage.initModality(Modality.APPLICATION_MODAL);
            Scene rosterScene = new Scene(root, 800, 500);
            rosterScene.getStylesheets().add(getClass().getResource("resources/css/roster.css").toExternalForm());
            rosterStage.setTitle(roster_name);
            rosterStage.setScene(rosterScene);
            rosterStage.show();
        }
    }
}
