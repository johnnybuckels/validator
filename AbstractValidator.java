package jb.validator.models;

import jb.validator.exceptions.ValidatorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Abstract representation of a validator concerning a specific class type T.
 * The abstract method is constraintSupplier() which needs to return a collection of Constraint<T>,
 * defining the set of constraints that this validator validates during the call of validate.
 * @param <CT> Class type of the objects that are validated by this validator.
 */
public abstract class AbstractValidator<CT> {

    private final String delimiter = "; ";

    /**
     * Supplier of a collection of constraints defining the validation process of this validator.
     * @return Any collection of constraints concerning this validators class type.
     */
    public abstract Collection<Constraint<CT>> constraintSupplier();

    /**
     * Supplier for the exception type that should be registered as cause if any validation fails. If this supplier
     * returns null, no cause will be set.
     * @return the exception type that is used as a cause fro failing validations.
     */
    public abstract Class<? extends Throwable> exceptionSupplier();

    /**
     * Core function of this validator. Validates the collection of constraints returned by this.constraintSupplier().
     * This method throws a ValidatorException possibly encapsulating another custom exception if any constraint is violated.
     * The encapsulated exception type is chosen according to the return value of this.exceptionSupplier().
     * Each violated constraint produces an informative String which all are put together in the thrown exception's message,
     * as long as the exception class provides a string argument constructor.
     * @param objectToValidate object that should be validated
     * @throws ValidatorException if any of the registered constraints was violated. The cause of this exception will be set to
     *  the exception type given by this.exceptionSupplier().
     */
    public final void validate(CT objectToValidate) throws ValidatorException {
        Collection<Constraint<CT>> collectedConstraints = constraintSupplier();
        if(collectedConstraints == null) {
            return;
        }
        StringBuilder messageBuilder = new StringBuilder();
        collectedConstraints.forEach(constraint -> {
                String constraintEvaluationMessage = constraint.validateObject(objectToValidate);
                if(!constraintEvaluationMessage.isEmpty()) {
                    messageBuilder.append(delimiter).append(constraintEvaluationMessage);
                }
        });
        if(messageBuilder.length() != 0) {
            throwExceptionWithMessage(messageBuilder.substring(delimiter.length()));
        }
    }

    /**
     * Constructs a ValidatorException with the given errorMessage and puts the returned exception from this.exceptionSupplier()
     * as the cause. If this.exceptionSupplier() returns null, no cause will be set.
     */
    private void throwExceptionWithMessage(String errorMessage) throws ValidatorException {
        Class<? extends Throwable> exception = exceptionSupplier();
        if(exception == null) {
            throw new ValidatorException(errorMessage);
        }
        String messagePrefix = "(%s) %s";
        try {
            Constructor<? extends Throwable> constructor = exceptionSupplier().getDeclaredConstructor(String.class);
            Throwable customException = constructor.newInstance(errorMessage);
            throw new ValidatorException(String.format(messagePrefix, customException.getClass().getSimpleName(), errorMessage), customException);
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InstantiationException | InvocationTargetException e) {
            try {
                Constructor<? extends Throwable> constructor = exceptionSupplier().getDeclaredConstructor();
                Throwable customException = constructor.newInstance();
                throw new ValidatorException(String.format(messagePrefix, customException.getClass().getSimpleName(), errorMessage), customException);
            } catch (Exception ignored) {
                throw new ValidatorException(errorMessage);
            }
        }
    }
}
