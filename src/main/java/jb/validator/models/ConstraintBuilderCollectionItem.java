package jb.validator.models;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConstraintBuilderCollectionItem<CT, IT, FT extends Collection<IT>> implements ServiceConstraintFinalizer<CT, IT>{

    protected Function<CT, FT> fieldGetter;

    protected ConstraintBuilderCollectionItem(Function<CT, FT> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    /**
     * Walks one step further trough the constraint building process by defining the target value of the collections items under validation.
     * Examples: String::length, Integer::signum, BigDecimal::precision, Collection::size, x -> x
     * @param targetGetter supplier for the value derived from the field value that is the actual target of this constraint.
     * @param <X> type of the target value (must implement Comparable<X>).
     * @return the next constraint builder in the building process
     */
    public <X extends Comparable<X>> ConstraintBuilderFinalCollectionItem<CT, IT, FT, X> suchThatTarget(Function<IT, X> targetGetter){
        return new ConstraintBuilderFinalCollectionItem<>(this, targetGetter);
    }

    // ----- "data" object validation

    public <X> Constraint<CT> assertExistenceUsing(Function<IT, Optional<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null || fieldToValidate.isEmpty()) {
                return false;
            } else {
                return fieldToValidate.stream().anyMatch(item -> serviceFunction.apply(item).isEmpty());
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(existenceFailMessageDefault)
        );
    }

    public <X> Constraint<CT> assertAbsenceUsing(Function<IT, Optional<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null || fieldToValidate.isEmpty()) {
                return false;
            } else {
                return fieldToValidate.stream().anyMatch(item -> serviceFunction.apply(item).isPresent());
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(absenceFailMessageDefault)
        );
    }

    public <X> Constraint<CT> assertEmptyUsing(Function<IT, Collection<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null || fieldToValidate.isEmpty()) {
                return false;
            } else {
                return fieldToValidate.stream().anyMatch(item -> serviceFunction.apply(item).isEmpty());
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(emptyCollectionFailMessageDefault)
        );
    }

    public <X> Constraint<CT> assertNotEmptyUsing(Function<IT, Collection<X>> serviceFunction) {
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if(fieldToValidate == null || fieldToValidate.isEmpty()) {
                return false;
            } else {
                return fieldToValidate.stream().anyMatch(item -> !serviceFunction.apply(item).isEmpty());
            }
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getValidationFailMessageFunction(nonEmptyCollectionFailMessageDefault)
        );
    }

    // ----- nested validator

    public Constraint<CT> assertNotThrowingUsing(Consumer<IT> fieldConsumer){
        Function<CT, Boolean> isConstraintViolatedFunction = objectToValidate -> {
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            if (fieldToValidate != null && !fieldToValidate.isEmpty()) {
                for(IT item : fieldToValidate) {
                    try {
                        fieldConsumer.accept(item);
                    } catch (Throwable e) {
                        return true;
                    }
                }
            }
            return false;
        };
        // When this function is actually called, fieldGetter.apply(objectToValidate) does not return null or an empty list (since isConstraintViolatedFunction evaluated to true)
        Function<CT, String> getCustomFailMessageFunction = objectToValidate -> {
            String throwMessage = "THIS MESSAGE SHOULD NEVER APPEAR"; // the catch block is expected to get triggered
            FT fieldToValidate = fieldGetter.apply(objectToValidate);
            for(IT item : fieldToValidate) {
                try {
                    fieldConsumer.accept(item);
                } catch (Throwable e) {
                    throwMessage = e.toString();
                    break;
                }
            }
            return getValidationFailMessageFunction(throwMessage).apply(objectToValidate);
        };
        return new Constraint<>(
                isConstraintViolatedFunction,
                getCustomFailMessageFunction
        );
    }

    // ----- helper functions

    private Function<CT, String> getValidationFailMessageFunction(String failMessageContent) {
        int maxStringLength = 100;
        String endPart = "...]";
        return objectToValidate -> {
            String collectionString = fieldGetter.apply(objectToValidate).toString();
            if(collectionString.length() > maxStringLength) {
                collectionString = collectionString.substring(0, maxStringLength - endPart.length()) + endPart;
            }
            return String.format(
                    "[object: %s, collection: %s]: Some item caused a failure (%s)",
                    objectToValidate.getClass().getSimpleName(),
                    collectionString,
                    failMessageContent
            );
        };
    }

}
