package com.lakshya.sms.dao;

import com.lakshya.sms.model.Course;
import com.lakshya.sms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {

    @Override
    public List<Course> findAll() throws SQLException {
        String sql = "SELECT course_id, course_name, duration_months FROM courses ORDER BY course_id";
        List<Course> courses = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(rs.getInt("course_id"), rs.getString("course_name"),
                        rs.getInt("duration_months")));
            }
        }
        return courses;
    }

    @Override
    public boolean exists(int courseId) throws SQLException {
        String sql = "SELECT 1 FROM courses WHERE course_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
