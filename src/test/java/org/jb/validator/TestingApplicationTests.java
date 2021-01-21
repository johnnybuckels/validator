package org.jb.validator;

import org.jb.validator.exceptions.ValidatorException;
import org.jb.validator.objects.RepoDummy;
import org.jb.validator.objects.TestClass;
import org.jb.validator.objects.TestClass2;
import org.jb.validator.validators.TestValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

class TestingApplicationTests {

	@Test
	void repoTest() {
		BiFunction<RepoDummy, String, Optional<TestClass>> fun = RepoDummy::findById;
		System.out.println(fun.apply(new RepoDummy(), "idString"));
	}

	@Test
	void genericTest() throws ValidatorException {
		TestClass objectToValidate = new TestClass();
		List<Integer> set = List.of(1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99, 1, 2, -10, 99);
		objectToValidate.setId("notNullId12323");
		objectToValidate.setSomeString("jfghjkhfdgj");
		objectToValidate.setSomeNumbers(set);
		objectToValidate.setTestClass2(new TestClass2());
		objectToValidate.getTestClass2().setId2(null);

		TestValidator testValidator = new TestValidator();
		testValidator.validate(objectToValidate);
	}

}
