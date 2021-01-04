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
		Constraint<TestClass> constraint = Constraint.forField(TestClass::getSomeString)
				.suchThat(String::isBlank).isEqualTo(true).isNotNullable().buildWithName("someStringConstraint");
		Constraint<TestClass> constraint2 = Constraint.forItemsInCollection(TestClass::getSomeNumbers)
				.suchThat(Integer::signum).isEqualTo(1).buildWithName("signumEqualToOne");

		TestClass objectToValidate = new TestClass();
		String someString = "jfghjkhfdgj";
		Set<Integer> set = Set.of(1, 2, -10, 99);
		objectToValidate.setSomeString(someString);
		objectToValidate.setSomeNumbers(set);
		constraint.validate(objectToValidate);
		System.out.println(constraint.validate(objectToValidate));
		System.out.println(constraint2.validate(objectToValidate));


	}

}
