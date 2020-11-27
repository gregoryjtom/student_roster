package sample;

public class Student {
    public String id_number = "";
    public String last_name = "";
    public String first_name = "";
    public String major = "";
    public String grade = "";
    public String grade_option = "";
    public String honor = "";
    public String notes = "";
    public String photo = "";

    public Student(String id, String last, String first, String major, String grade, String grade_option, String honor, String notes, String photo){
        this.id_number = id;
        this.last_name = last;
        this.first_name = first;
        this.major = major;
        this.grade = grade;
        this.grade_option = grade_option;
        this.honor = honor;
        this.notes = notes;
        this.photo = photo;
    }
    public Student(){}

}
