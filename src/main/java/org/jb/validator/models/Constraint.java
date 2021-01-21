package org.jb.validator.models;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public class Constraint<CT> {

    private final Function<CT, Boolean> isConstraintViolatedFunction;
    private final Function<CT, String> validationFailMessageFunction;

    private String constraintName;

    // ----- Constructors

    protected Constraint(Function<CT, Boolean> isConstraintViolatedFunction,
                         Function<CT, String> validationFailMessageFunction) {
        this.isConstraintViolatedFunction = isConstraintViolatedFunction;
        this.validationFailMessageFunction = validationFailMessageFunction;
    }


    // ----- Core Functions

    /**
     * Validate the given object and return a validation message.
     * If the validation passed, the returned string will be empty.
     */
    public String validateObject(CT objectToValidate) {
        String message;
        if (isConstraintViolatedFunction.apply(objectToValidate)){
            message = String.format(
                    "Constraint %s was violated %s",
                    Objects.requireNonNullElse(constraintName, "ANONYMOUS"),
                    validationFailMessageFunction.apply(objectToValidate)
            );
        } else {
            message = "";
        }
        return message;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public Constraint<CT> setConstraintName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    // ----- Builder initializers

    public static <X, Y> ConstraintBuilderField<X, Y> forField(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderField<>(fieldGetter);
    }

    public static <X, Y extends Collection<R>, R> ConstraintBuilderCollectionItem<X, R, Y> forItemsInCollection(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderCollectionItem<>(fieldGetter);
    }

    public static <X, Y> Constraint<X> forNotNullField(Function<X, Y> fieldGetter) {
        return new Constraint<>(
                objectToValidate ->
                        fieldGetter.apply(objectToValidate) == null,
                objectToValidate ->
                        String.format("Validation failed on object %s: Field value was null", objectToValidate.getClass().getSimpleName())
        );
    }
}
