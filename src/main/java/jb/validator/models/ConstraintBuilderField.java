package jb.validator.models;

import jb.validator.ThrowingConsumer;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class ConstraintBuilderField<CT, FT> implements ServiceConstraintFinalizer<CT, FT>{

    protected Function<CT, FT> fieldGetter;

    protected ConstraintBuilderField(Function<CT, FT> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    /**
     * Walks one step further trough the constraint building process by defining the target value of the field under validation.
     * Examples: String::length, Integer::signum, BigDecimal::precision, Collection::size, x -> x
     * @param targetGetter supplier for the value derived from the field value that is the actual target of this constraint.
     * @param <X> type of the target value (must implement Comparable<X>).
     * @return the next constraint builder in the building process
     */
    public <X extends Comparable<X>> ConstraintBuilderFinalField<CT, FT, X> suchThatTarget(Function<FT, X> targetGetter){
        return new ConstraintBuilderFinalField<>(this, targetGetter);
    }

    // ----- "data" object validation

    public <X> Constraint<CT> presentUsing(Function<FT, Optional<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null) {
                return false;
            } else {
                return serviceFunction.apply(fieldToValidate).isEmpty();
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(existenceFailMessageDefault)
        );
    }

    public <X> Constraint<CT> absentUsing(Function<FT, Optional<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null) {
                return false;
            } else {
                return serviceFunction.apply(fieldToValidate).isPresent();
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(absenceFailMessageDefault)
        );
    }

    public <X> Constraint<CT> emptyUsing(Function<FT, Collection<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null) {
                return false;
            } else {
                return serviceFunction.apply(fieldToValidate).isEmpty();
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(emptyCollectionFailMessageDefault)
        );
    }

    public <X> Constraint<CT> notEmptyUsing(Function<FT, Collection<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null) {
                return false;
            } else {
                return !serviceFunction.apply(fieldToValidate).isEmpty();
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(nonEmptyCollectionFailMessageDefault)
        );
    }

    // ----- nested validator

    public Constraint<CT> noThrowsUsing(ThrowingConsumer<FT> fieldConsumer){
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if (fieldToValidate != null) {
                try {
                    fieldConsumer.accept(fieldToValidate);
                } catch (Throwable e) {
                    return true;
                }
            }
            return false;
        };
        // When this function is actually called, fieldGetter.apply(objectToValidate) does not return null or an empty list (since isConstraintViolatedFunction evaluated to true)
        Function<CT, String> getCustomFailMessageFunction = objectToValidate -> {
            String throwMessage = "THIS MESSAGE SHOULD NEVER APPEAR";  // this value will be overwritten by the catch block which is expected to get triggered
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if (fieldToValidate != null) {
                try {
                    fieldConsumer.accept(fieldToValidate);
                } catch (Throwable e) {
                    throwMessage = e.toString();
                }
                return String.format(
                        "[object: %s, field value: %s]: An unwanted exception occurred (%s)",
                        objectToValidate.getClass().getSimpleName(),
                        fieldToValidate,
                        throwMessage
                );
            } else {
                return "";
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getCustomFailMessageFunction
        );
    }

    // ----- not null

    /**
     * Creates a simple not-null constraint for the given field.
     * @return a not-null constraint for the respective field.
     */
    public Constraint<CT> notNull() {
        return new Constraint<>(
                objectToValidate ->
                        fieldGetter.apply(objectToValidate) == null,
                objectToValidate ->
                        String.format("[object: %s, field value: %s]: Value of a required field was null",
                                objectToValidate.getClass().getSimpleName(), fieldGetter.apply(objectToValidate)
                        )
        );
    }

    // ----- Fail Messages

    private Function<CT, String> getValidationFailMessageFunction(String failMessageContent) {
        return objectToValidate -> String.format(
                "[object: %s, field value: %s]: %s",
                objectToValidate.getClass().getSimpleName(),
                fieldGetter.apply(objectToValidate),
                failMessageContent
        );
    }

}
