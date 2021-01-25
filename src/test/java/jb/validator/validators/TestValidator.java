package jb.validator.validators;

import jb.validator.models.AbstractValidator;
import jb.validator.exceptions.TestException;
import jb.validator.objects.RepoDummy;
import jb.validator.models.Constraint;
import jb.validator.objects.TestClass;

import java.util.Collection;
import java.util.List;

public class TestValidator extends AbstractValidator<TestClass> {

    RepoDummy repoDummy = new RepoDummy();
    TestValidator2 testValidator2 = new TestValidator2();

    @Override
    public Collection<Constraint<TestClass>> constraintSupplier() {
        return List.of(
                Constraint.forField(TestClass::getId).notNull().addName("idNotNull"),
                Constraint.forField(TestClass::getSomeNumbers).notNull().addName("someNumbersNotNull"),
                Constraint.forField(TestClass::getSomeString).notNull().addName("someStringNotNull"),
                Constraint.forField(TestClass::getId).suchThatTarget(String::length).isLessOrEqualThan(10).addName("idLength"),
                Constraint.forField(TestClass::getSomeNumbers).suchThatTarget(Collection::size).isGreaterOrEqualThan(0).addName("someNumbersNotEmpty"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatTarget(x -> x).isGreaterOrEqualThan(0).addName("noNegativeNumber"),
                Constraint.forField(TestClass::getSomeString).absentUsing(repoDummy::findById).addName("idNotPresent"),
                Constraint.forField(TestClass::getTestClass2).noThrowsUsing(testValidator2::validate).addName("validateTestClass2"),
                Constraint.forItemsInCollection(TestClass::getTestClass2Collection).noThrowsUsing(testValidator2::validate).addName("validator2ForCollectionItems")
                );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return TestException.class;
    }

}
