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
                Constraint.forNotNullField(TestClass::getId).addName("idNotNull"),
                Constraint.forNotNullField(TestClass::getSomeNumbers).addName("someNumbersNotNull"),
                Constraint.forNotNullField(TestClass::getSomeString).addName("someStringNotNull"),
                Constraint.forField(TestClass::getId).suchThatTarget(String::length).isLessOrEqualThan(10).addName("idLength"),
                Constraint.forField(TestClass::getSomeNumbers).suchThatTarget(Collection::size).isGreaterOrEqualThan(0).addName("someNumbersNotEmpty"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatTarget(x -> x).isGreaterOrEqualThan(0).addName("noNegativeNumber"),
                Constraint.forField(TestClass::getSomeString).assertAbsenceUsing(repoDummy::findById).addName("idNotPresent"),
                Constraint.forField(TestClass::getTestClass2).assertNotThrowingUsing(testValidator2::validate).addName("validateTestClass2"),
                Constraint.forItemsInCollection(TestClass::getTestClass2Collection).assertNotThrowingUsing(testValidator2::validate).addName("validator2ForCollectionItems")
                );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return TestException.class;
    }

}
