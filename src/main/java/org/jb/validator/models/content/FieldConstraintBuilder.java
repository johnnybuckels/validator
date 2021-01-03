package org.jb.validator.models.content;

import java.util.function.Function;

public class FieldConstraintBuilder<CT, FT>{

    protected final Function<CT, String> evaluateToEmptyStringFunction = objectToValidate -> "";
    protected final Function<CT, String> notNullFailMessageFunction = objectToValidate ->
            String.format("Validation failed on object %s: Field value was null.",
                    objectToValidate.getClass().getSimpleName()
            );

    protected final Function<CT, Boolean> evaluateToFalseFunction = objectToValidate -> false;
    protected final Function<CT, Boolean> isNotNullConstraintViolatedFunction;

    protected Function<CT, FT> fieldGetter;

    protected FieldConstraintBuilder(Function<CT, FT> fieldGetter) {
        this.fieldGetter = fieldGetter;
        this.isNotNullConstraintViolatedFunction = objectToValidate -> fieldGetter.apply(objectToValidate) == null;
    }

    public <X extends Comparable<X>> FieldConstraintBuilderFinal<CT, FT, X> suchThat(Function<FT, X> targetGetter){
        return new FieldConstraintBuilderFinal<>(this, targetGetter);
    }

    public Constraint<CT> notNullConstraint(String constraintName){
        return new Constraint<>(constraintName,
                evaluateToFalseFunction,
                isNotNullConstraintViolatedFunction,
                evaluateToEmptyStringFunction, notNullFailMessageFunction
        );
    }

}
