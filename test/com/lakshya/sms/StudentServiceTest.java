package com.lakshya.sms;

import com.lakshya.sms.dao.CourseDAO;
import com.lakshya.sms.dao.StudentDAO;
import com.lakshya.sms.model.Course;
import com.lakshya.sms.model.Student;
import com.lakshya.sms.service.StudentService;
import com.lakshya.sms.service.StudentServiceException;

import java.util.*;

/**
 * Tests the validation rules in StudentService using in-memory fake DAOs,
 * so NO database is needed. Run it to check the business logic works.
 */
public class StudentServiceTest {

    static int passed = 0, failed = 0;

    public static void main(String[] args) throws Exception {
        FakeStudentDAO students = new FakeStudentDAO();
        StudentService service = new StudentService(students, new FakeCourseDAO());

        // valid add
        int id = service.addStudent("Rahul Sharma", "Rahul@Mail.com", 20, 1);
        check("valid student is added", id == 1);
        check("email is stored in lower case", service.getById(id).getEmail().equals("rahul@mail.com"));

        // invalid inputs
        expectError("empty name rejected", () -> service.addStudent("  ", "a@b.com", 20, 1));
        expectError("name with digits rejected", () -> service.addStudent("R2D2", "a@b.com", 20, 1));
        expectError("bad email rejected", () -> service.addStudent("Amit", "not-an-email", 20, 1));
        expectError("age too low rejected", () -> service.addStudent("Amit", "amit@b.com", 10, 1));
        expectError("age too high rejected", () -> service.addStudent("Amit", "amit@b.com", 99, 1));
        expectError("unknown course rejected", () -> service.addStudent("Amit", "amit@b.com", 20, 99));
        expectError("duplicate email rejected", () -> service.addStudent("Other", "rahul@mail.com", 21, 1));

        // update
        int id2 = service.addStudent("Priya Verma", "priya@mail.com", 22, 2);
        service.updateStudent(id2, "Priya V", "priya@mail.com", 23, 2);   // keeps her own email: allowed
        check("update keeps own email", service.getById(id2).getName().equals("Priya V"));
        expectError("update to someone else's email rejected",
                () -> service.updateStudent(id2, "Priya V", "rahul@mail.com", 23, 2));
        expectError("update of missing student rejected",
                () -> service.updateStudent(500, "Amit", "amit@b.com", 20, 1));

        // search / sort / delete
        check("search finds match", service.search("rah").size() == 1);
        expectError("empty search rejected", () -> service.search("   "));
        check("sort by name works", service.getAll("name").get(0).getName().equals("Priya V"));
        service.deleteStudent(id);
        expectError("get deleted student fails", () -> service.getById(id));
        expectError("delete missing student fails", () -> service.deleteStudent(id));

        System.out.println("\nPassed: " + passed + "  Failed: " + failed);
        if (failed > 0) System.exit(1);
    }

    interface Action { void run() throws Exception; }

    static void expectError(String name, Action a) {
        try { a.run(); check(name, false); }
        catch (StudentServiceException e) { check(name, true); }
        catch (Exception e) { check(name + " (wrong exception " + e + ")", false); }
    }

    static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "PASS  " : "FAIL  ") + name);
    }

    // ---------- fake in-memory DAOs ----------
    static class FakeCourseDAO implements CourseDAO {
        public List<Course> findAll() {
            return List.of(new Course(1, "B.Tech CSE", 48), new Course(2, "BCA", 36));
        }
        public boolean exists(int id) { return id == 1 || id == 2; }
    }

    static class FakeStudentDAO implements StudentDAO {
        Map<Integer, Student> data = new TreeMap<>();
        int next = 1;
        public int add(Student s) { s.setId(next); data.put(next, s); return next++; }
        public Optional<Student> findById(int id) { return Optional.ofNullable(data.get(id)); }
        public List<Student> findAll(String sortBy) {
            List<Student> list = new ArrayList<>(data.values());
            if ("name".equals(sortBy)) list.sort(Comparator.comparing(Student::getName));
            return list;
        }
        public List<Student> searchByName(String k) {
            List<Student> r = new ArrayList<>();
            for (Student s : data.values()) if (s.getName().toLowerCase().contains(k.toLowerCase())) r.add(s);
            return r;
        }
        public boolean update(Student s) { return data.replace(s.getId(), s) != null; }
        public boolean delete(int id) { return data.remove(id) != null; }
        public boolean emailExists(String email, int excludeId) {
            for (Student s : data.values()) if (s.getEmail().equals(email) && s.getId() != excludeId) return true;
            return false;
        }
        public Map<String, Integer> countByCourse() { return new LinkedHashMap<>(); }
    }
}
