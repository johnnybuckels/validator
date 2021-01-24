package jb.validator.exceptions;

/**
 * This exception is thrown, when the validation of an object failed.
 * */
public class ValidatorException extends RuntimeException{
    public ValidatorException(String message) {
        super(message);
    }
    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
