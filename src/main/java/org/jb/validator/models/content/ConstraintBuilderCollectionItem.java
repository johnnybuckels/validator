package org.jb.validator.models.content;

import java.util.Collection;
import java.util.function.Function;

public class ConstraintBuilderCollectionItem<CT, IT, FT extends Collection<IT>> extends ConstraintBuilder<CT, FT> {


    protected ConstraintBuilderCollectionItem(Function<CT, FT> fieldGetter) {
        super(fieldGetter);
    }

    public <X extends Comparable<X>> ConstraintBuilderFinalCollectionItem<CT, IT, FT, X> suchThat(Function<IT, X> targetGetter){
        return new ConstraintBuilderFinalCollectionItem<>(this, targetGetter);
    }

}
