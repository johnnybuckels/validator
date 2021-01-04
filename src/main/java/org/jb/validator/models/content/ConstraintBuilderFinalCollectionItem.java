package org.jb.validator.models.content;

import org.jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.Collection;
import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class ConstraintBuilderFinalCollectionItem<CT, IT, FT extends Collection<IT>, BT extends Comparable<BT>>{

    // --- internal fields for construction
    protected Function<IT, BT> targetGetter;
    protected ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;
    protected boolean nullable = true;

    protected ConstraintBuilderFinalCollectionItem(ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem, Function<IT, BT> targetGetter) {
        this.constraintBuilderCollectionItem = constraintBuilderCollectionItem;
        this.targetGetter = targetGetter;
    }

    // ----- appending

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isNotNullable() {
        this.nullable = false;
        return this;
    }

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isLessThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAX;
        return this;
    }

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isLessOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAXINCL;
        return this;
    }

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isGreaterThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MIN;
        return this;
    }

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isGreaterOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MININCL;
        return this;
    }

    public ConstraintBuilderFinalCollectionItem<CT, IT, FT, BT> isEqualTo(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.EXACT;
        return this;
    }


    // ----- building

    public Constraint<CT> buildWithName(String constraintName) {

        return new Constraint<>(constraintName,
                createBoundValidationFunction(), createNotNullValidationFunction(),
                createBoundFailMessageFunction(), createNotNullFailMessageFunction()
        );
    }

    private Function<CT, Boolean> createNotNullValidationFunction() {
        Function<CT, Boolean> fun;
        if(nullable) {
            fun = constraintBuilderCollectionItem.evaluateToFalseFunction;
        } else {
            fun = constraintBuilderCollectionItem.isNotNullConstraintViolatedFunction;
        }
        return fun;
    }

    private Function<CT, Boolean> createBoundValidationFunction() {
        Function<CT, Boolean> fun;
        if(boundValue == null) {
            fun = constraintBuilderCollectionItem.evaluateToFalseFunction;
        } else {
            fun = objectToValidate -> {
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
        return fun;
    }

    private Function<CT, String> createNotNullFailMessageFunction() {
        return constraintBuilderCollectionItem.notNullFailMessageFunction;
    }

    private Function<CT, String> createBoundFailMessageFunction() {
        return objectToValidate -> String.format(
                "Validation for collection failed [object: %s]: The target value of every item must be %s %s.",
                objectToValidate.getClass().getSimpleName(),
                boundType.getBoundAssertionString(),
                boundValue
        );
    }

}
