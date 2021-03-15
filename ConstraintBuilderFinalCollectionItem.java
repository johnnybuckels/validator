package jb.validator.models;

import jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.Collection;
import java.util.function.Function;

/**
 * Builder used for creating a constraint concerning a field and some from that field derived target value.
 */
public class ConstraintBuilderFinalCollectionItem<CT, IT, FT extends Collection<IT>, BT extends Comparable<BT>> implements BoundConstraintFinalizer<CT, BT>{

    // --- internal fields for construction
    protected Function<IT, BT> targetGetter;
    protected ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem;
    protected ContentConstraintBoundTypeEnum boundType;
    protected BT boundValue;
    protected boolean anyViolationWillFail;

    protected ConstraintBuilderFinalCollectionItem(ConstraintBuilderCollectionItem<CT, IT, FT> constraintBuilderCollectionItem, Function<IT, BT> targetGetter, boolean anyViolationWillFail) {
        this.constraintBuilderCollectionItem = constraintBuilderCollectionItem;
        this.targetGetter = targetGetter;
        this.anyViolationWillFail = anyViolationWillFail;
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

    public Constraint<CT> isNot(BT boundValue) {
        this.boundValue = boundValue;
        this.boundType = ContentConstraintBoundTypeEnum.NOT;
        return new Constraint<>(createBoundValidationFunction(), createBoundFailMessageFunction());
    }

    // ----- building

    private Function<CT, Boolean> createBoundValidationFunction() {
        return objectToValidate -> {
            boolean isViolated;
            FT fieldToValidate = constraintBuilderCollectionItem.fieldGetter.apply(objectToValidate);
            if(fieldToValidate != null) {
                if(anyViolationWillFail) {
                    isViolated = fieldToValidate.stream()
                            .anyMatch(collectionItem ->
                                    boundType.getIsBoundBrokenFunction().apply(
                                            targetGetter.apply(collectionItem).compareTo(boundValue)
                                    )
                            );
                } else {
                    isViolated = fieldToValidate.stream()
                            .allMatch(collectionItem ->
                                    boundType.getIsBoundBrokenFunction().apply(
                                            targetGetter.apply(collectionItem).compareTo(boundValue)
                                    )
                            );
                }
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
            String oneOrEveryString;
            if(anyViolationWillFail) {
                oneOrEveryString = "every";
            } else {
                oneOrEveryString = "at least one";
            }
            return String.format(
                    "[object: %s, collection: %s]: The target value of %s item must be %s %s",
                    objectToValidate.getClass().getSimpleName(),
                    collectionString,
                    oneOrEveryString,
                    boundType.getBoundAssertionString(),
                    boundValue
            );
        };
    }

}
