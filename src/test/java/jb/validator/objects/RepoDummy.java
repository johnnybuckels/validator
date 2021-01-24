package jb.validator.objects;


import java.util.Optional;

public class RepoDummy {

    public Optional<TestClass> findById(String id) {
        return Optional.of(new TestClass());
    }

}
