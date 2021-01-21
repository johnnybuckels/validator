package org.jb.validator.models;

import org.jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.Collection;
import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class ConstraintBuilderFinalCollectionItem<CT, IT, FT extends Collection<IT>, BT extends Comparable<BT>> {

    // --- internal fields for construction
    protected Function<IT, BT> targetGetter;
    protected ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;

    protected ConstraintBuilderFinalCollectionItem(ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem, Function<IT, BT> targetGetter) {
        this.constraintBuilderCollectionItem = constraintBuilderCollectionItem;
        this.targetGetter = targetGetter;
    }

    public Constraint<CT> isLessThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAX;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    public Constraint<CT> isLessOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAXINCL;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    public Constraint<CT> isGreaterThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MIN;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    public Constraint<CT> isGreaterOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MININCL;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    public Constraint<CT> isEqualTo(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.EXACT;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }


    // ----- building

    public Constraint<CT> buildWithName(String constraintName) {

        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    private Function<CT, Boolean> createBoundValidationFunction() {
        return objectToValidate -> {
            boolean isViolated;
            FT fieldToValidate = constraintBuilderCollectionItem.fieldGetter.apply(objectToValidate);
            if(fieldToValidate != null) {
                isViolated = fieldToValidate.stream()
                        .anyMatch(collectionItem ->
                                boundType.getIsBoundBrokenFunction().apply(
                                        targetGetter.apply(collectionItem).compareTo(boundValue)
                                )
                );
            } else {
                isViolated = false;
            }
            return isViolated;
        };
    }

    private Function<CT, String> createBoundFailMessageFunction() {
        int maxStringLength = 100;
        String endPart = "...]";
        return objectToValidate -> {
            String collectionString = constraintBuilderCollectionItem.fieldGetter.apply(objectToValidate).toString();
            if(collectionString.length() > maxStringLength) {
                collectionString = collectionString.substring(0, maxStringLength - endPart.length()) + endPart;
            }
            return String.format(
                    "[object: %s, collection: %s]: The target value of every item must be %s %s",
                    objectToValidate.getClass().getSimpleName(),
                    collectionString,
                    boundType.getBoundAssertionString(),
                    boundValue
            );
        };
    }

}
