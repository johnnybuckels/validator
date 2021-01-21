package org.jb.validator.validators;

import org.jb.validator.exceptions.TestException;
import org.jb.validator.objects.RepoDummy;
import org.jb.validator.models.AbstractValidator;
import org.jb.validator.models.Constraint;
import org.jb.validator.objects.TestClass;

import java.util.Collection;
import java.util.List;

public class TestValidator extends AbstractValidator<TestClass> {

    RepoDummy repoDummy = new RepoDummy();
    TestValidator2 testValidator2 = new TestValidator2();

    @Override
    public Collection<Constraint<TestClass>> constraintSupplier() {
        return List.of(
                Constraint.forNotNullField(TestClass::getId).setConstraintName("idNotNull"),
                Constraint.forNotNullField(TestClass::getSomeNumbers).setConstraintName("someNumbersNotNull"),
                Constraint.forNotNullField(TestClass::getSomeString).setConstraintName("someStringNotNull"),
                Constraint.forField(TestClass::getId).suchThatTarget(String::length).isLessOrEqualThan(10).setConstraintName("idLength"),
                Constraint.forField(TestClass::getSomeNumbers).suchThatTarget(Collection::size).isGreaterOrEqualThan(0).setConstraintName("someNumbersNotEmpty"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatTarget(x -> x).isGreaterOrEqualThan(0).setConstraintName("noNegativeNumber"),
                Constraint.forField(TestClass::getSomeString).isAbsentUsing(repoDummy::findById).setConstraintName("idNotPresent"),
                Constraint.forField(TestClass::getTestClass2).doesNotThrowUsing(testValidator2::validate).setConstraintName("validateTestClass2")
                );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return TestException.class;
    }
}
