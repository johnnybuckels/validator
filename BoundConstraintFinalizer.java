package jb.validator.models;

/**
 * Defines the functions used to create a bound-constraint.
 * @param <CT> type of the object to validate
 * @param <BT> type of the bound value
 */
public interface BoundConstraintFinalizer<CT, BT> {
    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is greater than or equal to the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is greater than or equal to the given bound value.
     */
    Constraint<CT> isLessThan(BT boundValue);

    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is greater than the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is greater than the given bound value.
     */
    Constraint<CT> isLessOrEqualThan(BT boundValue);

    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is less than or equal to the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is less than or equal to the given bound value.
     */
    Constraint<CT> isGreaterThan(BT boundValue);

    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is less than the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is less than the given bound value.
     */
    Constraint<CT> isGreaterOrEqualThan(BT boundValue);

    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is unequal to the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is unequal to the given bound value.
     */
    Constraint<CT> isEqualTo(BT boundValue);


    /**
     * Final step of the constraint building process. Creates a constraint that fails, if the earlier defined target value
     * is equal to the given boundValue.
     * @param boundValue value of this constraints bound.
     * @return a constraint that fails if the earlier defined target value is equal to the given bound value.
     */
    Constraint<CT> isNot(BT boundValue);
}
