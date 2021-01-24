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
                Constraint.forNotNullField(TestClass::getId).addName("idNotNull"),
                Constraint.forNotNullField(TestClass::getSomeNumbers).addName("someNumbersNotNull"),
                Constraint.forNotNullField(TestClass::getSomeString).addName("someStringNotNull")
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
		List<Integer> set = List.of(1, 2, -10, 99, 11, 22);
		objectToValidate.setId("notNullId12323");
		objectToValidate.setSomeString("jfghjkhfdgj");
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