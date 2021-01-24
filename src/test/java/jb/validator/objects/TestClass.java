package jb.validator.objects;

import java.util.Collection;
import java.util.List;

public class TestClass {
    private String id;
    private String someString;
    private List<Integer> someNumbers;
    private TestClass2 testClass2;
    private Collection<TestClass2> testClass2Collection;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSomeString() {
        return someString;
    }

    public void setSomeString(String someString) {
        this.someString = someString;
    }

    public List<Integer> getSomeNumbers() {
        return someNumbers;
    }

    public void setSomeNumbers(List<Integer> someNumbers) {
        this.someNumbers = someNumbers;
    }

    public TestClass2 getTestClass2() {
        return testClass2;
    }

    public void setTestClass2(TestClass2 testClass2) {
        this.testClass2 = testClass2;
    }

    public Collection<TestClass2> getTestClass2Collection() {
        return testClass2Collection;
    }

    public void setTestClass2Collection(Collection<TestClass2> testClass2Collection) {
        this.testClass2Collection = testClass2Collection;
    }
}
