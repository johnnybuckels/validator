package org.jb.validator.models;

import org.jb.validator.exceptions.ValidatorException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public abstract class AbstractValidator<CT> {


    public abstract Collection<Constraint<CT>> constraintSupplier();
    public abstract Class<? extends Throwable> exceptionSupplier();

    private final String delimiter = "; ";


    public void validate(CT objectToValidate) throws ValidatorException {
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

    private void throwExceptionWithMessage(String errorMessage) throws ValidatorException {
        Class<? extends Throwable> exception = exceptionSupplier();
        if(exception == null) {
            throw new ValidatorException(errorMessage);
        }
        String prefixMessage = "Captured a thrown exception: ";
        try {
            Constructor<? extends Throwable> constructor = exceptionSupplier().getDeclaredConstructor(String.class);
            throw new ValidatorException(prefixMessage + constructor.newInstance(errorMessage).toString());
        } catch (IllegalAccessException | NoSuchMethodException | SecurityException | InstantiationException | InvocationTargetException e) {
            try {
                Constructor<? extends Throwable> constructor = exceptionSupplier().getDeclaredConstructor();
                throw new ValidatorException(prefixMessage + constructor.newInstance().toString() + " (" + errorMessage + ")");
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException instantiationException) {
                throw new ValidatorException(errorMessage);
            }
        }
    }
}
