package org.jb.validator;

import org.jb.validator.constants.ContentConstraintBoundTypeEnum;

import java.util.Collection;
import java.util.function.Function;

public class GenericTestClass <CT> {

    private boolean nullable = true;
    private Function<CT, Boolean> isConstraintSatisfiedFunction;

    public static <X, Y, Z extends Comparable<Z>> GenericTestClass<X> create(
            Function<X, Y> fieldGetter,
            Function<Y, Z> targetGetter,
            ContentConstraintBoundTypeEnum boundType,
            Z boundValue,
            boolean nullable
    ) {
        return new GenericTestClass<>(fieldGetter, targetGetter, boundType, boundValue, nullable);
    }

    public <Y, Z extends Comparable<Z>> GenericTestClass(Function<CT, Y> fieldGetter,
                                                           Function<Y, Z> targetGetter,
                                                           ContentConstraintBoundTypeEnum boundType,
                                                           Z boundValue,
                                                           boolean nullable) {
        this.nullable = nullable;

        isConstraintSatisfiedFunction = objectToValidate -> boundType.getIsBoundBrokenFunction().apply(
                        fieldGetter.andThen(targetGetter).apply(objectToValidate).compareTo(boundValue)
        );
    }

    public Boolean isConstraintSatisfied(CT objectToValidate) {
        return isConstraintSatisfiedFunction.apply(objectToValidate);
    }
}
