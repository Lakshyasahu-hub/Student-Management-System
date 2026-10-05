package com.lakshya.sms.service;

import com.lakshya.sms.dao.CourseDAO;
import com.lakshya.sms.dao.StudentDAO;
import com.lakshya.sms.model.Course;
import com.lakshya.sms.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Business logic layer: validates input and applies the rules, then calls the DAO.
 * The menu (Main) never talks to the database directly - it only calls this class.
 */
public class StudentService {

    private static final String NAME_REGEX = "[A-Za-z][A-Za-z .'-]{1,49}";
    private static final String EMAIL_REGEX = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
    private static final int MIN_AGE = 16;
    private static final int MAX_AGE = 60;

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    public StudentService(StudentDAO studentDAO, CourseDAO courseDAO) {
        this.studentDAO = studentDAO;
        this.courseDAO = courseDAO;
    }

    public int addStudent(String name, String email, int age, int courseId) throws StudentServiceException {
        Student student = new Student(name.trim(), email.trim().toLowerCase(), age, courseId);
        validate(student, 0);
        try {
            return studentDAO.add(student);
        } catch (SQLException e) {
            throw new StudentServiceException("Could not add the student: " + e.getMessage(), e);
        }
    }

    public Student getById(int id) throws StudentServiceException {
        try {
            return studentDAO.findById(id)
                    .orElseThrow(() -> new StudentServiceException("No student found with id " + id));
        } catch (SQLException e) {
            throw new StudentServiceException("Could not read the student: " + e.getMessage(), e);
        }
    }

    public List<Student> getAll(String sortBy) throws StudentServiceException {
        try {
            return studentDAO.findAll(sortBy);
        } catch (SQLException e) {
            throw new StudentServiceException("Could not read students: " + e.getMessage(), e);
        }
    }

    public List<Student> search(String keyword) throws StudentServiceException {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new StudentServiceException("Search text cannot be empty.");
        }
        try {
            return studentDAO.searchByName(keyword.trim());
        } catch (SQLException e) {
            throw new StudentServiceException("Search failed: " + e.getMessage(), e);
        }
    }

    public void updateStudent(int id, String name, String email, int age, int courseId)
            throws StudentServiceException {
        getById(id);                                   // throws if the student does not exist
        Student student = new Student(name.trim(), email.trim().toLowerCase(), age, courseId);
        student.setId(id);
        validate(student, id);
        try {
            studentDAO.update(student);
        } catch (SQLException e) {
            throw new StudentServiceException("Could not update the student: " + e.getMessage(), e);
        }
    }

    public void deleteStudent(int id) throws StudentServiceException {
        try {
            if (!studentDAO.delete(id)) {
                throw new StudentServiceException("No student found with id " + id);
            }
        } catch (SQLException e) {
            throw new StudentServiceException("Could not delete the student: " + e.getMessage(), e);
        }
    }

    public List<Course> getCourses() throws StudentServiceException {
        try {
            return courseDAO.findAll();
        } catch (SQLException e) {
            throw new StudentServiceException("Could not read courses: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getCourseReport() throws StudentServiceException {
        try {
            return studentDAO.countByCourse();
        } catch (SQLException e) {
            throw new StudentServiceException("Could not build the report: " + e.getMessage(), e);
        }
    }

    /** Checks every rule. excludeId lets a student keep their own email while updating. */
    private void validate(Student s, int excludeId) throws StudentServiceException {
        if (!s.getName().matches(NAME_REGEX)) {
            throw new StudentServiceException("Name must be 2-50 characters (letters, spaces, . ' -).");
        }
        if (!s.getEmail().matches(EMAIL_REGEX) || s.getEmail().length() > 100) {
            throw new StudentServiceException("Email is not valid.");
        }
        if (s.getAge() < MIN_AGE || s.getAge() > MAX_AGE) {
            throw new StudentServiceException("Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
        try {
            if (!courseDAO.exists(s.getCourseId())) {
                throw new StudentServiceException("Course id " + s.getCourseId() + " does not exist.");
            }
            if (studentDAO.emailExists(s.getEmail(), excludeId)) {
                throw new StudentServiceException("A student with this email already exists.");
            }
        } catch (SQLException e) {
            throw new StudentServiceException("Database error while validating: " + e.getMessage(), e);
        }
    }
}
