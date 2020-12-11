package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class HomeController implements Initializable {

    private Manager manager;

    public HomeController(){
        manager = Manager.getInstance();
    }

    @FXML
        private StackPane logo;

    @FXML
        private Rectangle rect;

    @FXML
        private Text text;

    @FXML
        private Text actionText;

    @FXML
    private void openRoster() throws Exception{
        Vector<String> roster_names = manager.queryAllRosters();
        if (roster_names.size() == 0){
            actionText.setFill(Color.RED);
            actionText.setText("No rosters.");
            return;
        }

        // open up separate window
        Parent openWindow = FXMLLoader.load(getClass().getResource("resources/fxml/fileChooser.fxml"));
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(openWindow, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("resources/css/openFileWindow.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    private void newRoster() throws Exception{
        // open up separate window
        Parent newWindow = FXMLLoader.load(getClass().getResource("resources/fxml/newFile.fxml"));
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(newWindow, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("resources/css/newFileWindow.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // logo lighting
        Light.Point light = new Light.Point(50, 50, 100, Color.WHITE);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        rect.setEffect(lighting);

        // drop shadow for logo text
        text.setEffect(new DropShadow(1,Color.WHITE));

        // reflection for logo
        Reflection reflection = new Reflection();
        reflection.setFraction(0.45);
        reflection.setTopOffset(-100);
        logo.setEffect(reflection);
    }
}
