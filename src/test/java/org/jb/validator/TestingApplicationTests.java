package org.jb.validator;

import org.jb.validator.models.content.Constraint;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.function.Function;

class TestingApplicationTests {

	@Test
	void sandboxText() {
		Function<String, Integer> fun = String::length;
		System.out.println(fun.getClass().getCanonicalName());
	}

	@Test
	void genericTest() {
		Constraint<TestClass> cls = Constraint.forField(TestClass::getSomeString)
				.suchThat(String::length).isLessOrEqualThan(10).isNotNullable().buildWithName("someStringConstraint");
		TestClass objectToValidate = new TestClass();
		String someString = "someTooLongString";
		Set<Integer> set = Set.of(1, 2, 10, 99);
		objectToValidate.setSomeString(null);
		objectToValidate.setSomeNumbers(set);

		System.out.println(cls.isConstraintViolated(objectToValidate));
	}

}
