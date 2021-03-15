package jb.validator.models;

import jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class ConstraintBuilderFinalField<CT, FT, BT extends Comparable<BT>> implements BoundConstraintFinalizer<CT, BT>{

    // --- internal fields for construction
    protected Function<FT, BT> targetGetter;
    protected ConstraintBuilderField<CT, FT> constraintBuilderField;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;

    protected ConstraintBuilderFinalField(ConstraintBuilderField<CT, FT> constraintBuilderField, Function<FT, BT> targetGetter) {
        this.constraintBuilderField = constraintBuilderField;
        this.targetGetter = targetGetter;
    }

    // ----- appending

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

    public Constraint<CT> isNot(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.NOT;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    // --------- private functions

    private Function<CT, Boolean> createBoundValidationFunction() {
        return objectToValidate -> {
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

    private Function<CT, String> createBoundFailMessageFunction() {
        return objectToValidate -> String.format(
                "[object: %s, field value: %s]: Target value must be %s %s but was %s",
                objectToValidate.getClass().getSimpleName(),
                constraintBuilderField.fieldGetter.apply(objectToValidate),
                boundType.getBoundAssertionString(),
                boundValue,
                constraintBuilderField.fieldGetter.andThen(targetGetter).apply(objectToValidate)
        );
    }

}
