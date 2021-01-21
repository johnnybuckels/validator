package org.jb.validator.models;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConstraintBuilderField<CT, FT> {

    protected Function<CT, FT> fieldGetter;

    protected ConstraintBuilderField(Function<CT, FT> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    public <X extends Comparable<X>> ConstraintBuilderFinalField<CT, FT, X> suchThatTarget(Function<FT, X> targetGetter){
        return new ConstraintBuilderFinalField<>(this, targetGetter);
    }

    // ----- "data" object validation

    public <X> Constraint<CT> isExistingUsing(Function<FT, Optional<X>> serviceFunction) {
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
                getValidationFailMessageFunction("Requested data object does not exist.")
        );
    }

    public <X> Constraint<CT> isAbsentUsing(Function<FT, Optional<X>> serviceFunction) {
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
                getValidationFailMessageFunction("Requested data object already exists.")
        );
    }

    public <X> Constraint<CT> isEmptyUsing(Function<FT, Collection<X>> serviceFunction) {
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
                getValidationFailMessageFunction("Requested data object collection is empty")
        );
    }

    public <X> Constraint<CT> isNotEmptyUsing(Function<FT, Collection<X>> serviceFunction) {
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
                getValidationFailMessageFunction("Requested data object collection is not empty")
        );
    }

    // ----- nested validator

    public <ET extends Throwable> Constraint<CT> doesNotThrowUsing(Consumer<FT> fieldConsumer){
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
        Function<CT, String> getCustomFailMessageFunction = objectToValidate -> {
            String throwMessage;
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            try {
                fieldConsumer.accept(fieldToValidate);
                throwMessage = "THIS MESSAGE SHOULD NEVER APPEAR";
            } catch (Throwable e) {
                throwMessage = e.toString();
            }
            return String.format(
                    "[object: %s, field value: %s]: An unwanted exception occurred (%s)",
                    objectToValidate.getClass().getSimpleName(),
                    fieldToValidate,
                    throwMessage
            );
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getCustomFailMessageFunction
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
