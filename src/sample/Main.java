package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/home.fxml"));

        // set stage
        primaryStage.setTitle("Student Roster");
        Scene scene = new Scene(root, 500, 300);
        scene.getStylesheets().add(getClass().getResource("resources/css/home.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(500);
        primaryStage.setMaxWidth(500);
        primaryStage.setMinHeight(300);
        primaryStage.setMaxHeight(300);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
