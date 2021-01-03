package org.jb.validator.models.content;

import java.util.Objects;
import java.util.function.Function;

public class Constraint<CT> {
    private final String constraintName;
    private final Function<CT, Boolean> isBoundConstraintViolatedFunction;
    private final Function<CT, Boolean> isNotNullConstraintViolatedFunction;
    private final Function<CT, String> notNullValidationFailMessageFunction; // only created when starting an actual validation
    private final Function<CT, String> boundValidationFailMessageFunction; // only created when starting an actual validation

    private String validationFailMessage;

    // ----- Constructors

    protected Constraint(String constraintName,
                         Function<CT, Boolean> isBoundConstraintViolatedFunction,
                         Function<CT, Boolean> isNotNullConstraintViolatedFunction,
                         Function<CT, String> boundValidationFailMessageFunction, Function<CT, String> notNullValidationFailMessageFunction) {
        this.constraintName = constraintName;
        this.isBoundConstraintViolatedFunction = isBoundConstraintViolatedFunction;
        this.isNotNullConstraintViolatedFunction = isNotNullConstraintViolatedFunction;
        this.notNullValidationFailMessageFunction = notNullValidationFailMessageFunction;
        this.boundValidationFailMessageFunction = boundValidationFailMessageFunction;
    }


    // ----- Core Functions

    public boolean isConstraintViolated(CT objectToValidate) {
        boolean isViolated;
        if(isNotNullConstraintViolatedFunction.apply(objectToValidate)){
            isViolated = true;
            validationFailMessage = notNullValidationFailMessageFunction.apply(objectToValidate);
        } else if (isBoundConstraintViolatedFunction.apply(objectToValidate)){
            isViolated = true;
            validationFailMessage = boundValidationFailMessageFunction.apply(objectToValidate);
        } else {
            isViolated = false;
            validationFailMessage = "";
        }
        return isViolated;
    }

    public String getValidationFailMessage() {
        return Objects.requireNonNullElse(validationFailMessage, "");
    }

    public String getConstraintName() {
        return constraintName;
    }

    // ----- Builder initializers

    public static <X, Y> FieldConstraintBuilder<X, Y> forField(Function<X, Y> fieldGetter) {
        return new FieldConstraintBuilder<>(fieldGetter);
    }
}
