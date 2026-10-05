package com.lakshya.sms.dao;

import com.lakshya.sms.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** DAO = Data Access Object. Only this layer talks to the database about students. */
public interface StudentDAO {
    int add(Student student) throws SQLException;                       // returns the new id
    Optional<Student> findById(int id) throws SQLException;
    List<Student> findAll(String sortBy) throws SQLException;           // sortBy: id, name, age, course
    List<Student> searchByName(String keyword) throws SQLException;
    boolean update(Student student) throws SQLException;
    boolean delete(int id) throws SQLException;
    boolean emailExists(String email, int excludeId) throws SQLException;
    Map<String, Integer> countByCourse() throws SQLException;
}
