package com.lakshya.sms.dao;

import com.lakshya.sms.model.Student;
import com.lakshya.sms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JDBC implementation. Every query that uses user input is a PreparedStatement
 * with "?" placeholders, so the input can never change the SQL (prevents SQL injection).
 */
public class StudentDAOImpl implements StudentDAO {

    // JOIN students with courses so we can show the course NAME, not just its id
    private static final String BASE_SELECT =
            "SELECT s.student_id, s.name, s.email, s.age, s.course_id, c.course_name "
          + "FROM students s JOIN courses c ON s.course_id = c.course_id ";

    @Override
    public int add(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, email, age, course_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCourseId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    @Override
    public Optional<Student> findById(int id) throws SQLException {
        String sql = BASE_SELECT + "WHERE s.student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    @Override
    public List<Student> findAll(String sortBy) throws SQLException {
        // Column names cannot be "?" parameters, so we only allow values from this fixed list.
        String orderColumn;
        switch (sortBy == null ? "id" : sortBy.toLowerCase()) {
            case "name":   orderColumn = "s.name";                break;
            case "age":    orderColumn = "s.age";                 break;
            case "course": orderColumn = "c.course_name, s.name"; break;
            default:       orderColumn = "s.student_id";
        }
        String sql = BASE_SELECT + "ORDER BY " + orderColumn;
        List<Student> students = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                students.add(mapRow(rs));
            }
        }
        return students;
    }

    @Override
    public List<Student> searchByName(String keyword) throws SQLException {
        String sql = BASE_SELECT + "WHERE s.name LIKE ? ORDER BY s.name";
        List<Student> students = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRow(rs));
                }
            }
        }
        return students;
    }

    @Override
    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, age = ?, course_id = ? WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());
            ps.setInt(4, student.getCourseId());
            ps.setInt(5, student.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean emailExists(String email, int excludeId) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE email = ? AND student_id <> ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public Map<String, Integer> countByCourse() throws SQLException {
        // LEFT JOIN + GROUP BY: also shows courses that have 0 students
        String sql = "SELECT c.course_name, COUNT(s.student_id) AS total "
                   + "FROM courses c LEFT JOIN students s ON c.course_id = s.course_id "
                   + "GROUP BY c.course_id, c.course_name ORDER BY c.course_name";
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("course_name"), rs.getInt("total"));
            }
        }
        return result;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("student_id"), rs.getString("name"), rs.getString("email"),
                rs.getInt("age"), rs.getInt("course_id"), rs.getString("course_name"));
    }
}
