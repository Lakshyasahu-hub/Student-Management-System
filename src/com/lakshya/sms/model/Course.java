package com.lakshya.sms.model;

/** A course a student can join (one row of the "courses" table). */
public class Course {
    private final int id;
    private final String name;
    private final int durationMonths;

    public Course(int id, String name, int durationMonths) {
        this.id = id;
        this.name = name;
        this.durationMonths = durationMonths;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getDurationMonths() { return durationMonths; }
}
