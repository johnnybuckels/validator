package org.jb.validator.models.content;

import org.jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class FieldConstraintBuilderFinal<CT, FT, BT extends Comparable<BT>> {

    // --- internal fields for construction
    protected Function<FT, BT> targetGetter;
    protected FieldConstraintBuilder<CT, FT> fieldConstraintBuilder;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;
    protected boolean nullable = true;

    // --- constraint key fields
    protected Function<CT, String> boundValidationFailMessageFunction;

    protected FieldConstraintBuilderFinal(FieldConstraintBuilder<CT, FT> fieldConstraintBuilder, Function<FT, BT> targetGetter) {
        this.fieldConstraintBuilder = fieldConstraintBuilder;
        this.targetGetter = targetGetter;
    }

    // ----- appending

    public FieldConstraintBuilderFinal<CT, FT, BT> isNotNullable() {
        this.nullable = false;
        return this;
    }

    public FieldConstraintBuilderFinal<CT, FT, BT> isLessThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAX;
        return this;
    }

    public FieldConstraintBuilderFinal<CT, FT, BT> isLessOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MAXINCL;
        return this;
    }

    public FieldConstraintBuilderFinal<CT, FT, BT> isGreaterThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MIN;
        return this;
    }

    public FieldConstraintBuilderFinal<CT, FT, BT> isGreaterOrEqualThan(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.MININCL;
        return this;
    }

    public FieldConstraintBuilderFinal<CT, FT, BT> isEqualTo(BT boundValue) {
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
            fun = fieldConstraintBuilder.evaluateToFalseFunction;
        } else {
            fun = fieldConstraintBuilder.isNotNullConstraintViolatedFunction;
        }
        return fun;
    }

    private Function<CT, Boolean> createBoundValidationFunction() {
        Function<CT, Boolean> fun;
        if(boundValue == null) {
            fun = fieldConstraintBuilder.evaluateToFalseFunction;
        } else {
            fun = objectToValidate -> {
                boolean isViolated;
                FT fieldToValidate = fieldConstraintBuilder.fieldGetter.apply(objectToValidate);
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
        return fieldConstraintBuilder.notNullFailMessageFunction;
    }

    private Function<CT, String> createBoundFailMessageFunction() {
        return objectToValidate -> String.format(
                "Validation failed on object %s: Field target value must be %s %s but was %s.",
                objectToValidate.getClass().getSimpleName(),
                fieldConstraintBuilder.fieldGetter.andThen(targetGetter).apply(objectToValidate),
                boundType.getBoundAssertionString(),
                boundValue
        );
    }

}
