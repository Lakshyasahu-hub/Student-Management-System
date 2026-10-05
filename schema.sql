-- Run this file once in MySQL to create the database and tables.
CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS courses (
    course_id       INT AUTO_INCREMENT PRIMARY KEY,
    course_name     VARCHAR(60) NOT NULL UNIQUE,
    duration_months INT NOT NULL
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    age        INT NOT NULL,
    course_id  INT NOT NULL,
    CONSTRAINT fk_student_course FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

INSERT IGNORE INTO courses (course_name, duration_months) VALUES
    ('B.Tech CSE', 48),
    ('BCA', 36),
    ('MCA', 24),
    ('MBA', 24),
    ('B.Sc IT', 36);
