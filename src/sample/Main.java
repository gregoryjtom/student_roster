package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {

    /*
    what should happen if the current student is new:
    - should make sure that id is filled (if not then give error)
    - should have increased number of students
    - should disable new student button, unless they clicked save changes (i guess it should not be disabled but it will save first just in case then go to new)

    - need to add delete functionality and shift all the inputs after to the left
    - need to add save changes and save functionality

    ALSO
    - need to make sure that updateStudent checks to see if the id already exists, if not then calls insert

    // TODO: fix photo display and update, refactor stuff
    // TODO: add top bar - add the other file buttons
    // TODO: make into interface that can open
    // TODO: make home screen
    // TODO: restyle everything
    // TODO: make the title show the name
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        // goal: title in middle-top and then two buttons: open file + new file

        Manager manager = new Manager();
        manager.initializeDB();

        BorderPane root = new BorderPane();
        StackPane center = new StackPane();
        AnchorPane bottom = new AnchorPane();

        root.setCenter(center);
        root.setBottom(bottom);


        StackPane logo = new StackPane();

        // square for the logo
        Rectangle rect = new Rectangle(100, 100);
        rect.setFill(Color.DODGERBLUE);

        Light.Point light = new Light.Point(50, 50, 100, Color.WHITE);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);
        rect.setEffect(lighting);

        // text has a drop shadow
        Text text = new Text("RT");
        text.setFont(Font.font("Futura", FontWeight.BOLD, 80));
        text.setFill(Color.WHITE);
        text.setEffect(new DropShadow(1,Color.WHITE));

        // reflection for logo
        Reflection reflection = new Reflection();
        reflection.setFraction(0.45);
        reflection.setTopOffset(-80);
        logo.setEffect(reflection);
        logo.getChildren().addAll(rect,text);
        center.getChildren().add(logo);


        Text titleText = new Text("Welcome to Roster Tracker!");
        titleText.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));


        Text actionText = new Text();

        HBox hbox = new HBox();
        hbox.setSpacing(30);

        Button btn = new Button("Open Roster");
        btn.setOnAction((e)->{
            Vector<String> roster_names = manager.queryAllRosters();
            if (roster_names.size() == 0){
                actionText.setFill(Color.RED);
                actionText.setText("No rosters.");
                return;
            }

            // open up separate window
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.setPadding(new Insets(15,10,15,10));

            ObservableList<String> rosters = FXCollections.observableArrayList(roster_names);
            ChoiceBox cb = new ChoiceBox(rosters);
            cb.setPrefWidth(200);
            Button submitBtn = new Button("Open");
            Text actionText2 = new Text();
            submitBtn.setOnAction(evt->{
                if (cb.getValue() == null){
                    actionText2.setFill(Color.RED);
                    actionText2.setText("Did not choose.");
                }
                else {
                    // open up new roster window
                    String roster_name = (String) cb.getValue();
                    int roster_id = manager.queryRoster(roster_name);
                    Roster roster = new Roster(roster_id,roster_name);
                    Stage[] stage = {primaryStage};
                    dialog.close();
                    roster.start(stage);
                }
            });
            dialogVbox.getChildren().addAll(new Text("Open a file:"),cb,submitBtn,actionText2);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        Button btn2 = new Button("New Roster");
        btn2.setOnAction(e->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.setPadding(new Insets(15,10,15,10));

            TextField tf_name = new TextField();
            tf_name.setPrefWidth(200);
            Button submitBtn = new Button("Create");
            Text actionText2 = new Text();
            submitBtn.setOnAction(evt->{
                if (tf_name.getText().equals("")){
                    actionText2.setFill(Color.RED);
                    actionText2.setText("Did not enter name.");
                }
                else {
                    // open up new roster window
                    String roster_name = tf_name.getText();
                    int roster_id = manager.insertRoster(roster_name);
                    Roster roster = new Roster(roster_id,roster_name);
                    Stage[] stage = {primaryStage};
                    dialog.close();
                    roster.start(stage);
                }
            });
            dialogVbox.getChildren().addAll(new Text("Create a new file:"),tf_name,submitBtn,actionText2);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        hbox.getChildren().addAll(btn,btn2);

        bottom.getChildren().addAll(titleText,hbox,actionText);
        bottom.setLeftAnchor(titleText, 125.);
        bottom.setBottomAnchor(titleText,80.);
        bottom.setLeftAnchor(hbox,140.);
        bottom.setBottomAnchor(hbox,40.);
        bottom.setLeftAnchor(actionText,140.);
        bottom.setBottomAnchor(actionText,20.);

        // set stage
        primaryStage.setTitle("Student Roster");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();
        primaryStage.setMinWidth(500);
        primaryStage.setMaxWidth(500);
        primaryStage.setMinHeight(300);
        primaryStage.setMaxHeight(300);
    }




    public static void main(String[] args) {
        launch(args);
        //final Manager manager = new Manager();
       // manager.initializeDB();
        //int id = manager.insertRoster("First Roster");
        //manager.deleteStudent("1");
        //Student st = new Student("1","Tom","Marty","Computer Science","A","Pass/No Pass","Y","I love Marty.","");
        //Student st = new Student("2","Costas","Bob","Communications","B","Letter Grade","N","Got a future in sports.","");
        //manager.insertStudent(st,1);
        //Student st1 = manager.queryStudent("1");
        //String[] rosters = manager.queryAllRosters();
        //System.out.println(rosters[0]);
    }
}
