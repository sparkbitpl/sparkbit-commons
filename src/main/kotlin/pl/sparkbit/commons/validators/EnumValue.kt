package pl.sparkbit.commons.validators

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.TYPE)
@Constraint(validatedBy = [EnumValidatorImpl::class])
annotation class EnumValue(
    val message: String = "Invalid enum value",
    val value: KClass<out Enum<*>>,
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = []
)

class EnumValidatorImpl : ConstraintValidator<EnumValue, String> {

    private lateinit var validNames: List<String>

    override fun initialize(constraintAnnotation: EnumValue) {
        this.validNames = lookupValidNames(constraintAnnotation)
    }

    override fun isValid(obj: String?, context: ConstraintValidatorContext?): Boolean {
        return obj == null || obj in validNames
    }

    companion object {
        fun lookupValidNames(ann: EnumValue): List<String> {
            return ann.value.java.enumConstants.map {
                it.name
            }
        }
    }
}
