package jb.validator.validators;

import jb.validator.models.AbstractValidator;
import jb.validator.models.Constraint;
import jb.validator.objects.TestClass2;

import java.util.Collection;
import java.util.List;

public class TestValidator2 extends AbstractValidator<TestClass2> {

    @Override
    public Collection<Constraint<TestClass2>> constraintSupplier() {
        return List.of(
                Constraint.forNotNullField(TestClass2::getId2).addName("id2NotNull")
                );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return Exception.class;
    }

}
