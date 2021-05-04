package com.marshmellow.repository;

import com.marshmellow.Exception.StudentNotFound;
import com.marshmellow.model.Grade;
import com.marshmellow.model.Offering;
import com.marshmellow.model.Student;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Queue;

public class StudentRepository {
    private static final String TABLE_NAME = "Student";
    private static StudentRepository instance;

    private static final String IN_PROG_ST = "INSERT INTO InProgressCourses(sid, code, classCode) "
            + " VALUES(?,?,?)"
            + " ON DUPLICATE KEY UPDATE sid = sid";
    private static final String SUBMIT_ST = "INSERT INTO SubmittedCourses(sid, code, classCode) "
            + " VALUES(?,?,?)"
            + " ON DUPLICATE KEY UPDATE sid = sid";
    private static final String IN_PROG_QUEUE_ST = "INSERT INTO InProgressQueue(sid, code, classCode) "
            + " VALUES(?,?,?)"
            + " ON DUPLICATE KEY UPDATE sid = sid";
    private static final String WAIT_QUEUE_ST = "INSERT INTO WaitQueue(sid, code, classCode) "
            + " VALUES(?,?,?)"
            + " ON DUPLICATE KEY UPDATE sid = sid";
    public static final String DEL_IN_PROG = "DELETE FROM InProgressCourses WHERE sid = ? AND code = ? AND classCode = ?";
    public static final String DEL_SUBMIT = "DELETE FROM SubmittedCourses WHERE sid = ? AND code = ? AND classCode = ?";
    public static final String DEL_IN_PROG_QUEUE = "DELETE FROM InProgressQueue WHERE sid = ? AND code = ? AND classCode = ?";
    public static final String DEL_QUEUE = "DELETE FROM WaitQueue WHERE sid = ? AND code = ? AND classCode = ?";
    public static final String GET_IN_PROG = "SELECT C.code, C.classCode FROM InProgressCourses C WHERE C.sid = ?;";
    public static final String GET_SUBMIT = "SELECT C.code, C.classCode FROM SubmittedCourses C WHERE C.sid = ?;";
    public static final String GET_IN_PROG_QUEUE = "SELECT C.code, C.classCode FROM InProgressQueue C WHERE C.sid = ?;";
    public static final String GET_QUEUE  = "SELECT C.code, C.classCode FROM WaitQueue C WHERE C.sid = ?;";

    public static StudentRepository getInstance() {
        if (instance == null) {
            try {
                instance = new StudentRepository();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("error in StudentRepository.create query.");
            }
        }
        return instance;
    }

    private StudentRepository() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Student(id CHAR(50), name CHAR(225), secondName CHAR(225), " +
                   "email CHAR(225), password CHAR(225), birthDate CHAR(15), field CHAR(225), faculty CHAR(225), " +
                   "level CHAR(225), status CHAR(225), img CHAR(255), PRIMARY KEY(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Grade(sid CHAR(50), code CHAR(50), term INT, grade INT, " +
                    "PRIMARY KEY(sid, code, term)," +
                    "FOREIGN KEY (sid) REFERENCES Student(id)," +
                    "FOREIGN KEY (code) REFERENCES Course(code));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS SubmittedCourses(sid CHAR(50), code CHAR(50), classCode CHAR(10), " +
                   "PRIMARY KEY(sid, code, classCode)," +
                   "FOREIGN KEY (sid) REFERENCES Student(id)," +
                   "FOREIGN KEY (code, classCode) REFERENCES Course(code, classCode));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS InProgressCourses(sid CHAR(50), code CHAR(50), classCode CHAR(10), " +
                   "PRIMARY KEY(sid, code, classCode)," +
                   "FOREIGN KEY (sid) REFERENCES Student(id)," +
                   "FOREIGN KEY (code, classCode) REFERENCES Course(code, classCode));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS inProgressQueue(sid CHAR(50), code CHAR(50), classCode CHAR(10), " +
                        "PRIMARY KEY(sid, code, classCode)," +
                        "FOREIGN KEY (sid) REFERENCES Student(id)," +
                        "FOREIGN KEY (code, classCode) REFERENCES Course(code, classCode));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS WaitQueue(sid CHAR(50), code CHAR(50), classCode CHAR(10), " +
                        "PRIMARY KEY(sid, code, classCode)," +
                        "FOREIGN KEY (sid) REFERENCES Student(id)," +
                        "FOREIGN KEY (code, classCode) REFERENCES Course(code, classCode));"
        );

        createTableStatement.executeBatch();
        createTableStatement.close();
        con.close();
    }

    protected String getFindByIdStatement() {
        return String.format("SELECT* FROM %s S WHERE S.id = ?;", TABLE_NAME);
    }

    protected void fillFindByIdValues(PreparedStatement st, String id) throws SQLException {
        st.setString(1, id);
    }

    protected String getInsertStatement() {
        return String.format(
                "INSERT INTO %s(id, name, secondName, email, password, birthDate, field, faculty, level, status, img)"
                + " VALUES(?,?,?,?,?,?,?,?,?,?,?)"
                + "ON DUPLICATE KEY UPDATE id = id",
                TABLE_NAME);
    }

    protected void fillInsertValues(PreparedStatement st, Student data) throws SQLException {
        st.setString(1, data.getStudentId());
        st.setString(2, data.getName());
        st.setString(3, data.getSecondName());
        st.setString(4, data.getEmail());
        st.setString(5, data.getPassword());
        st.setString(6, data.getBirthDate());
        st.setString(7, data.getField());
        st.setString(8, data.getFaculty());
        st.setString(9, data.getLevel());
        st.setString(10, data.getStatus());
        st.setString(11, data.getImg());
    }

    protected String getFindAllStatement() {
        return String.format("SELECT * FROM %s;", TABLE_NAME);
    }

    protected Student convertResultSetToDomainModel(ResultSet rs) throws Exception {
        Student student = new Student();
        student.setStudentId(rs.getString(1));
        student.setName(rs.getString(2));
        student.setSecondName(rs.getString(3));
        student.setEmail(rs.getString(4));
        student.setPassword(rs.getString(5));
        student.setBirthDate(rs.getString(6));
        student.setField(rs.getString(7));
        student.setFaculty(rs.getString(8));
        student.setLevel(rs.getString(9));
        student.setStatus(rs.getString(10));
        student.setImg(rs.getString(11));
        student.setGrades(getGrades(student.getStudentId()));
        return student;
    }

    protected ArrayList<Student> convertResultSetToDomainModelList(ResultSet rs) throws Exception {
        ArrayList<Student> students = new ArrayList<>();
        while (rs.next()) {
            students.add(this.convertResultSetToDomainModel(rs));
        }
        return students;
    }

    public void insertGrades(Student student) throws Exception {
        Connection con = ConnectionPool.getConnection();
        con.setAutoCommit(false);
        PreparedStatement st = con.prepareStatement(
                "INSERT INTO Grade(sid, code, term, grade)"
                        + " VALUES(?,?,?,?)"
                        + " ON DUPLICATE KEY UPDATE sid = sid"
        );
        for (Grade grade : student.getGrades()) {
            st.setString(1, student.getStudentId());
            st.setString(2, grade.code);
            st.setInt(3, grade.term);
            st.setInt(4, grade.grade);
            st.addBatch();
        }
        try {
            if (student.getGrades().size() > 0) {
                st.executeBatch();
                con.commit();
            }
        } catch (Exception e) {
            con.rollback();
            System.out.println("error in Repository.insert query.");
            e.printStackTrace();
        } finally {
            DbUtils.close(st);
            DbUtils.close(con);
        }
    }

    public ArrayList<Grade> getGrades(String sid) throws Exception {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement("select G.code, G.term, G.grade FROM Grade G WHERE G.sid = ?");
        PreparedStatement st2 = con.prepareStatement("select C.name, C.units FROM Course C WHERE C.code = ?");
        st.setString(1, sid);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet == null) {
                return new ArrayList<>();
            }
            ArrayList<Grade> result = new ArrayList<>();
            while (resultSet.next()) {
                st2.setString(1, resultSet.getString(1));
                ResultSet c = st2.executeQuery();
                if (c.next()) {
                    result.add(new Grade(
                            resultSet.getString(1),
                            c.getString(1),
                            resultSet.getInt(3),
                            resultSet.getInt(2),
                            c.getInt(2)
                    ));
                }
            }
            return result;
        } catch (Exception e) {
            System.out.println("Exception in get getPrerequisites");
            throw e;
        } finally {
            DbUtils.close(st);
            DbUtils.close(st2);
            DbUtils.close(con);
        }
    }

    private void runCourseQuery(String statement, String sid, String code, String classCode) throws Exception {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(statement);
        st.setString(1, sid);
        st.setString(2, code);
        st.setString(3, classCode);

        try {
            st.execute();
        } catch (Exception e) {
            System.out.println("error in addCourse.insert query.");
            e.printStackTrace();
        } finally {
            DbUtils.close(st);
            DbUtils.close(con);
        }
    }

    private ArrayList<String[]> getCourseListQuery(String statement, String sid) throws Exception {
        ArrayList<String[]> courses = new ArrayList<>();
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(statement);
        st.setString(1, sid);
        try {
            ResultSet rs = st.executeQuery();
            while (rs.next())
                courses.add(new String[] {rs.getString(1), rs.getString(2)});
            return courses;
        } catch (Exception e) {
            System.out.println("error in addCourse.insert query.");
            e.printStackTrace();
            throw e;
        } finally {
            DbUtils.close(st);
            DbUtils.close(con);
        }
    }

    public void addCourseToInProgCourses(String sid, String code, String classCode) throws Exception {
        runCourseQuery(IN_PROG_ST, sid, code, classCode);
    }

    public void addCourseToInProgQueue(String sid, String code, String classCode) throws Exception {
        runCourseQuery(IN_PROG_QUEUE_ST, sid, code, classCode);
    }

    public void addCourseToSubmittedCourses(String sid, String code, String classCode) throws Exception {
        runCourseQuery(SUBMIT_ST, sid, code, classCode);
    }

    public void addCourseToQueue(String sid, String code, String classCode) throws Exception {
        runCourseQuery(WAIT_QUEUE_ST, sid, code, classCode);
    }

    public void removeCourseFromInProg(String sid, String code, String classCode) throws Exception {
        runCourseQuery(DEL_IN_PROG, sid, code, classCode);
    }

    public void removeCourseFromSubmit(String sid, String code, String classCode) throws Exception {
        runCourseQuery(DEL_SUBMIT, sid, code, classCode);
    }

    public void removeCourseFromInProgQueue(String sid, String code, String classCode) throws Exception {
        runCourseQuery(DEL_IN_PROG_QUEUE, sid, code, classCode);
    }

    public void removeCourseFromQueue(String sid, String code, String classCode) throws Exception {
        runCourseQuery(DEL_QUEUE, sid, code, classCode);
    }

    public ArrayList<String[]> getInProgCourses(String sid) throws Exception {
        return getCourseListQuery(GET_IN_PROG, sid);
    }

    public ArrayList<String[]> getSubmittedCourses(String sid) throws Exception {
        return getCourseListQuery(GET_SUBMIT, sid);
    }

    public ArrayList<String[]> getInProgQueueCourses(String sid) throws Exception {
        return getCourseListQuery(GET_IN_PROG_QUEUE, sid);
    }

    public ArrayList<String[]> getQueueCourses(String sid) throws Exception {
        return getCourseListQuery(GET_QUEUE, sid);
    }

    public int getInProgUnitCount(String sid) throws Exception {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(
                "SELECT SUM(C.units) FROM InProgressCourses P, Course C " +
                "WHERE P.sid = ? AND C.code = P.code AND C.classCode = P.classCode");
        st.setString(1, sid);
        try {
            ResultSet resultSet = st.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            System.out.println("error in unit count.find query.");
            e.printStackTrace();
            throw e;
        } finally {
            DbUtils.close(st);
            DbUtils.close(con);
        }
    }

    public Student findById(String id) throws Exception {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getFindByIdStatement());
        st.setString(1, id);
        try {
            ResultSet resultSet = st.executeQuery();
            if (!resultSet.next()) {
                throw new StudentNotFound();
            }
            return convertResultSetToDomainModel(resultSet);
        } catch (Exception e) {
            System.out.println("error in CourseRepository.find query.");
            e.printStackTrace();
            throw e;
        } finally {
            DbUtils.close(st);
            DbUtils.close(con);
        }
    }

    public void insert(Student student) throws SQLException {
        Connection con = ConnectionPool.getConnection();
        PreparedStatement st = con.prepareStatement(getInsertStatement());
        fillInsertValues(st, student);
        try {
            st.execute();
            st.close();
            con.close();
        } catch (Exception e) {
            st.close();
            con.close();
            System.out.println("error in Repository.insert query.");
            e.printStackTrace();
        }
    }
}

