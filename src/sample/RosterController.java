package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class RosterController implements Initializable {

    private final int max_students = 100;

    // Database
    private Manager manager = Manager.getInstance();
    private int roster_id;
    private String roster_name;

    // Lists for nodes
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
    private ObservableList<String> values = FXCollections.observableArrayList("Letter Grade", "Pass/No Pass");
    private ObservableList<Student> listStudents;
    private final ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
    private final ObservableList<XYChart.Data<String,Number>> barData = FXCollections.observableArrayList();

    // Current student tracker
    private AtomicInteger curr = new AtomicInteger();
    private final Student[] currSt = new Student[1];

    // bottom buttons
    @FXML
    public Button newStudentBtn;
    @FXML
    public Button deleteStudentBtn;
    @FXML
    public Button saveChangesBtn;

    // table
    @FXML
    public TableView table;

    // student attributes
    @FXML
    public Text nameText;
    @FXML
    public ImageView imageView;
    @FXML
    public TextField idTextField;
    @FXML
    public TextField lastNameTextField;
    @FXML
    public TextField firstNameTextField;
    @FXML
    public ChoiceBox majorChoiceBox;
    @FXML
    public ToggleGroup toggleGroup;
    @FXML
    public RadioButton rbA;
    @FXML
    public RadioButton rbB;
    @FXML
    public RadioButton rbC;
    @FXML
    public RadioButton rbD;
    @FXML
    public RadioButton rbF;
    @FXML
    public ChoiceBox gradeChoiceBox;
    @FXML
    public CheckBox honorCheckBox;
    @FXML
    public TextArea notesTextArea;
    @FXML
    public Button openPictureBtn;

    // action text
    @FXML
    public Text actionText;

    // stats
    @FXML
    public HBox statsHBox;

    // top menu buttons
    @FXML
    public Button newFileBtn;
    @FXML
    public Button openFileBtn;
    @FXML
    public Button saveFileBtn;
    @FXML
    public Button saveAsFileBtn;
    @FXML
    public Button closeFileBtn;
    @FXML
    public Button closeAppBtn;

    public RosterController(int roster_id, String roster_name){
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

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // choice boxes
        majorChoiceBox.setItems(major_values);
        gradeChoiceBox.setItems(values);

        // table columns
        // photo
        TableColumn<Student,String> columnPhoto = new TableColumn<>("Photo");
        columnPhoto.setCellFactory(param -> {
            //Set up the ImageView
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(50);
            imageview.setPreserveRatio(true);

            //Set up the cell
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

        // table setup
        table.setItems(listStudents);
        table.getColumns().addAll(columnPhoto,columnId,columnLast,columnFirst,columnMajor,columnGrade,columnGradeop,columnHonor,columnNotes);
        table.getSelectionModel().selectedItemProperty().addListener((obs)->{
            currSt[0] = (Student) table.getSelectionModel().getSelectedItem();
            curr.getAndSet(table.getSelectionModel().getSelectedIndex());
            fillStudentInfo();
        });

        // charts
        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Majors");
        pieChart.setLegendSide(Side.LEFT);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> barChart = new BarChart<String,Number>(xAxis,yAxis);
        barChart.setTitle("Grades");
        xAxis.setLabel("Grade");
        yAxis.setLabel("Frequency");
        XYChart.Series<String, Number> series = new XYChart.Series<>("All Students", barData);
        barChart.getData().setAll(series);

        statsHBox.getChildren().addAll(pieChart,barChart);
    }

    // Button methods

    // bottom buttons
    @FXML
    private void addNewStudent(){
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

    @FXML
    private void deleteStudent(){
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

    @FXML
    private void saveChangesFromBtn(){
        saveChanges();
    }

    // top menu buttons

    @FXML
    private void newFile() throws Exception{
        // open up separate window
        Parent newWindow = FXMLLoader.load(getClass().getResource("resources/fxml/newFile.fxml"));
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(newWindow, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("resources/css/newFileWindow.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    private void openFile() throws Exception{
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
    private void saveFile(){
        for (Student student: listStudents){
            manager.updateStudent(student,roster_id);
        }
    }

    @FXML
    private void saveAs() throws Exception{
        // open up separate window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/fxml/saveAs.fxml"));
        SaveAsController saveAsController= new SaveAsController(listStudents,(Stage) saveAsFileBtn.getScene().getWindow(), roster_id);
        loader.setController(saveAsController);
        Parent saveAsWindow = loader.load();
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        Scene dialogScene = new Scene(saveAsWindow, 300, 200);
        dialogScene.getStylesheets().add(getClass().getResource("resources/css/newFileWindow.css").toExternalForm());
        dialog.setScene(dialogScene);
        dialog.show();
    }

    @FXML
    private void closeFile(){
        Stage stage = (Stage) closeFileBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void closeApp(){
        Platform.exit();
    }

    // image selection
    @FXML
    private void selectImage(){
        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        Stage stage = (Stage) openPictureBtn.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
    }

    private void fillStudentInfo(){
        idTextField.setText(currSt[0].getId_number());
        lastNameTextField.setText(currSt[0].getLast_name());
        firstNameTextField.setText(currSt[0].getFirst_name());
        if (currSt[0].getMajor().equals("")) {
            majorChoiceBox.getSelectionModel().clearSelection();
        }
        else{
            majorChoiceBox.setValue(currSt[0].getMajor());
        }

        switch (currSt[0].getGrade()) {
            case "A" -> toggleGroup.selectToggle(rbA);
            case "B" -> toggleGroup.selectToggle(rbB);
            case "C" -> toggleGroup.selectToggle(rbC);
            case "D" -> toggleGroup.selectToggle(rbD);
            case "F" -> toggleGroup.selectToggle(rbF);
            default -> {
                Toggle tog = toggleGroup.getSelectedToggle();
                if (tog != null) tog.setSelected(false);
            }
        }

        if (currSt[0].getGradeop().equals("")) {
            gradeChoiceBox.getSelectionModel().clearSelection();
        }
        else{
            gradeChoiceBox.setValue(currSt[0].getGradeop());
        }

        honorCheckBox.setSelected(currSt[0].getHonor().equals("Y"));

        notesTextArea.setText(currSt[0].getNotes());

        nameText.setText(currSt[0].getFirst_name() + " " + currSt[0].getLast_name());

        updateCharts();

        if (!currSt[0].getPhoto().equals("")){
            imageView.setImage(new Image(currSt[0].getPhoto()));
        }
        else{
            imageView.setImage(null);
        }
    }

    private void saveChanges(){
        // check if id number field is filled and valid
        if (!isNumeric(idTextField.getText()))
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
        currSt[0].setId_number(idTextField.getText());
        currSt[0].setLast_name(lastNameTextField.getText());
        currSt[0].setFirst_name(firstNameTextField.getText());

        // major
        String major = (String) majorChoiceBox.getValue();
        if (major == null){
            currSt[0].setMajor("");
        }
        else {
            currSt[0].setMajor(major);
        }

        // grade
        RadioButton button = (RadioButton) toggleGroup.getSelectedToggle();
        if (button == null){
            currSt[0].setGrade("");
        }
        else {
            currSt[0].setGrade(button.getText());
        }

        // grade option
        String grade_option = (String) gradeChoiceBox.getValue();
        if (grade_option == null){
            currSt[0].setGradeop("");
        }
        else {
            currSt[0].setGradeop(grade_option);
        }

        // honor
        if (honorCheckBox.isSelected()){
            currSt[0].setHonor("Y");
        }
        else{
            currSt[0].setHonor("N");
        }

        // notes
        currSt[0].setNotes(notesTextArea.getText());

        // image
        if (imageView.getImage() == null) {
            currSt[0].setPhoto("");
        }
        else{
            currSt[0].setPhoto(imageView.getImage().getUrl());
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
