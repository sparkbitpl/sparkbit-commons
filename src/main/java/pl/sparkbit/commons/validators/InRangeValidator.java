package pl.sparkbit.commons.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InRangeValidator implements ConstraintValidator<InRange, Double> {

    private double min;
    private double max;

    @Override
    public void initialize(InRange constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value == null || (value >= min && value <= max);
    }
}
