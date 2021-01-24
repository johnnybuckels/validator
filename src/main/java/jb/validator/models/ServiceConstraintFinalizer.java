package jb.validator.models;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Defines the functions used to create a constraint using other objects methods whose return values are subject to the
 * validation process.
 * @param <CT> type of the object to validate
 * @param <FT> type of the field under validation
 */
public interface ServiceConstraintFinalizer<CT, FT> {

    /**
     * Creates a constraint evaluating if the given service function applied to the earlier defined field value results
     * in an empty optional or not. This method might be used to evaluate if some id or object exists in a database.
     * In this case one could pass myRepositoryInstance::findByMyFieldValue as serviceFunction.
     * @param serviceFunction function returning an optional of some type when applied to the earlier defined field value.
     * @param <X> type of the optionals content.
     * @return a constraint that will fail if the returned optional empty.
     */
    <X> Constraint<CT> assertExistenceUsing(Function<FT, Optional<X>> serviceFunction);

    String existenceFailMessageDefault = "Requested data object does not exist";

    /**
     * Creates a constraint evaluating if the given service function applied to the earlier defined field value results
     * in an empty optional or not. This method might be used to evaluate if some id or object exists in a database.
     * In this case one could pass myRepositoryInstance::findByMyFieldValue as serviceFunction.
     * @param serviceFunction function returning an optional of some type when applied to the earlier defined field value.
     * @param <X> type of the optionals content.
     * @return a constraint that will fail if the returned optional is not empty.
     */
    <X> Constraint<CT> assertAbsenceUsing(Function<FT, Optional<X>> serviceFunction);

    String absenceFailMessageDefault = "Requested data object already exists";


    /**
     * Creates a constraint evaluating if the given service function applied to the earlier defined field value results
     * in an empty collection or not. This method might be used to evaluate if some id or object exists in a database.
     * In this case one could pass myRepositoryInstance::findByMyFieldValue as serviceFunction.
     * @param serviceFunction function returning a collection of some type when applied to the earlier defined field value.
     * @param <X> type of the collections content.
     * @return a constraint that will fail if the returned collection is empty.
     */
    <X> Constraint<CT> assertEmptyUsing(Function<FT, Collection<X>> serviceFunction);

    String emptyCollectionFailMessageDefault = "Requested data object collection is empty";


    /**
     * Creates a constraint evaluating if the given service function applied to the earlier defined field value results
     * in an empty collection or not. This method might be used to evaluate if some id or object exists in a database.
     * In this case one could pass myRepositoryInstance::findByMyFieldValue as serviceFunction.
     * @param serviceFunction function returning a collection of some type when applied to the earlier defined field value.
     * @param <X> type of the collections content.
     * @return a constraint that will fail if the returned collection is not empty.
     */
    <X> Constraint<CT> assertNotEmptyUsing(Function<FT, Collection<X>> serviceFunction);

    String nonEmptyCollectionFailMessageDefault = "Requested data object collection is not empty";


    // ----- nested validator

    /**
     * Creates a constraint evaluating if applying the given consumer function to the earlier defined field value
     * throws any exception. Ths method might be used for applying another validator to the respective field.
     * In this case one could pass myValidatorInstance::validate as fieldConsumer.
     * @param fieldConsumer void function consuming the earlier defined field value.
     * @return a constraint that will fail if the field consumer throws an exception.
     */
    Constraint<CT> assertNotThrowingUsing(Consumer<FT> fieldConsumer);

}
