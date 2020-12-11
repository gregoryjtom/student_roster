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
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Roster {
    private final int max_students = 100;
    private final int max_rosters = 20;
    private final TextField[] tfArr = new TextField[3];
    private final ObservableList<String> major_values = FXCollections.observableArrayList("Anthropology and Sociology",
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
    private Text nameText = new Text();
    private ImageView iv = new ImageView();
    private Button newBtn = new Button("New Student");
    private Button deleteBtn = new Button("Delete Student");
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
    private Manager manager = Manager.getInstance();
    private ObservableList<Student> listStudents;
    private final ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<String,Number>> barData = FXCollections.observableArrayList();
    private AtomicInteger curr = new AtomicInteger();
    private final Student[] currSt = new Student[1];

    public Roster(int roster_id,String roster_name){
        // all backend initialization plus first 3 textfields

        this.roster_id = roster_id;
        this.roster_name = roster_name;

        // manager and students array
        Vector<Student> students = manager.queryStudentsInRoster(roster_id);
        listStudents = FXCollections.observableArrayList(students);
        if(listStudents.size() == 0){
            listStudents.add(new Student());
        }
        curr.set(0);
        currSt[0] = listStudents.get(curr.get());

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
                if (listStudents.size()+1 > max_students){
                    System.out.println("Max students reached.");
                }
                else {
                    listStudents.add(new Student());
                    curr.set(listStudents.size()-1);
                    currSt[0] = listStudents.get(curr.get());
                    fillStudentInfo();
                }
            }
        });

        deleteBtn.setTextFill(Color.RED);
        // delete student button
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Student st = listStudents.get(curr.get());
                manager.deleteStudent(st.getId_number());
                listStudents.remove(curr.get());
                if (curr.get() < listStudents.size()){
                    // show same index
                    currSt[0] = listStudents.get(curr.get());
                }
                else if(curr.get() > 0){
                    // show previous index
                    curr.getAndDecrement();
                    currSt[0] = listStudents.get(curr.get());
                }
                else{
                    // show new
                    listStudents.add(new Student());
                    curr.set(listStudents.size()-1);
                    currSt[0] = listStudents.get(curr.get());
                }
                fillStudentInfo();
            }
        });

        saveChangesBtn.setOnAction((e) -> {
            saveChanges();
        });

        saveBtn.setOnAction(e->{
            for (Student student: listStudents){
                manager.updateStudent(student,roster_id);
            }
        });

        // TODO: add open button and saveAs button later (need stage)
    }

    public void start(){
        // all front end work
        final Stage rosterStage = new Stage();
        rosterStage.initModality(Modality.APPLICATION_MODAL);

        /**
         * ALL PANES
         */

        BorderPane root = new BorderPane();

        TabPane tabPane = new TabPane();
        Tab rosterTab = new Tab("Roster");
        Tab statsTab = new Tab("Stats");
        tabPane.getTabs().addAll(rosterTab,statsTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setCenter(tabPane);

        // two panes for roster and stats

        BorderPane rosterPane = new BorderPane();
        rosterTab.setContent(rosterPane);

        ScrollPane statsPane = new ScrollPane();
        statsTab.setContent(statsPane);

        // top file panel
        HBox topPanel = new HBox();
        topPanel.setStyle("-fx-background-color: #8aaff2;");
        topPanel.setPadding(new Insets(5, 12, 5, 12));
        topPanel.setSpacing(10);
        root.setTop(topPanel);

        // bottom panel
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
        rosterPane.setBottom(anchorPane);

        // pane for the entire center
        ScrollPane scrollPane = new ScrollPane();
        rosterPane.setCenter(scrollPane);

        BorderPane center = new BorderPane();
        center.setPadding(new Insets (10,12,10,12));
        scrollPane.setContent(center);

        // pane for the table view
        HBox tablePane = new HBox();
        tablePane.setPadding(new Insets(10,12,10,12));
        center.setTop(tablePane);

        // pane for the student view
        BorderPane bottom = new BorderPane();
        bottom.setPadding(new Insets(10,12,10,12));
        center.setCenter(bottom);

        // pane for all the student items
        FlowPane pane = new FlowPane();
        pane.setOrientation(Orientation.VERTICAL);
        bottom.setCenter(pane);

        // top pane holding picture plus student name
        HBox titlePanel = new HBox();
        titlePanel.setSpacing(30);
        titlePanel.setPrefHeight(80);

        iv.setFitHeight(70);
        iv.setPreserveRatio(true);

        StackPane namePane = new StackPane(nameText);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        titlePanel.getChildren().addAll(namePane,iv);
        bottom.setTop(titlePanel);

        /**
         * CHARTS
         */

        // after any update, go through students and update data (add to savechanges? fill student info?);
        // create another method called update piechart

        HBox hbox_chart = new HBox();
        hbox_chart.setSpacing(20);

        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Majors");
        pieChart.setLegendSide(Side.LEFT);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart =
                new BarChart<String,Number>(xAxis,yAxis);
        barChart.setTitle("Grades");
        xAxis.setLabel("Grade");
        yAxis.setLabel("Frequency");
        XYChart.Series<String, Number> series = new XYChart.Series<>("All Students", barData);
        barChart.getData().setAll(series);

        hbox_chart.getChildren().addAll(pieChart,barChart);
        statsPane.setContent(hbox_chart);

        /**
         * TABLE AT TOP
         */

        //photo
        TableColumn<Student,String> columnPhoto = new TableColumn<>("Photo");
        columnPhoto.setCellFactory(param -> {
            //Set up the ImageView
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(50);
            imageview.setPreserveRatio(true);

            //Set up the Table
            TableCell<Student, String> cell = new TableCell<Student, String>() {
                @Override
                public void updateItem(String im, boolean empty) {
                    setEditable(false);
                    if (im != null && !im.equals("")) {
                        imageview.setImage(new Image(im));
                    }
                    else{
                        imageview.setImage(null);
                    }
                }
            };

            // Attach the imageview to the cell
            cell.setGraphic(imageview);
            return cell;
        });
        columnPhoto.setCellValueFactory(new PropertyValueFactory<>("photo"));

        // first 3
        TableColumn<Student,String> columnId = new TableColumn<>("ID");
        columnId.setCellValueFactory(new PropertyValueFactory<>("id_number"));
        columnId.setCellFactory(TextFieldTableCell.forTableColumn());
        columnId.setOnEditCommit((TableColumn.CellEditEvent<Student, String> t) -> {
            if (isNumeric(t.getNewValue())){
                t.getRowValue().setId_number(t.getNewValue());
                fillStudentInfo();
            }
            else{
                t.getRowValue().setId_number(t.getOldValue());
                t.getTableView().getColumns().get(0).setVisible(false);
                t.getTableView().getColumns().get(0).setVisible(true);
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, evt -> {
                            actionText.setFill(Color.FIREBRICK);
                            actionText.setText("ID must be valid integer.");
                        }),
                        new KeyFrame(Duration.seconds(2), evt -> actionText.setText(""))
                );
                timeline.play();
            }
        });

        TableColumn<Student,String> columnLast = new TableColumn<>("Last Name");
        columnLast.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        columnLast.setCellFactory(TextFieldTableCell.forTableColumn());
        columnLast.setOnEditCommit((TableColumn.CellEditEvent<Student, String> t) -> {
            t.getRowValue().setLast_name(t.getNewValue());
            fillStudentInfo();
        });

        TableColumn<Student,String> columnFirst = new TableColumn<>("First Name");
        columnFirst.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        columnFirst.setCellFactory(TextFieldTableCell.forTableColumn());
        columnFirst.setOnEditCommit((TableColumn.CellEditEvent<Student, String> t) -> {
            t.getRowValue().setFirst_name(t.getNewValue());
            fillStudentInfo();
        });

        // major
        TableColumn<Student,String> columnMajor = new TableColumn<>("Major");
        columnMajor.setCellValueFactory(new PropertyValueFactory<>("major"));
        columnMajor.setCellFactory(param->{
            //Set up the ChoiceBox
            final ChoiceBox choiceBox = new ChoiceBox(major_values);

            //Set up the Table
            TableCell<Student, String> cell = new TableCell<Student, String>() {
                @Override
                public void updateItem(String major, boolean empty) {
                   setGraphic(choiceBox);
                   if (major != null && !major.equals("")) {
                           choiceBox.setValue(major);
                   }
                   else{
                       choiceBox.getSelectionModel().clearSelection();
                   }
                }
            };
            choiceBox.valueProperty().addListener((obs, oldvalue, newvalue) ->{
                if (cell.getTableRow() != null && cell.getTableRow().getItem() != null) {
                    if (newvalue == null) {
                        ((Student) cell.getTableRow().getItem()).setMajor("");
                    } else {
                        ((Student) cell.getTableRow().getItem()).setMajor((String) newvalue);
                    }
                    fillStudentInfo();
                }
            });
            return cell;
        });

        TableColumn<Student,String> columnGrade = new TableColumn<>("Grade");
        columnGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        columnGrade.setCellFactory(param->{
            //Set up the ChoiceBox
            final ObservableList<String> grades = FXCollections.observableArrayList("A","B","C","D","F");
            final ChoiceBox choiceBox = new ChoiceBox(grades);

            //Set up the Table
            TableCell<Student, String> cell = new TableCell<Student, String>() {
                @Override
                public void updateItem(String grade, boolean empty) {
                    setGraphic(choiceBox);
                    if (grade != null && !grade.equals("")) {
                        choiceBox.setValue(grade);
                    }
                    else{
                        choiceBox.getSelectionModel().clearSelection();
                    }
                }
            };
            choiceBox.valueProperty().addListener((obs, oldvalue, newvalue) ->{
                if (cell.getTableRow() != null && cell.getTableRow().getItem() != null) {
                    if (newvalue == null) {
                        ((Student) cell.getTableRow().getItem()).setGrade("");
                    } else {
                        ((Student) cell.getTableRow().getItem()).setGrade((String) newvalue);
                    }
                    fillStudentInfo();
                }
            });
            return cell;
        });

        TableColumn<Student,String> columnGradeop = new TableColumn<>("Grade Option");
        columnGradeop.setCellValueFactory(new PropertyValueFactory<>("gradeop"));
        columnGradeop.setCellFactory(param->{
            //Set up the ChoiceBox
            final ChoiceBox choiceBox = new ChoiceBox(values);

            //Set up the Table
            TableCell<Student, String> cell = new TableCell<Student, String>() {
                @Override
                public void updateItem(String gradeop, boolean empty) {
                    setGraphic(choiceBox);
                    if (gradeop != null && !gradeop.equals("")) {
                        choiceBox.setValue(gradeop);
                    }
                    else{
                        choiceBox.getSelectionModel().clearSelection();
                    }
                }
            };
            choiceBox.valueProperty().addListener((obs, oldvalue, newvalue) ->{
                if (cell.getTableRow() != null && cell.getTableRow().getItem() != null) {
                    if (newvalue == null) {
                        ((Student) cell.getTableRow().getItem()).setGradeop("");
                    } else {
                        ((Student) cell.getTableRow().getItem()).setGradeop((String) newvalue);
                    }
                    fillStudentInfo();
                }
            });
            return cell;
        });

        TableColumn<Student,String> columnHonor = new TableColumn<>("Honor");
        columnHonor.setCellValueFactory(new PropertyValueFactory<>("honor"));
        columnHonor.setCellFactory(param->{
            //Set up the ChoiceBox
            final CheckBox checkBox = new CheckBox();

            //Set up the Table
            TableCell<Student, String> cell = new TableCell<Student, String>() {
                @Override
                public void updateItem(String honor, boolean empty) {
                    setGraphic(checkBox);
                    if (honor != null) {
                        checkBox.setSelected(honor.equals("Y"));
                    }
                    else{
                        checkBox.setSelected(false);
                    }
                }
            };
            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) ->{
                if (cell.getTableRow() != null && cell.getTableRow().getItem() != null) {
                    if (isSelected) {
                        ((Student) cell.getTableRow().getItem()).setHonor("Y");
                    } else {
                        ((Student) cell.getTableRow().getItem()).setHonor("N");
                    }
                    fillStudentInfo();
                }
            });
            return cell;
        });


        TableColumn<Student,String> columnNotes = new TableColumn<>("Notes");
        columnNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        columnNotes.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNotes.setOnEditCommit((TableColumn.CellEditEvent<Student, String> t) -> {
            t.getRowValue().setNotes(t.getNewValue());
            fillStudentInfo();
        });

        // table styling
        TableView<Student> table = new TableView<>();
        table.setEditable(true);
        table.setMaxHeight(250);
        table.setItems(listStudents);
        table.getColumns().addAll(columnPhoto,columnId,columnLast,columnFirst,columnMajor,columnGrade,columnGradeop,columnHonor,columnNotes);
        table.getSelectionModel().selectedItemProperty().addListener((obs)->{
            currSt[0] = table.getSelectionModel().getSelectedItem();
            curr.getAndSet(table.getSelectionModel().getSelectedIndex());
            fillStudentInfo();
        });

        HBox.setHgrow(table,Priority.ALWAYS);
        tablePane.getChildren().add(table);

        /**
         * FORM AT BOTTOM
         */

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
        ta.setPrefWidth(400);
        hBox_ta.setPadding(new Insets(5));
        pane.getChildren().add(hBox_ta);

        //photo
        HBox hbox_pic = new HBox(5,new Label("Picture"),openButton);
        hbox_pic.setPadding(new Insets(5));
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
        pane.getChildren().add(hbox_pic);

        // bottom panel buttons
        leftBottomPanel.getChildren().addAll(newBtn,deleteBtn);
        rightBottomPanel.getChildren().add(saveChangesBtn);

        // action text for saves
        pane.getChildren().add(actionText);

        newFileBtn.setOnAction(e->{
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
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
                    roster.start();
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
                    roster.start();
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
                for (Student student: listStudents){
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
        rosterStage.setScene(new Scene(root, 800, 500));
        rosterStage.setMinWidth(675);
        rosterStage.show();
    }

    private void fillStudentInfo(){
        tfArr[0].setText(currSt[0].getId_number());
        tfArr[1].setText(currSt[0].getLast_name());
        tfArr[2].setText(currSt[0].getFirst_name());
        if (currSt[0].getMajor().equals("")) {
            cb1.getSelectionModel().clearSelection();
        }
        else{
            cb1.setValue(currSt[0].getMajor());
        }

        switch (currSt[0].getGrade()) {
            case "A" -> group.selectToggle(rb1);
            case "B" -> group.selectToggle(rb2);
            case "C" -> group.selectToggle(rb3);
            case "D" -> group.selectToggle(rb4);
            case "F" -> group.selectToggle(rb5);
            default -> {
                Toggle tog = group.getSelectedToggle();
                if (tog != null) tog.setSelected(false);
            }
        }

        if (currSt[0].getGradeop().equals("")) {
            cb.getSelectionModel().clearSelection();
        }
        else{
            cb.setValue(currSt[0].getGradeop());
        }

        chb.setSelected(currSt[0].getHonor().equals("Y"));

        ta.setText(currSt[0].getNotes());

        nameText.setText(currSt[0].getFirst_name() + " " + currSt[0].getLast_name());

        updateCharts();

        if (!currSt[0].getPhoto().equals("")){
            iv.setImage(new Image(currSt[0].getPhoto()));
        }
        else{
            iv.setImage(null);
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
                    new KeyFrame(Duration.seconds(2), evt -> actionText.setText(""))
            );
            timeline1.play();
            return;
        }

        // first 3
        currSt[0].setId_number(tfArr[0].getText());
        currSt[0].setLast_name(tfArr[1].getText());
        currSt[0].setFirst_name(tfArr[2].getText());

        // major
        String major = (String) cb1.getValue();
        if (major == null){
            currSt[0].setMajor("");
        }
        else {
            currSt[0].setMajor(major);
        }

        // grade
        RadioButton button = (RadioButton) group.getSelectedToggle();
        if (button == null){
            currSt[0].setGrade("");
        }
        else {
            currSt[0].setGrade(button.getText());
        }

        // grade option
        String grade_option = (String) cb.getValue();
        if (grade_option == null){
            currSt[0].setGradeop("");
        }
        else {
            currSt[0].setGradeop(grade_option);
        }

        // honor
        if (chb.isSelected()){
            currSt[0].setHonor("Y");
        }
        else{
            currSt[0].setHonor("N");
        }

        // notes
        currSt[0].setNotes(ta.getText());

        // image
        if (iv.getImage() == null) {
            currSt[0].setPhoto("");
        }
        else{
            currSt[0].setPhoto(iv.getImage().getUrl());
        }

        nameText.setText(currSt[0].getFirst_name()+ " " + currSt[0].getLast_name());

        updateCharts();

        // notify of changes saved
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, evt -> {
                    actionText.setFill(Color.FIREBRICK);
                    actionText.setText("Changes saved.");
                }),
                new KeyFrame(Duration.seconds(2), evt -> actionText.setText(""))
        );
        timeline.play();
    }

    private void updateCharts(){
        Map<String, Integer> majorCounts = new HashMap<>();
        Map<String, Integer> gradeCounts = new HashMap<>();

        for (Student student: listStudents){
            String major = student.getMajor();
            String grade = student.getGrade();

            if (!major.equals("")) {
                Integer count = majorCounts.get(major);
                if (count == null) {
                    count = 0;
                }
                majorCounts.put(major, count + 1);
            }
            if (!grade.equals("")){
                Integer count = gradeCounts.get(grade);
                if (count == null){
                    count = 0;
                }
                gradeCounts.put(grade,count+1);
            }
        }
        pieData.clear();
        barData.clear();
        majorCounts.forEach((k,v)->{
            naiveAddPieData(k,v);
        });
        gradeCounts.forEach((k,v)->{
            naiveAddBarData(k,v);
        });

    }

    public void naiveAddPieData(String name, int value)
    {
        pieData.add(new PieChart.Data(name, value));
    }

    public void naiveAddBarData(String grade, int value)
    {
        barData.add(new XYChart.Data<String,Number>(grade,value));
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
