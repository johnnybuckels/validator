package jb.validator.validators;

import jb.validator.models.AbstractValidator;
import jb.validator.exceptions.TestException;
import jb.validator.objects.RepoDummy;
import jb.validator.models.Constraint;
import jb.validator.objects.TestClass;

import java.net.BindException;
import java.util.Collection;
import java.util.List;

public class TestValidator extends AbstractValidator<TestClass> {

    RepoDummy repoDummy = new RepoDummy();
    TestValidator2 testValidator2 = new TestValidator2();

    private void throwingFunction(String s) throws BindException {
        throw new BindException();
    }

    private boolean customValidationFunction(TestClass classToTest) {
        return true;
    }

    @Override
    public Collection<Constraint<TestClass>> constraintSupplier() {
        return List.of(
                Constraint.forField(TestClass::getId).notNull().addName("idNotNull"),
                Constraint.forField(TestClass::getSomeNumbers).notNull().addName("someNumbersNotNull"),
                Constraint.forField(TestClass::getId).suchThatTarget(String::length).isLessOrEqualThan(10).addName("idLength"),
                Constraint.forField(TestClass::getSomeNumbers).suchThatTarget(Collection::size).isGreaterOrEqualThan(0).addName("someNumbersNotEmpty"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatEveryTarget(x -> x).isGreaterOrEqualThan(0).addName("noNegativeNumber"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatEveryTarget(x -> x).isNot(0).addName("noZeros"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers).suchThatOneTarget(x -> x).isGreaterOrEqualThan(100).addName("oneValueHigh"),
                Constraint.forField(TestClass::getSomeString).notNull().addName("someStringNotNull"),
                Constraint.forField(TestClass::getSomeString).absentUsing(repoDummy::findById).addName("idNotPresent"),
                Constraint.forField(TestClass::getSomeString).suchThatTarget(x -> x).isNot("ForbiddenValue").addName("notForbiddenValue"),
                Constraint.forField(TestClass::getTestClass2).noThrowsUsing(testValidator2::validate).addName("validateTestClass2"),
                Constraint.forItemsInCollection(TestClass::getTestClass2Collection).noThrowsUsing(testValidator2::validate).addName("validator2ForCollectionItems"),
                Constraint.forField(TestClass::getId).noThrowsUsing(this::throwingFunction),
                Constraint.forCustomValidation(this::customValidationFunction, "My custom validation has failed").addName("customValidation")
        );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return TestException.class;
    }

}
