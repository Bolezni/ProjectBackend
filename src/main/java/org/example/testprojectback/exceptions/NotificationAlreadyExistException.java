package org.example.testprojectback.exceptions;

public class NotificationAlreadyExistException extends RuntimeException {
    public NotificationAlreadyExistException(String message) {
        super(message);
    }
}
