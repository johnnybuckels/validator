package org.jb.validator.annotations;

public @interface AutoValidated {
    Class<?>[] using() default {};
}
