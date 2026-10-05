package com.lakshya.sms;

import com.lakshya.sms.dao.CourseDAOImpl;
import com.lakshya.sms.dao.StudentDAOImpl;
import com.lakshya.sms.model.Course;
import com.lakshya.sms.model.Student;
import com.lakshya.sms.service.StudentService;
import com.lakshya.sms.service.StudentServiceException;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Console menu. It only talks to StudentService, never to the database directly. */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final StudentService SERVICE =
            new StudentService(new StudentDAOImpl(), new CourseDAOImpl());

    public static void main(String[] args) {
        System.out.println("=== Student Management System ===");
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            try {
                switch (choice) {
                    case 1: addStudent();      break;
                    case 2: viewAll();         break;
                    case 3: searchStudents();  break;
                    case 4: viewById();        break;
                    case 5: updateStudent();   break;
                    case 6: deleteStudent();   break;
                    case 7: showCourseReport();break;
                    case 8: showCourses();     break;
                    case 0: running = false;   break;
                    default: System.out.println("Please choose a number from the menu.");
                }
            } catch (StudentServiceException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add student");
        System.out.println("2. View all students (sorted)");
        System.out.println("3. Search students by name");
        System.out.println("4. View student by ID");
        System.out.println("5. Update student");
        System.out.println("6. Delete student");
        System.out.println("7. Course-wise student count");
        System.out.println("8. List courses");
        System.out.println("0. Exit");
    }

    private static void addStudent() throws StudentServiceException {
        showCourses();
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        int age = readInt("Age: ");
        int courseId = readInt("Course ID: ");
        int id = SERVICE.addStudent(name, email, age, courseId);
        System.out.println("Student added with ID " + id);
    }

    private static void viewAll() throws StudentServiceException {
        String sortBy = readLine("Sort by (id / name / age / course) [id]: ").trim();
        printStudents(SERVICE.getAll(sortBy.isEmpty() ? "id" : sortBy));
    }

    private static void searchStudents() throws StudentServiceException {
        printStudents(SERVICE.search(readLine("Enter part of the name: ")));
    }

    private static void viewById() throws StudentServiceException {
        printStudents(List.of(SERVICE.getById(readInt("Student ID: "))));
    }

    private static void updateStudent() throws StudentServiceException {
        int id = readInt("Student ID to update: ");
        Student current = SERVICE.getById(id);
        System.out.println("Press Enter to keep the current value.");
        String name = readLine("Name [" + current.getName() + "]: ");
        String email = readLine("Email [" + current.getEmail() + "]: ");
        int age = readOptionalInt("Age [" + current.getAge() + "]: ", current.getAge());
        showCourses();
        int courseId = readOptionalInt("Course ID [" + current.getCourseId() + "]: ", current.getCourseId());

        SERVICE.updateStudent(id,
                name.trim().isEmpty() ? current.getName() : name,
                email.trim().isEmpty() ? current.getEmail() : email,
                age, courseId);
        System.out.println("Student updated.");
    }

    private static void deleteStudent() throws StudentServiceException {
        int id = readInt("Student ID to delete: ");
        Student student = SERVICE.getById(id);
        String answer = readLine("Delete " + student.getName() + "? (y/n): ");
        if (answer.trim().equalsIgnoreCase("y")) {
            SERVICE.deleteStudent(id);
            System.out.println("Student deleted.");
        } else {
            System.out.println("Cancelled.");
        }
    }

    private static void showCourseReport() throws StudentServiceException {
        System.out.println("\n--- Students per course ---");
        for (Map.Entry<String, Integer> e : SERVICE.getCourseReport().entrySet()) {
            System.out.printf("%-15s %d%n", e.getKey(), e.getValue());
        }
    }

    private static void showCourses() throws StudentServiceException {
        System.out.println("\n--- Courses ---");
        for (Course c : SERVICE.getCourses()) {
            System.out.printf("%d. %s (%d months)%n", c.getId(), c.getName(), c.getDurationMonths());
        }
        System.out.println();
    }

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.printf("%-5s %-22s %-28s %-4s %-15s%n", "ID", "Name", "Email", "Age", "Course");
        System.out.println("-".repeat(78));
        for (Student s : students) {
            System.out.printf("%-5d %-22s %-28s %-4d %-15s%n",
                    s.getId(), s.getName(), s.getEmail(), s.getAge(), s.getCourseName());
        }
    }

    // ---------- safe input helpers (never crash on wrong input) ----------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SC.hasNextLine() ? SC.nextLine() : "";
    }

    private static int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static int readOptionalInt(String prompt, int defaultValue) {
        while (true) {
            String text = readLine(prompt).trim();
            if (text.isEmpty()) {
                return defaultValue;
            }
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number (or press Enter to keep).");
            }
        }
    }
}
