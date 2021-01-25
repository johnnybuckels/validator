package jb.validator;

import jb.validator.exceptions.WrapperException;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {
    @Override
    default void accept(T t) {
        try{
            consumeWithThrows(t);
        } catch (Throwable e) {
            throw new WrapperException(e);
        }
    }

    void consumeWithThrows(T t) throws Throwable;

}
