# Student Management System

A console-based Java application to manage student records, using **JDBC** and a **MySQL** database.

## Features
- Add, view, update and delete students (full CRUD)
- Search students by name and sort by ID, name, age or course
- Input validation (name, email format, age range, valid course, unique email)
- Course-wise student count report (SQL `JOIN` + `GROUP BY`)
- Layered design: `model` -> `dao` -> `service` -> menu (`Main`)
- `PreparedStatement` used for all queries to prevent SQL injection

## Tech Used
Java, JDBC, MySQL, Object-Oriented Programming, Git

## Project Structure
```
src/com/lakshya/sms/
  model/    Student, Course             (data classes)
  dao/      StudentDAO, CourseDAO       (interfaces)
            StudentDAOImpl, CourseDAOImpl  (JDBC + SQL)
  service/  StudentService              (validation and business rules)
  util/     DBConnection                (database connection)
  Main.java                             (console menu)
test/       StudentServiceTest          (tests for validation rules, no database needed)
schema.sql                              (creates the database and tables)
```

## How to Run

**1. Requirements:** JDK 11 or higher, MySQL Server, and the MySQL Connector/J jar
(download from https://dev.mysql.com/downloads/connector/j/ - choose "Platform Independent", extract, and copy the `mysql-connector-j-x.x.x.jar` file into the `lib/` folder).

**2. Create the database:** open MySQL and run `schema.sql`
```
mysql -u root -p < schema.sql
```

**3. Add your database details:** copy `db.properties.example` to `db.properties` and put your MySQL password in it.

**4. Compile and run** (from the project folder)

Windows:
```
dir /s /b src\*.java > sources.txt
javac -cp "lib/*" -d out @sources.txt
java -cp "out;lib/*" com.lakshya.sms.Main
```
Linux / Mac:
```
javac -cp "lib/*" -d out $(find src -name "*.java")
java -cp "out:lib/*" com.lakshya.sms.Main
```

## Run the tests (no database needed)
Windows:
```
dir /s /b src\*.java test\*.java > all.txt
javac -d out @all.txt
java -cp out com.lakshya.sms.StudentServiceTest
```
Linux / Mac:
```
javac -d out $(find src test -name "*.java")
java -cp out com.lakshya.sms.StudentServiceTest
```

## Screenshots
_Add 2-3 screenshots of the program running here._

## Author
Lakshya Sahu - [LinkedIn](https://www.linkedin.com/in/lakshya-825-sahu) | [LeetCode](https://leetcode.com/u/Lakshya62/)
