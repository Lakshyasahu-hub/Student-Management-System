package com.lakshya.sms.dao;

import com.lakshya.sms.model.Course;

import java.sql.SQLException;
import java.util.List;

public interface CourseDAO {
    List<Course> findAll() throws SQLException;
    boolean exists(int courseId) throws SQLException;
}
