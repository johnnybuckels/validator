package jb.validator.models;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a constraint for a single field of the object to validate.
 * @param <CT> the type of the object toi validate.
 */
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

    public Constraint<CT> addName(String constraintName) {
        this.constraintName = constraintName;
        return this;
    }

    // ----- Builder initializers

    /**
     * Creates a builder for this constraint concerning a single field of the object under validation.
     * @param fieldGetter supplier of the field value under validation. Usually this is should be a reference to an instance
     *                    method of type X (X::getMyField)
     * @param <X> type of the object under validation.
     * @param <Y> type of the field under validation.
     * @return a builder for a constraint concerning a single field.
     */
    public static <X, Y> ConstraintBuilderField<X, Y> forField(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderField<>(fieldGetter);
    }

    /**
     * Creates a builder for this constraint concerning items of some collection-field within the object under validation.
     * @param fieldGetter supplier of the field value under validation. Usually this is should be a reference to an instance
     *                    method of type X (X::getMyField)
     * @param <X> type of the object under validation.
     * @param <Y> type of the field under validation.
     * @param <R> type of the items within the collection.
     * @return a builder for a constraint concerning items within a collection-field.
     */
    public static <X, Y extends Collection<R>, R> ConstraintBuilderCollectionItem<X, R, Y> forItemsInCollection(Function<X, Y> fieldGetter) {
        return new ConstraintBuilderCollectionItem<>(fieldGetter);
    }

}
