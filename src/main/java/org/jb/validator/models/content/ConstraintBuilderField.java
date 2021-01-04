package org.jb.validator.models.content;

import java.util.function.Function;

public class ConstraintBuilderField<CT, FT> extends ConstraintBuilder<CT, FT>{


    protected ConstraintBuilderField(Function<CT, FT> fieldGetter) {
        super(fieldGetter);
    }

    public <X extends Comparable<X>> ConstraintBuilderFinalField<CT, FT, X> suchThat(Function<FT, X> targetGetter){
        return new ConstraintBuilderFinalField<>(this, targetGetter);
    }

}
