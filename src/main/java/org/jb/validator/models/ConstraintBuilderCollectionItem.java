package org.jb.validator.models;

import java.util.Collection;
import java.util.function.Function;

public class ConstraintBuilderCollectionItem<CT, IT, FT extends Collection<IT>> {

    protected Function<CT, FT> fieldGetter;

    protected ConstraintBuilderCollectionItem(Function<CT, FT> fieldGetter) {
        this.fieldGetter = fieldGetter;
    }

    public <X extends Comparable<X>> ConstraintBuilderFinalCollectionItem<CT, IT, FT, X> suchThatTarget(Function<IT, X> targetGetter){
        return new ConstraintBuilderFinalCollectionItem<>(this, targetGetter);
    }

}
