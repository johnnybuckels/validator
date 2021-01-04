package org.jb.validator.models.content;

import java.util.Collection;
import java.util.function.Function;

public class Constraint<CT> {
    private final String constraintName;
    private final Function<CT, Boolean> isBoundConstraintViolatedFunction;
    private final Function<CT, Boolean> isNotNullConstraintViolatedFunction;
    private final Function<CT, String> notNullValidationFailMessageFunction; // only created when starting an actual validation
    private final Function<CT, String> boundValidationFailMessageFunction; // only created when starting an actual validation
    private final String validationFailMessagePrefix;

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
        validationFailMessagePrefix = String.format("Constraint %s was violated: ", this.constraintName);
    }


    // ----- Core Functions

    /**
     * Validate the given object and return a validation message.
     * If the validation passed, the returned string will be empty.
     */
    public String validate(CT objectToValidate) {
        String message;
        if(isNotNullConstraintViolatedFunction.apply(objectToValidate)){
            message = validationFailMessagePrefix + notNullValidationFailMessageFunction.apply(objectToValidate);
        } else if (isBoundConstraintViolatedFunction.apply(objectToValidate)){
            message = validationFailMessagePrefix + boundValidationFailMessageFunction.apply(objectToValidate);
        } else {
            message = "";
        }
        return message;
    }

    public String getConstraintName() {
        return constraintName;
    }

    // ----- Builder initializers

    public static <X, Y> ConstraintBuilderField<X, Y> forField(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderField<>(fieldGetter);
    }

    public static <X, Y extends Collection<R>, R> ConstraintBuilderCollectionItem<X, R, Y> forItemsInCollection(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderCollectionItem<>(fieldGetter);
    }
}
