# Validator Project

This java project provides an ensemble of classes suited for validating simple java objects with informative evaluation messages. 

The central element is the abstract validator class. Once constraints are registered to a validator, it can be used to validate a java object throwing an exception and returning an informative error message for every violated constraint.

## Quick usage

Assume some class that is subject to validation.

```java
public class TestClass {
    private String id;
    private String someString;
    private List<Integer> someNumbers;
    
    // getters, setters...
}
```

Define a validator for this class.

```java
public class TestValidator extends AbstractValidator<TestClass> {

    @Override
    public Collection<Constraint<TestClass>> constraintSupplier() {
        return List.of(
                Constraint.forField(TestClass::getId).notNull().addName("idNotNull"),
                Constraint.forField(TestClass::getId)
                    .suchThatTarget(String::length).isLessOrEqualThan(10).addName("idLength"),
                Constraint.forItemsInCollection(TestClass::getSomeNumbers)
                    .suchThatOneTarget(x -> x).isGreaterOrEqualThan(0).addName("atLeastOneNonnegativeNumber")
        );
    }

    @Override
    public Class<? extends Throwable> exceptionSupplier() {
        return Exception.class;
    }
}
```

Use the validator.

```java
class Testing {
    @Test
    void testingValidator() {
        TestClass objectToValidate = new TestClass();
        List<Integer> set = List.of(1, 2, 10, 99, 11, 22);
        objectToValidate.setId("notNullId12323");
        objectToValidate.setSomeString("abcdefghjklmnop");
        objectToValidate.setSomeNumbers(set);
        TestValidator testValidator = new TestValidator();
        try { 
            testValidator.validate(objectToValidate);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
```

## Functionalities

---

The following chapters lists all possibilities to easily construct almost any constraint.

In general, constructing a validator for some Object-Type is achieved by declaring a Class extending this package's ``AbstractValidator``. It is necessary to implement two methods :

- ``Collection<Constraint<TestClass>> constraintSupplier()``
    returns a collection of ``Constraint`` objects. There are various possibilities to easily form constraints matching almost any conditions. A more detailed explanation of defining such constraints is described in the following chapter.
- ``Class<? extends Throwable> exceptionSupplier()``
  returns the exception class type that is thrown whenever a validation failed. An exception of the given type will be wrapped in a ``ValidatorException`` along with an informative error message listing all violated constraints. The return value ``null`` is also possible. In that case, no custom exception type will be put as the cause of the ``ValidatorException``.

Calling the inherited method ``validate`` on any object of the declared class type starts the validation of all supplied constraints. If any constraint is violated a ``ValidatorException`` with an appropriate error message is thrown.

In the following chapter we present various ways to construct ``Constraint``-Objects. In general,the class ``Constraint`` comes with various static methods that initialize different Builder-Objects leading through the process of defining a suiting constraint.

### Not-Null constraint

---

Creating a not null constraint consists of two steps: picking the field of interest and declaring the Not-Null constraint.

````java
Constraint.forField(MyObject::getMyField()).notNull()
````

Not-Null constraints are considered as individual constraints just like any other constraint.

### Field constraint

---

Creating a constraint for some field can have multiple purposes that are covered by slightly different approaches

### Field constraint for content validation

Creating a content constraint, follows the steps of picking the field of interest, defining a derived target value from that field and finally stating a bound on that target value. This way one can easily verify the length of some String-field. In general this approach is suited for all target values implementing ``java.lang.Comparable``.

Constraint about String length and value:

````java
Constraint<MyObject> c1 = Constraint.forField(MyObject::getMyStringField)
    .suchThatTarget(String::length).isLessOrEqualThan(10);

Constraint<MyObject> c2 = Constraint.forField(MyObject::getMyStringField)
    .suchThatTarget(x -> x).isNot("ForbiddenValue);

````

Constraint about a plain BigDecimal value:

````java
Constraint<MyObject> c1 = Constraint.forField(MyObject::getMyBigDecimalField)
    .suchThatTarget(x -> x).isLessOrEqualThan(BigDecimal.valueOf(1234L, 2));

Constraint<MyObject> c2 = Constraint.forField(MyObject::getMyBigDecimalField)
    .suchThatTarget(BigDecimal::scale).isEqualTo(2);
````

Constraint about the size of some Collection:

````java
Constraint.forField(MyObject::getMyCollectionField)
    .suchThatEveryTarget(Collection::size).isEqualTo(3);
````

The respective Target-Getter function may be more complex if one desires.

### Field constraint for semantic validation

When working with objects representing actual database entries one might verify the existence or absence of some objects that need to be fetched from some source in order to verify the integrity of the object under validation.

For this purpose, we use the static Builder-Methods for supplying another service's method - for example a query of some JpaRepository or some validate method of another validator. In order to make use of such a service, one needs to supply the respective service-providing object to the validator class.

Constraints ensuring the absence and existence of some objects:

````java
MyService myService = new MyService();

Constraint<MyObject> myConstraint1 = Constraint.forField(MyObject::getSomeId)
    .absentUsing(myService::findById);

Constraint<MyObject> myConstraint2 = Constraint.forField(MyObject::getSomeOtherId)
    .existingUsing(myService::findByOtherId);
````

Constraint for validating a nested object with another validator:

````java
MyValidator<SomeObject> myValidator = new MyValidator<>();

Constraint<MyObject> myConstraint = Constraint.forField(MyObject::getMyNestedObject)
    .noThrowsUsing(myValidator::validate);
````

Constraint for validating some field using a custom method:

````java
private void myCustomThrowingConsumer(MyFieldType myField) {
    throw new Exception("I throw");
}

Constraint<MyObject> myConstraint = Constraint.forField(MyObject::getMyNestedObject)
    .noThrowsUsing(this::myCustomThrowingConsumer);
````

### Validating collections

All of the previously mentioned possibilities also apply to the validation of items in a collection. Every field implementing ``java.lang.Collection`` may be validated in the following way.
Additionally, one can choose between the two options of ensuring that a constraint holds for every or for at least one item.

Constraint for validating contents of a Collection:

````java
Constraint<MyObject> c1 = Constraint.forItemsInCollection(MyObject::getMyListOfString)
    .suchThatEveryTarget(String::length).isEqualTo(1);

Constraint<MyObject> c2 = Constraint.forItemsInCollection(MyObject::getMyListOfString)
    .suchThatOneTarget(x -> x).isEqualTo("ensuredStringValue");
````

Constraint for validating nested Objects in a Collection:

````java
MyValidator<SomeObject> myValidator = new MyValidator<>();

Constraint<MyObject> constraint = Constraint.forItemsInCollection(MyObject::getMySomeObjectList)
    .noThrowsUsing(myValidator::validate)
````

### Adding constraint names

In order to link error messages to a previously defined constraint, it is helpful to add informative names to all constraints. This name becomes part of the resulting error message. If a constraint is not named, its name is set to "ANONYMOUS".

Adding names to constraints:

````java
Constraint<MyObject> constraint1 = Constraint.forField(MyObject::getMyField)
    .notNull()
    .addName("myFieldNotNull");

Constraint<MyObject> constraint2 = Constraint.forField(MyObject::getMyField)
    .suchThatTarget(BigDecimal::precision).isSmallerThan(12)
    .addName("myFieldPrecisionSmaller12");

````

### Creating custom constraints

If the previous options do not suffice, one can also define custom constraints by using the static method ``Constraint.forCustomValidation``. This way, we only have to supply our own method to detect weather an object satisfies our constraint or not. Additionally, we can define a method that constructs an error message suiting the object under validation.

Defining a custom constraint:

````java
private boolean isMyConstraintViolated(MyObject myObject) {
    return myObject.getMyIntegerField() >= myObject.myOtherIntegerField();
}

private String myErrorMessageFunction(MyObject myObject) {
    return String.format(
        "myIntegerField field was %s and needed to be smaller than myOtherIntegerField which was %s",
        myObject.getMyIntegerField(), myObject.getMyOtherIntegerField()
    );
}

Constraint<MyObject> constraint = Constraint.forCustomValidation(
    this::isMyConstraintViolated, this::myErrorMessageFunction
    ).addName("myIntegerFieldSmallerThanMyOtherIntegerField");
````

There are two more variants of ``forCustomValidation``, one with a constant error string message and one with a generic error message.