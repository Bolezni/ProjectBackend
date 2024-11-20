package org.example.testprojectback.exceptions;

public class FriendShipAlreadyExistsException extends RuntimeException{
    public FriendShipAlreadyExistsException(String message) {
        super(message);
    }
}
