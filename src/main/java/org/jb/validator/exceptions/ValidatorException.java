package org.jb.validator.exceptions;

public class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }
    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
