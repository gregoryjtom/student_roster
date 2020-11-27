package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Roster {
    private int max_students = 100;
    private int max_rosters = 20;
    private TextField[] tfArr = new TextField[3];
    private ObservableList<String> major_values = FXCollections.observableArrayList("Anthropology and Sociology",
            "Art",
            "Biology",
            "Business and Management",
            "Communications",
            "Computer Science",
            "Criminal Justice",
            "Economics",
            "Education",
            "Engineering",
            "English",
            "Environmental Science",
            "Finance and Accounting",
            "Health Professions",
            "History",
            "Kinesiology and Physical Therapy",
            "Math",
            "Nursing",
            "Political Science",
            "Psychology");
    private ChoiceBox cb1 = new ChoiceBox(major_values);
    private final ToggleGroup group = new ToggleGroup();
    private RadioButton rb1 = new RadioButton("A");
    private RadioButton rb2 = new RadioButton("B");
    private RadioButton rb3 = new RadioButton("C");
    private RadioButton rb4 = new RadioButton("D");
    private RadioButton rb5 = new RadioButton("F");
    private ObservableList<String> values = FXCollections.observableArrayList("Letter Grade", "Pass/No Pass");
    private ChoiceBox cb = new ChoiceBox(values);
    private CheckBox chb = new CheckBox();
    private TextArea ta = new TextArea();
    private final Button openButton = new Button("Open a Picture...");
    private final FileChooser fileChooser = new FileChooser();
    private ImageView iv = new ImageView();
    private Button newBtn = new Button("New Student");
    private Button deleteBtn = new Button("Delete Student");
    private Button prevBtn = new Button("Previous Student");
    private Button nextBtn = new Button("Next Student");
    private Text actionText = new Text();
    private Button saveChangesBtn = new Button("Save Changes");
    private Button newFileBtn = new Button("New");
    private Button openFileBtn = new Button("Open");
    private Button closeFileBtn = new Button("Close");
    private Button exitAppBtn = new Button("Exit");
    private Button saveBtn = new Button("Save");
    private Button saveAsBtn = new Button("Save As");
    private int roster_id;
    private String roster_name;
    private Manager manager = new Manager();
    private Vector<Student> students;
    private AtomicInteger curr = new AtomicInteger();
    private final Student[] currSt = new Student[1];

    public Roster(int roster_id,String roster_name){
        // all backend initialization plus first 3 textfields

        this.roster_id = roster_id;
        this.roster_name = roster_name;

        // manager and students array
        manager.initializeDB();
        students = manager.queryStudentsInRoster(roster_id);
        if(students.size() == 0){
            System.out.println("No students.");
            students.add(new Student());
        }
        curr.set(0);
        currSt[0] = students.get(curr.get());

        // first 3 textfields
        for (int i = 0; i < 3; i++){
            TextField tf = new TextField();
            tfArr[i] = tf;
        }

        // grade
        rb1.setToggleGroup(group);
        rb2.setToggleGroup(group);
        rb3.setToggleGroup(group);
        rb4.setToggleGroup(group);
        rb5.setToggleGroup(group);

        newBtn.setTextFill(Color.GREEN);

        // new student button
        newBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (students.size()+1 > max_students){
                    System.out.println("Max students reached.");
                }
                else {
                    students.add(new Student());
                    curr.set(students.size()-1);
                    currSt[0] = students.get(curr.get());
                    fillStudentInfo();
                }
            }
        });

        deleteBtn.setTextFill(Color.RED);
        // delete student button
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Student st = students.get(curr.get());
                manager.deleteStudent(st.id_number);
                students.remove(curr.get());
                if (curr.get() < students.size()){
                    // show same index
                    currSt[0] = students.get(curr.get());
                }
                else if(curr.get() > 0){
                    // show previous index
                    curr.getAndDecrement();
                    currSt[0] = students.get(curr.get());

                }
                else{
                    // show new
                    students.add(new Student());
                    curr.set(students.size()-1);
                    currSt[0] = students.get(curr.get());
                }
                fillStudentInfo();
            }
        });

        // previous student button
        prevBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                curr.getAndDecrement();
                currSt[0] = students.get(curr.get());
                fillStudentInfo();
            }
        });

        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                curr.getAndIncrement();
                currSt[0] = students.get(curr.get());
                fillStudentInfo();
            }
        });

        saveChangesBtn.setOnAction((e) -> {
            saveChanges();
        });

        saveBtn.setOnAction(e->{
            for (Student student: students){
                manager.updateStudent(student,roster_id);
            }
        });

        // TODO: add open button and saveAs button later (need stage)
    }

    public void start(Stage[] primaryStage){
        // all front end work
        final Stage rosterStage = new Stage();
        rosterStage.initModality(Modality.APPLICATION_MODAL);
        rosterStage.initOwner(primaryStage[0]);

        FlowPane pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);

        BorderPane root = new BorderPane();
        root.setCenter(pane);

        HBox topPanel = new HBox();
        topPanel.setStyle("-fx-background-color: #8aaff2;");
        topPanel.setPadding(new Insets(5, 12, 5, 12));
        topPanel.setSpacing(10);
        root.setTop(topPanel);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: #d1c9c9;");

        HBox leftBottomPanel = new HBox();
        leftBottomPanel.setPadding(new Insets(5, 12, 5, 12));
        leftBottomPanel.setSpacing(10);
        anchorPane.setLeftAnchor(leftBottomPanel,5.);

        HBox rightBottomPanel = new HBox();
        rightBottomPanel.setPadding(new Insets(5, 12, 5, 12));
        rightBottomPanel.setSpacing(10);
        anchorPane.setRightAnchor(rightBottomPanel,5.);

        anchorPane.getChildren().addAll(leftBottomPanel,rightBottomPanel);

        root.setBottom(anchorPane);

        // first 3
        String[] textArr = {"Student ID", "Last Name", "First Name"};
        int j = 0;
        for (String string: textArr) {
            HBox.setHgrow(tfArr[j], Priority.ALWAYS); // to make it look better
            HBox hBox = new HBox(5, new Label(string), tfArr[j++]);
            hBox.setPadding(new Insets(5));
            pane.getChildren().add(hBox);
        }

        // major
        HBox hbox_cb1 = new HBox(5, new Label("Major"), cb1);
        HBox.setHgrow(cb1,Priority.ALWAYS);
        hbox_cb1.setPadding(new Insets(5));
        pane.getChildren().add(hbox_cb1);

        // grade in class
        HBox hbox_g = new HBox(5,new Label("Current Grade"),rb1,rb2,rb3,rb4,rb5);
        hbox_g.setPadding(new Insets(5));
        pane.getChildren().add(hbox_g);

        // grade option
        HBox hbox_cb = new HBox(5, new Label("Grade Option"), cb);
        HBox.setHgrow(cb,Priority.ALWAYS);
        hbox_cb.setPadding(new Insets(5));
        pane.getChildren().add(hbox_cb);

        // honor
        HBox hbox_chb = new HBox(5,new Label("Honor Student?"),chb);
        hbox_chb.setPadding(new Insets(5));
        pane.getChildren().add(hbox_chb);

        //notes
        HBox.setHgrow(ta, Priority.ALWAYS); // to make it look better
        HBox hBox_ta = new HBox(5, new Label("Notes"), ta);
        ta.setPrefWidth(200);
        hBox_ta.setPadding(new Insets(5));
        pane.getChildren().add(hBox_ta);

        //photo
        HBox hbox_pic = new HBox(5,new Label("Picture"),openButton);
        hbox_pic.setPadding(new Insets(5));
        iv.setFitWidth(200);
        iv.setPreserveRatio(true);

        //image open button
        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(rosterStage);
                        if (file != null) {
                            iv.setImage(new Image(file.toURI().toString()));
                        }
                    }
                });

        pane.getChildren().addAll(hbox_pic,iv);


        leftBottomPanel.getChildren().addAll(prevBtn,nextBtn,newBtn,deleteBtn);
        rightBottomPanel.getChildren().add(saveChangesBtn);

        pane.getChildren().add(actionText);

        newFileBtn.setOnAction(e->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage[0]);
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
                    dialog.close();
                    roster.start(primaryStage);
                }
            });
            dialogVbox.getChildren().addAll(new Text("Create a new file:"),tf_name,submitBtn,actionText2);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        openFileBtn.setOnAction(e->{
            Vector<String> roster_names = manager.queryAllRosters();

            // open up separate window
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage[0]);
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
                    dialog.close();
                    roster.start(primaryStage);
                }
            });
            dialogVbox.getChildren().addAll(new Text("Open a file:"),cb,submitBtn,actionText2);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        saveAsBtn.setOnAction(e->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(rosterStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.setPadding(new Insets(15,10,15,10));
            TextField tf_name = new TextField();
            Button submitBtn = new Button("Save");
            submitBtn.setOnAction(evt->{
                for (Student student: students){
                    manager.updateStudent(student,roster_id);
                }
                manager.updateRoster(tf_name.getText(),roster_id);
                dialog.close();
            });
            dialogVbox.getChildren().addAll(new Text("Name of file:"),tf_name,submitBtn);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        closeFileBtn.setOnAction(e->{
            rosterStage.close();
        });

        exitAppBtn.setOnAction(e->{
            Platform.exit();
        });

        Text topText = new Text("File Menu:");
        topPanel.getChildren().addAll(topText,newFileBtn,openFileBtn,saveBtn,saveAsBtn,closeFileBtn,exitAppBtn);

        fillStudentInfo();

        // set stage
        rosterStage.setTitle(roster_name);
        rosterStage.setScene(new Scene(root, 700, 400));
        rosterStage.show();
    }

    private void fillStudentInfo(){
        tfArr[0].setText(currSt[0].id_number);
        tfArr[1].setText(currSt[0].last_name);
        tfArr[2].setText(currSt[0].first_name);
        if (currSt[0].major.equals("")) {
            cb1.getSelectionModel().clearSelection();
        }
        else{
            cb1.setValue(currSt[0].major);
        }

        if (currSt[0].grade.equals("A")){
            group.selectToggle(rb1);
        }
        else if (currSt[0].grade.equals("B")){
            group.selectToggle(rb2);
        }
        else if (currSt[0].grade.equals("C")){
            group.selectToggle(rb3);
        }
        else if (currSt[0].grade.equals("D")){
            group.selectToggle(rb4);
        }
        else if (currSt[0].grade.equals("F")){
            group.selectToggle(rb5);
        }
        else{
            Toggle tog = group.getSelectedToggle();
            if (tog != null) tog.setSelected(false);
        }

        if (currSt[0].grade_option.equals("")) {
            cb.getSelectionModel().clearSelection();
        }
        else{
            cb.setValue(currSt[0].grade_option);
        }

        if (currSt[0].honor.equals("Y")) {
            chb.setSelected(true);
        }
        else{
            chb.setSelected(false);
        }

        ta.setText(currSt[0].notes);

        if (!currSt[0].photo.equals("")){
            iv.setImage(new Image(currSt[0].photo));
        }
        else{
            iv.setImage(null);
        }

        if (curr.get() == 0){
            prevBtn.setTextFill(Color.GRAY);
            prevBtn.setDisable(true);
        }
        else{
            prevBtn.setTextFill(Color.BLACK);
            prevBtn.setDisable(false);
        }
        if (curr.get() >= students.size()-1){
            nextBtn.setTextFill(Color.GRAY);
            nextBtn.setDisable(true);
        }
        else{
            nextBtn.setTextFill(Color.BLACK);
            nextBtn.setDisable(false);
        }
    }
    private void saveChanges(){
        // check if id number field is filled and valid
        if (!isNumeric(tfArr[0].getText()))
        {
            Timeline timeline1 = new Timeline(
                    new KeyFrame(Duration.ZERO, evt -> {
                        actionText.setFill(Color.FIREBRICK);
                        actionText.setText("ID must be valid integer.");
                    }),
                    new KeyFrame(Duration.seconds(2), evt -> {
                        actionText.setText("");
                    })
            );
            timeline1.play();
            return;
        }

        // first 3
        currSt[0].id_number = tfArr[0].getText();
        currSt[0].last_name = tfArr[1].getText();
        currSt[0].first_name = tfArr[2].getText();

        // major
        String major = (String) cb1.getValue();
        if (major == null){
            currSt[0].major = "";
        }
        else {
            currSt[0].major = major;
        }

        // grade
        RadioButton button = (RadioButton) group.getSelectedToggle();
        if (button == null){
            currSt[0].grade = "";
        }
        else {
            currSt[0].grade = button.getText();
        }

        // grade option
        currSt[0].grade_option = (String) cb.getValue();
        String grade_option = (String) cb.getValue();
        if (grade_option == null){
            currSt[0].grade_option = "";
        }
        else {
            currSt[0].grade_option = grade_option;
        }

        // honor
        if (chb.isSelected()){
            currSt[0].honor = "Y";
        }
        else{
            currSt[0].honor = "N";
        }

        // notes
        currSt[0].notes = ta.getText();

        // image
        if (iv.getImage() == null) {
            currSt[0].photo = "";
        }
        else{
            currSt[0].photo = iv.getImage().getUrl();
        }

        // notify of changes saved
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, evt -> {
                    actionText.setFill(Color.FIREBRICK);
                    actionText.setText("Changes saved.");
                }),
                new KeyFrame(Duration.seconds(2), evt -> {
                    actionText.setText("");
                })
        );
        timeline.play();
    }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("View Pictures");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null || strNum.equals("")) {
            return false;
        }
        try {
            int d = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
