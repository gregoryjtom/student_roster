package sample;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SaveAsController {
    private Manager manager;
    private ObservableList<Student> listStudents;
    private Stage rosterStage;
    private int roster_id;

    public SaveAsController(ObservableList<Student> listStudents, Stage rosterStage, int roster_id) {
        manager = Manager.getInstance();
        this.listStudents = listStudents;
        this.rosterStage = rosterStage;
        this.roster_id = roster_id;
    }

    @FXML
    private TextField textField;

    @FXML
    private Button submitBtn;

    @FXML
    private Text actionText;

    @FXML
    private void submit() throws Exception{
        String roster_name = textField.getText();
        if (roster_name.equals("")){
            actionText.setFill(Color.RED);
            actionText.setText("Did not enter name.");
        }
        for (Student student: listStudents){
            manager.updateStudent(student,roster_id);
        }
        manager.updateRoster(roster_name,roster_id);
        rosterStage.setTitle(roster_name);
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }
}
