package sample;
import java.sql.*;
import java.util.Vector;

/*
need to add
- update student (note: making it mandatory to add all fields? does it even matter?)
- delete student
- new roster

- query student (based on id)
- query student id's in roster


need to create interface:
- stores student id's of current roster
- stores current roster id
- stores if new or not
 */

public class Manager {
    private Connection conn;
    private int max_students = 100;
    private int max_rosters = 20;

    public Manager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=Dskitty12;&serverTimezone=UTC");
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
    public void initializeDB(){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("USE rosterdb");
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public Vector<Student> queryStudentsInRoster(int roster_id){
        Vector<Student> students = new Vector<Student>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students " +
                    "WHERE roster_id = " + roster_id + " ORDER BY last_name");
            // if no results found:
            if (!rs.next()) {
                System.out.println("No students.");
            } else {
                students.add(new Student(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                        rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
            }
            while (rs.next()) {
                students.add(new Student(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                        rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)));
            }
            stmt.close();
            rs.close();
        }

        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return students;

    }

    public void insertStudent(Student st, int roster_id){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("INSERT INTO students " +
                    "VALUES (" + st.id_number + ", '" + st.last_name + "', '"
                    + st.first_name + "', '" + st.major + "', '" + st.grade + "', '"
                    + st.grade_option + "', '" + st.honor + "', '" + st.notes
                    + "', '" + st.photo + "', " + roster_id +  ")");

            if (success != 0) {
                System.out.println("Successfully inserted student.");
            }
            else{
                System.out.println("Did not insert student.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }

    public void updateStudent(Student st, int roster_id){
        // if id_number is not filled, then return immediately and do nothing
        if (st.id_number.equals("")){
            return;
        }
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students " +
                    "WHERE student_id = '" + st.id_number + "'");
            // if no results found, then insert:
            if (!rs.next()){
                insertStudent(st,roster_id);
            }
            // otherwise update the values
            else {
                int success = stmt.executeUpdate("UPDATE students " +
                        "SET last_name = '" + st.last_name + "', first_name = '" + st.first_name + "', major = '" +
                        st.major + "', current_grade = '" + st.grade + "', grade_option = '" + st.grade_option + "', honor = '" +
                        st.honor + "', notes = '" + st.notes + "', photo_url = '" + st.photo + "' "
                        + "WHERE student_id = '" + st.id_number + "'");
                if (success != 0) {
                    System.out.println("Successfully updated student.");
                } else {
                    System.out.println("Did not update student.");
                }
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public Student queryStudent(String id){
        Student st = null;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students " +
                    "WHERE student_id = '" + id + "'");
            // if no results found:
            if (!rs.next()){
                System.out.println("This student does not exist.");
            }
            else{
                 st = new Student(id,rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                        rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9));

            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return st;
    }

    public void deleteStudent(String id){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("DELETE FROM students " +
                    "WHERE student_id = '" + id + "'");
            if (success != 0) {
                System.out.println("Successfully deleted student.");
            }
            else{
                System.out.println("Student did not exist in database.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public Vector<String> queryAllRosters(){
        Vector<String> roster_names = new Vector<String>();
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT roster_name FROM ROSTERS");
            // if no results found:
            if (!rs.next()) {
                System.out.println("No rosters.");
            } else {
                roster_names.add(rs.getString(1));
            }
            while (rs.next()) {
                roster_names.add(rs.getString(1));
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return roster_names;
    }

    public int insertRoster(String name){
        int roster_id = 0;
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("INSERT INTO rosters " +
                    "VALUES (DEFAULT, '" + name + "')");
            if (success != 0) {
                ResultSet rs = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM rosters");
                rs.next();
                roster_id = rs.getInt(1);
                System.out.println("Successfully inserted " + name + " with roster ID #" + roster_id + ".");
                rs.close();
            }
            else{
                System.out.println("Did not insert roster.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
       return roster_id;
    }

    public int queryRoster(String name){
        int roster_id = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT roster_id " +
                    "FROM rosters " +
                    "WHERE roster_name = '" + name + "'");
            if (rs.next()) {
                roster_id = rs.getInt(1);
            }
            else{
                System.out.println("Did not find roster.");
            }
            stmt.close();
            rs.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return roster_id;
    }

    public void updateRoster(String name, int roster_id){
        try{
            Statement stmt = conn.createStatement();
            int success = stmt.executeUpdate("UPDATE rosters " +
                    "SET roster_name = '" + name + "' " +
                    "WHERE roster_id = " + roster_id);
            if (success != 0) {
                System.out.println("Successfully saved roster ID #" + roster_id + " with name: " + name + ".");
            }
            else{
                System.out.println("Did not update roster.");
            }
            stmt.close();
        }
        catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public void closeConn(){
        try{
            conn.close();
        }
        catch(SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

}
