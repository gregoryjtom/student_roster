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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {
    private int max_students = 100;
    private int max_rosters = 20;

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
    // TODO: add top bar
    // TODO: make into interface that can open
    // TODO: make home screen
    // TODO: restyle everything
     */

    @Override
    public void start(Stage primaryStage) throws Exception{
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        FlowPane pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);
        ScrollPane root = new ScrollPane();
        root.setContent(pane);

        Manager manager = new Manager();
        manager.initializeDB();
        int roster_id = 1;
        Vector<Student> students = manager.queryStudentsInRoster(roster_id);
        if(students.size() == 0){
            System.out.println("No students.");
            students.add(new Student());
        }

        AtomicInteger curr = new AtomicInteger();
        curr.set(0);
        final Student[] currSt = {students.get(curr.get())};

        // first 3
        String[] textArr = {"Student ID", "Last Name", "First Name"};
        String[] fillArr = {currSt[0].id_number, currSt[0].last_name, currSt[0].first_name};
        TextField[] tfArr = new TextField[3];
        int j = 0;
        for (String string: textArr) {
            TextField tf = new TextField(fillArr[j]);
            tfArr[j++] = tf;
            HBox.setHgrow(tf, Priority.ALWAYS); // to make it look better
            HBox hBox = new HBox(5, new Label(string), tf);
            hBox.setPadding(new Insets(5));
            pane.getChildren().add(hBox);
        }

        // major
        ObservableList<String> major_values = FXCollections.observableArrayList("Anthropology and Sociology",
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
        ChoiceBox cb1 = new ChoiceBox(major_values);
        if (currSt[0].major.equals("")) {
            cb1.getSelectionModel().clearSelection();
        }
        else{
            cb1.setValue(currSt[0].major);
        }
        HBox hbox_cb1 = new HBox(5, new Label("Major"), cb1);
        HBox.setHgrow(cb1,Priority.ALWAYS);
        hbox_cb1.setPadding(new Insets(5));
        pane.getChildren().add(hbox_cb1);

        // grade in class

        final ToggleGroup group = new ToggleGroup();
        RadioButton rb1 = new RadioButton("A");
        rb1.setToggleGroup(group);

        RadioButton rb2 = new RadioButton("B");
        rb2.setToggleGroup(group);

        RadioButton rb3 = new RadioButton("C");
        rb3.setToggleGroup(group);

        RadioButton rb4 = new RadioButton("D");
        rb4.setToggleGroup(group);

        RadioButton rb5 = new RadioButton("F");
        rb5.setToggleGroup(group);

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

        HBox hbox_g = new HBox(5,new Label("Current Grade"),rb1,rb2,rb3,rb4,rb5);
        hbox_g.setPadding(new Insets(5));
        pane.getChildren().add(hbox_g);

        // grade option

        ObservableList<String> values = FXCollections.observableArrayList("Letter Grade", "Pass/No Pass");
        ChoiceBox cb = new ChoiceBox(values);

        if (currSt[0].grade_option.equals("")) {
            cb.getSelectionModel().clearSelection();
        }
        else{
            cb.setValue(currSt[0].grade_option);
        }
        HBox hbox_cb = new HBox(5, new Label("Grade Option"), cb);
        HBox.setHgrow(cb,Priority.ALWAYS);
        hbox_cb.setPadding(new Insets(5));
        pane.getChildren().add(hbox_cb);

        primaryStage.setTitle("ScrollPane Demo");
        primaryStage.setScene(new Scene(root, 450, 250));
        primaryStage.show();

        // honor student
        CheckBox chb = new CheckBox();
        if (currSt[0].honor.equals("Y")) {
            chb.setSelected(true);
        }
        else{
            chb.setSelected(false);
        }
        HBox hbox_chb = new HBox(5,new Label("Honor Student?"),chb);
        hbox_chb.setPadding(new Insets(5));
        pane.getChildren().add(hbox_chb);

        //notes
        TextArea ta = new TextArea();
        ta.setText(currSt[0].notes);
        HBox.setHgrow(ta, Priority.ALWAYS); // to make it look better
        HBox hBox_ta = new HBox(5, new Label("Notes"), ta);
        hBox_ta.setPadding(new Insets(5));
        pane.getChildren().add(hBox_ta);

        //photo
        final Button openButton = new Button("Open a Picture...");
        HBox hbox_pic = new HBox(5,new Label("Picture"),openButton);
        hbox_pic.setPadding(new Insets(5));
        final FileChooser fileChooser = new FileChooser();
        ImageView iv = new ImageView();
        iv.setFitWidth(400);
        iv.setPreserveRatio(true);
        // NEED TO CHANGE: if photo already uploaded
        if (!currSt[0].photo.equals("")){
            //iv.setImage(new Image(currSt.photo));
        }
        openButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            iv.setImage(new Image(file.toURI().toString()));
                        }
                    }
                });
        pane.getChildren().addAll(hbox_pic,iv);

        // new student btn (this one and one below will make it more complicated
        Button newBtn = new Button("New Student");
        // delete student btn
        Button deleteBtn = new Button("Delete Student");
        // prev student btn
        Button prevBtn = new Button("Previous Student");
        // next student btn
        Button nextBtn = new Button("Next Student");

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

                    // NEED TO CHANGE: if photo already uploaded
                    if (!currSt[0].photo.equals("")){
                        //iv.setImage(new Image(currSt.photo));
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
            }
        });

        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Student st = students.get(curr.get());
                manager.deleteStudent(st.id_number);
                students.remove(curr.get());
                if (curr.get() < students.size()){
                    // show same index
                    currSt[0] = students.get(curr.get());

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

                    // NEED TO CHANGE: if photo already uploaded
                    if (!currSt[0].photo.equals("")){
                        //iv.setImage(new Image(currSt.photo));
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
                else if(curr.get() > 0){
                    // show previous index
                    curr.getAndDecrement();
                    currSt[0] = students.get(curr.get());

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

                    // NEED TO CHANGE: if photo already uploaded
                    if (!currSt[0].photo.equals("")){
                        //iv.setImage(new Image(currSt.photo));
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
                else{
                    // show new
                    students.add(new Student());
                    curr.set(students.size()-1);
                    currSt[0] = students.get(curr.get());

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

                    // NEED TO CHANGE: if photo already uploaded
                    if (!currSt[0].photo.equals("")){
                        //iv.setImage(new Image(currSt.photo));
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
            }
        });

        // b/c have to make sure that the prev and next buttons are aware of the changes (aka the students array))

        prevBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                curr.getAndDecrement();
                currSt[0] = students.get(curr.get());

                // should make all below into a function:
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

                // NEED TO CHANGE: if photo already uploaded
                if (!currSt[0].photo.equals("")){
                    //iv.setImage(new Image(currSt.photo));
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
        });
        //gray out and disable if no more previous
        if (curr.get() == 0){
            prevBtn.setTextFill(Color.GRAY);
            prevBtn.setDisable(true);
        }
        else{
            prevBtn.setTextFill(Color.BLACK);
            prevBtn.setDisable(false);
        }

        nextBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                curr.getAndIncrement();
                currSt[0] = students.get(curr.get());

                // should make all below into a function:
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

                // NEED TO CHANGE: if photo already uploaded
                if (!currSt[0].photo.equals("")){
                    //iv.setImage(new Image(currSt.photo));
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
        });
        //gray out and disable if no more next
        if (curr.get() >= students.size()){
            nextBtn.setTextFill(Color.GRAY);
            nextBtn.setDisable(true);
        }
        else{
            nextBtn.setTextFill(Color.BLACK);
            nextBtn.setDisable(false);
        }

        pane.getChildren().addAll(newBtn,deleteBtn,prevBtn,nextBtn);


        // action text
        Text actionText = new Text();



        // save changes button -> need a boolean isNew to see if the currSt is new
        // save changes saves to student array but save file saves to database
        Button saveChangesBtn = new Button("Save Changes");
        saveChangesBtn.setOnAction((e) -> {
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
        });
        pane.getChildren().addAll(saveChangesBtn,actionText);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e->{
            for (Student student: students){
                manager.updateStudent(student,roster_id);
            }
        });

        Button saveAsBtn = new Button("Save As");
        saveAsBtn.setOnAction(e->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
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
        pane.getChildren().addAll(saveBtn,saveAsBtn);
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
