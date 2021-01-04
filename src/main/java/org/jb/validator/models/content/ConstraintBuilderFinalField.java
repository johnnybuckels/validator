package org.jb.validator.models.content;

import org.jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class ConstraintBuilderFinalField<CT, FT, BT extends Comparable<BT>> {

    // --- internal fields for construction
    protected Function<FT, BT> targetGetter;
    protected ConstraintBuilderField<CT, FT> constraintBuilderField;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;
    protected boolean nullable = true;

    // --- constraint key fields
    protected Function<CT, String> boundValidationFailMessageFunction;

    protected ConstraintBuilderFinalField(ConstraintBuilderField<CT, FT> constraintBuilderField, Function<FT, BT> targetGetter) {
        this.constraintBuilderField = constraintBuilderField;
        this.targetGetter = targetGetter;
    }

    // ----- appending

    public ConstraintBuilderFinalField<CT, FT, BT> isNotNullable() {
        this.nullable = false;
        return this;
    }

    public ConstraintBuilderFinalField<CT, FT, BT> isLessThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAX;
        return this;
    }

    public ConstraintBuilderFinalField<CT, FT, BT> isLessOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAXINCL;
        return this;
    }

    public ConstraintBuilderFinalField<CT, FT, BT> isGreaterThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MIN;
        return this;
    }

    public ConstraintBuilderFinalField<CT, FT, BT> isGreaterOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MININCL;
        return this;
    }

    public ConstraintBuilderFinalField<CT, FT, BT> isEqualTo(BT boundValue) {
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
            fun = constraintBuilderField.evaluateToFalseFunction;
        } else {
            fun = constraintBuilderField.isNotNullConstraintViolatedFunction;
        }
        return fun;
    }

    private Function<CT, Boolean> createBoundValidationFunction() {
        Function<CT, Boolean> fun;
        if(boundValue == null) {
            fun = constraintBuilderField.evaluateToFalseFunction;
        } else {
            fun = objectToValidate -> {
                boolean isViolated;
                FT fieldToValidate = constraintBuilderField.fieldGetter.apply(objectToValidate);
                if(fieldToValidate != null) {
                    isViolated = boundType.getIsBoundBrokenFunction().apply(
                            targetGetter.apply(fieldToValidate).compareTo(boundValue)
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
        return constraintBuilderField.notNullFailMessageFunction;
    }

    private Function<CT, String> createBoundFailMessageFunction() {
        return objectToValidate -> String.format(
                "Validation failed [object: %s, field value: %s]: Target value must be %s %s but was %s.",
                objectToValidate.getClass().getSimpleName(),
                constraintBuilderField.fieldGetter.apply(objectToValidate),
                boundType.getBoundAssertionString(),
                boundValue,
                constraintBuilderField.fieldGetter.andThen(targetGetter).apply(objectToValidate)
        );
    }

}
