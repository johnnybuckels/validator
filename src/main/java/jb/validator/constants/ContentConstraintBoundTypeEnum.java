package jb.validator.constants;

import java.util.function.Function;

/**
 * Type of the constraint "sign".
 * For example:
 *  "assert that MAXIMUM length of string s is 10"
 *  translates to something like
 *  assert s.length() < 10;
 * The enum constant holds information about the "sign" and the respective Function, evaluating
 * the expression. The evaluation states if the bound is violated. Hence, evaluating to "true" means, that the bound
 * is broken.
 */
public enum ContentConstraintBoundTypeEnum {
    MIN("greater than or equal to", x -> x <= 0),
    MININCL("greater than or equal to", x -> x < 0),
    MAX("smaller than", x -> x >= 0),
    MAXINCL("smaller than or equal to", x -> x > 0),
    EXACT("equal to", x -> x != 0);

    private final String boundAssertionString;
    private final Function<Integer, Boolean> isBoundBrokenFunction;

    ContentConstraintBoundTypeEnum(String boundAssertionString, Function<Integer, Boolean> isBoundBrokenFunction) {
        this.boundAssertionString = boundAssertionString;
        this.isBoundBrokenFunction = isBoundBrokenFunction;
    }

    public String getBoundAssertionString() {
        return boundAssertionString;
    }

    public Function<Integer, Boolean> getIsBoundBrokenFunction() {
        return isBoundBrokenFunction;
    }
}
