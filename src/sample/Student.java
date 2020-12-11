package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Student {
    private final StringProperty idProp = new SimpleStringProperty();
    private final StringProperty lastProp = new SimpleStringProperty();
    private final StringProperty firstProp = new SimpleStringProperty();
    private final StringProperty majorProp = new SimpleStringProperty();
    private final StringProperty gradeProp = new SimpleStringProperty();
    private final StringProperty gradeopProp = new SimpleStringProperty();
    private final StringProperty honorProp = new SimpleStringProperty();
    private final StringProperty notesProp = new SimpleStringProperty();
    private final StringProperty photoProp = new SimpleStringProperty();

    public Student(String id, String last, String first, String major, String grade, String grade_option, String honor, String notes, String photo){
        idProp.set(id);
        lastProp.set(last);
        firstProp.set(first);
        majorProp.set(major);
        gradeProp.set(grade);
        gradeopProp.set(grade_option);
        honorProp.set(honor);
        notesProp.set(notes);
        photoProp.set(photo);
    }
    public Student(){
        idProp.set("");
        lastProp.set("");
        firstProp.set("");
        majorProp.set("");
        gradeProp.set("");
        gradeopProp.set("");
        honorProp.set("");
        notesProp.set("");
        photoProp.set("");
    }

    // id

    public String getId_number(){
        return idProp.get();
    }
    public void setId_number(String id){
        idProp.set(id);
    }
    public StringProperty id_numberProperty(){
        return idProp;
    }

    // last name

    public String getLast_name(){
        return lastProp.get();
    }
    public void setLast_name(String last){
        lastProp.set(last);
    }
    public StringProperty last_nameProperty(){
        return lastProp;
    }

    // first name

    public String getFirst_name(){
        return firstProp.get();
    }
    public void setFirst_name(String first){
        firstProp.set(first);
    }
    public StringProperty first_nameProperty(){
        return firstProp;
    }

    // major

    public String getMajor(){
        return majorProp.get();
    }
    public void setMajor(String major){
        majorProp.set(major);
    }
    public StringProperty majorProperty(){
        return majorProp;
    }

    // grade

    public String getGrade(){
        return gradeProp.get();
    }
    public void setGrade(String grade){
        gradeProp.set(grade);
    }
    public StringProperty gradeProperty(){
        return gradeProp;
    }

    // grade option

    public String getGradeop(){
        return gradeopProp.get();
    }
    public void setGradeop(String gradeop){
        gradeopProp.set(gradeop);
    }
    public StringProperty gradeopProperty(){
        return gradeopProp;
    }

    // honor

    public String getHonor(){
        return honorProp.get();
    }
    public void setHonor(String honor){
        honorProp.set(honor);
    }
    public StringProperty honorProperty(){
        return honorProp;
    }

    // notes

    public String getNotes(){
        return notesProp.get();
    }
    public void setNotes(String notes){
        notesProp.set(notes);
    }
    public StringProperty notesProperty(){
        return notesProp;
    }

    // photo

    public String getPhoto(){
        return photoProp.get();
    }
    public void setPhoto(String photo){
        photoProp.set(photo);
    }
    public StringProperty photoProperty(){
        return photoProp;
    }


}
