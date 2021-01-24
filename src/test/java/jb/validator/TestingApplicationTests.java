package jb.validator;

import jb.validator.exceptions.ValidatorException;
import jb.validator.objects.RepoDummy;
import jb.validator.objects.TestClass;
import jb.validator.objects.TestClass2;
import jb.validator.validators.TestValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
		TestClass2 testClass2 = new TestClass2();
		testClass2.setId2(null);
		objectToValidate.setTestClass2(testClass2);
		objectToValidate.setTestClass2Collection(List.of(testClass2, testClass2));
		TestValidator testValidator = new TestValidator();
		assertThrows(ValidatorException.class, () -> testValidator.validate(objectToValidate));
	}

	@Test
	void testingValidator() throws ValidatorException {
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
