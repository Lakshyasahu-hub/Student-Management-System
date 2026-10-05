package com.lakshya.sms.service;

/** Thrown when input is invalid or something goes wrong; the message is safe to show to the user. */
public class StudentServiceException extends Exception {
    private static final long serialVersionUID = 1L;

    public StudentServiceException(String message) {
        super(message);
    }

    public StudentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
