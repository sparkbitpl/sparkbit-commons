package pl.sparkbit.commons.validators;

import jakarta.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = InRangeValidator.class)
public @interface InRange {

    double min();

    double max();

    String message() default "invalid value";

    Class[] groups() default {};

    Class[] payload() default {};
}
