package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class FileChooserController implements Initializable {
    private Manager manager;

    public FileChooserController(){
        manager = Manager.getInstance();
    }

    @FXML
        private ChoiceBox cb;

    @FXML
        private Button openBtn;

    @FXML
        private Text actionText;

    @FXML
        private void submit() throws Exception{
        if (cb.getValue() == null){
            actionText.setFill(Color.RED);
            actionText.setText("Did not choose.");
        }
        else {
            // get selected roster and close window
            String roster_name = (String) cb.getValue();
            int roster_id = manager.queryRoster(roster_name);
            Stage stage = (Stage) openBtn.getScene().getWindow();
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Vector<String> roster_names = manager.queryAllRosters();
        ObservableList<String> rosters = FXCollections.observableArrayList(roster_names);
        cb.setItems(rosters);
    }
}
