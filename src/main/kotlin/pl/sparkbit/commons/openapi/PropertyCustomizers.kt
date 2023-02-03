package pl.sparkbit.commons.openapi

import com.fasterxml.jackson.databind.type.SimpleType
import com.google.common.collect.BoundType
import com.google.common.collect.Range
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.media.StringSchema
import mu.KotlinLogging
import pl.sparkbit.commons.validators.EnumValidatorImpl
import pl.sparkbit.commons.validators.EnumValue
import pl.sparkbit.commons.validators.InRange
import java.math.BigDecimal
import java.util.Objects
import javax.validation.Validation
import javax.validation.ValidatorFactory
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Digits
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Negative
import javax.validation.constraints.NegativeOrZero
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size
import javax.validation.metadata.BeanDescriptor
import javax.validation.metadata.ConstraintDescriptor
import javax.validation.metadata.ElementDescriptor

interface JavaBeansAwarePropertyCustomizer {
    fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*>
}

class EnumPropertyCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val enumConstraint = elementDescriptor.lookup<EnumValue>()
        if (enumConstraint != null) {
            property.enum = EnumValidatorImpl.lookupValidNames(enumConstraint.annotation)
        }
        return property
    }
}

class InRangeCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val inRangeConstraint = elementDescriptor.lookup<InRange>()
        if (inRangeConstraint != null) {
            val inRange = inRangeConstraint.annotation
            applyRange(property, Range.closed(inRange.min.toBigDecimal(), inRange.max.toBigDecimal()))
        }
        return property
    }
}

class DigitsCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val digits = elementDescriptor.lookup<Digits>()
        if (digits != null) {
            apply(property, digits.annotation)
        }
        return property
    }

    private fun apply(property: Schema<*>, digits: Digits) {
        property.multipleOf = BigDecimal.ONE.divide(BigDecimal.valueOf(10).pow(digits.fraction))
        val maxExclusive = BigDecimal.valueOf(10).pow(digits.integer)
        applyRange(property, Range.lessThan(maxExclusive))
    }
}

class NegativeCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val negative = elementDescriptor.lookup<Negative>()
        if (negative != null) {
            applyNegative(property)
        }
        return property
    }

    private fun applyNegative(property: Schema<*>) {
        applyRange(property, Range.lessThan(BigDecimal.ZERO))
    }
}

class NegativeOrZeroCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val negativeOrZero = elementDescriptor.lookup<NegativeOrZero>()
        if (negativeOrZero != null) {
            applyNegativeOrZero(property)
        }
        return property
    }

    private fun applyNegativeOrZero(property: Schema<*>) {
        applyRange(property, Range.atMost(BigDecimal.ZERO))
    }
}

class PositiveCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val positive = elementDescriptor.lookup<Positive>()
        if (positive != null) {
            applyPositive(property)
        }
        return property
    }

    private fun applyPositive(property: Schema<*>) {
        applyRange(property, Range.greaterThan(BigDecimal.ZERO))
    }
}

class PositiveOrZeroCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val positiveOrZero = elementDescriptor.lookup<PositiveOrZero>()
        if (positiveOrZero != null) {
            applyPositiveOrZero(property)
        }
        return property
    }

    private fun applyPositiveOrZero(property: Schema<*>) {
        applyRange(property, Range.atLeast(BigDecimal.ZERO))
    }
}

class PatternCustomizer : JavaBeansAwarePropertyCustomizer {
    private val log = KotlinLogging.logger {}
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val pattern = elementDescriptor.lookup<Pattern>()
        if (pattern != null) {
            if (!Objects.equals(property.pattern, pattern.annotation.regexp)) {
                log.warn { "Override pattern ${property.pattern} with ${pattern.annotation.regexp} for ${property.name}" }
            }
            property.pattern = pattern.annotation.regexp
        }
        return property
    }
}

class MinCustomizer : JavaBeansAwarePropertyCustomizer {

    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val min = elementDescriptor.lookup<Min>()
        if (min != null) {
            applyRange(property, Range.atLeast(BigDecimal.valueOf(min.annotation.value)))
        }
        return property
    }
}

class MaxCustomizer : JavaBeansAwarePropertyCustomizer {

    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val max = elementDescriptor.lookup<Max>()
        if (max != null) {
            applyRange(property, Range.atMost(BigDecimal.valueOf(max.annotation.value)))
        }
        return property
    }
}

class SizeCustomizer : JavaBeansAwarePropertyCustomizer {
    private val log = KotlinLogging.logger {}
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val size = elementDescriptor.lookup<Size>()
        if (size != null) {
            applySize(property, Range.closed(size.annotation.min, size.annotation.max))
        }
        return property
    }

    private fun applySize(property: Schema<*>, range: Range<Int>) {
        when (property) {
            is StringSchema -> {
                val existingRange = property.buildRange({ it.minLength }, { it.maxLength })
                val newRange = existingRange.intersection(range)
                property.minLength(newRange.lowerEndpoint())
                property.maxLength(newRange.upperEndpoint())
            }

            is ArraySchema -> {
                val existingRange = property.buildRange({ it.minItems }, { it.maxItems })
                val newRange = existingRange.intersection(range)
                property.minItems(newRange.lowerEndpoint())
                property.maxItems(newRange.upperEndpoint())
            }

            else -> {
                log.warn { "Cannot apply size constraint $range to schema ${property.javaClass.name} - type is unsupported" }
            }
        }
    }
}

class EmailCustomizer : JavaBeansAwarePropertyCustomizer {
    private val log = KotlinLogging.logger {}
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val email = elementDescriptor.lookup<Email>()
        if (email != null) {
            if (!Objects.equals(property.format, FORMAT_EMAIL)) {
                if (property.format != null) {
                    log.warn { "Overwrite format ${property.format} with $FORMAT_EMAIL for ${property.name}" }
                }
                property.format = FORMAT_EMAIL
            }
        }
        return property
    }

    companion object {
        const val FORMAT_EMAIL = "email"
    }
}

class RequiredFieldCustomizer : JavaBeansAwarePropertyCustomizer {
    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        // The same logic is used in default ModelConverter (io.swagger.v3.core.jackson.ModelResolver#applyBeanValidatorAnnotations)
        // We want it to be consistent so even if meaning of these annotations are different we will use as the same thing.
        val required = elementDescriptor.lookup<NotNull>() ?: elementDescriptor.lookup<NotEmpty>() ?: elementDescriptor.lookup<NotBlank>()
        if (required != null && parent != null) {
            parent.addRequiredItem(property.name)
        }
        return property
    }
}

class DecimalMinCustomizer : JavaBeansAwarePropertyCustomizer {

    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val decimalMin = elementDescriptor.lookup<DecimalMin>()
        if (decimalMin != null) {
            applyRange(property, Range.atLeast(BigDecimal(decimalMin.annotation.value)))
        }
        return property
    }
}

class DecimalMaxCustomizer : JavaBeansAwarePropertyCustomizer {

    override fun customize(property: Schema<*>, parent: Schema<*>?, elementDescriptor: ElementDescriptor): Schema<*> {
        val decimalMax = elementDescriptor.lookup<DecimalMax>()
        if (decimalMax != null) {
            applyRange(property, Range.atMost(BigDecimal(decimalMax.annotation.value)))
        }
        return property
    }
}

class BeansValidationModel(private val customizers: List<JavaBeansAwarePropertyCustomizer>) : ModelConverter {
    private val log = KotlinLogging.logger {}
    private var factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
    override fun resolve(type: AnnotatedType, context: ModelConverterContext, chain: MutableIterator<ModelConverter>): Schema<*>? {
        if (chain.hasNext()) {
            val resolvedSchema = chain.next().resolve(type, context, chain)
            if (type.parent == null) {
                val constraints = when (type.type) {
                    is SimpleType -> {
                        factory.validator.getConstraintsForClass((type.type as SimpleType).rawClass)
                    }

                    is Class<*> -> {
                        factory.validator.getConstraintsForClass(type.type as Class<*>)
                    }

                    else -> {
                        log.warn { "Unsupported ${type.type}" }
                        null
                    }
                }
                if (constraints != null) {
                    customize(resolvedSchema, null, constraints)
                }
            }
            return resolvedSchema
        }
        return null
    }

    private fun customize(schema: Schema<*>, parent: Schema<*>?, descriptor: ElementDescriptor) {
        customizers.forEach { it.customize(schema, parent, descriptor) }
        if (descriptor is BeanDescriptor) {
            schema.properties.forEach { (name, propSchema) ->
                val propDescriptor = descriptor.getConstraintsForProperty(name)
                if (propDescriptor != null) {
                    customize(propSchema, schema, propDescriptor)
                }
            }
        }
    }
}

private inline fun <reified T : Annotation> Iterable<ConstraintDescriptor<*>>.lookup(): ConstraintDescriptor<T>? {
    @Suppress("UNCHECKED_CAST")
    return this.firstOrNull { it.annotation is T } as ConstraintDescriptor<T>?
}

private inline fun <reified T : Annotation> ElementDescriptor.lookup(): ConstraintDescriptor<T>? {
    return this.constraintDescriptors.lookup() ?: this.constraintDescriptors.flatMap { it.composingConstraints }.lookup()
}

private fun boundType(exclusive: Boolean?): BoundType {
    return when (exclusive) {
        true -> BoundType.OPEN
        false -> BoundType.CLOSED
        null -> BoundType.CLOSED
    }
}

private fun <T : Comparable<*>> Schema<*>.buildRange(
    minGetter: (Schema<*>) -> T?,
    maxGetter: (Schema<*>) -> T?,
    excludeMinimum: (Schema<*>) -> Boolean? = { false },
    excludeMax: (Schema<*>) -> Boolean? = { false }
): Range<T> {
    val minimum = minGetter(this)
    val maximum = maxGetter(this)
    val lowerBoundType = boundType(excludeMinimum(this))
    val upperBoundType = boundType(excludeMax(this))
    return if (this.minimum != null) {
        if (maximum != null) {
            Range.range(minimum, lowerBoundType, maximum, upperBoundType)
        } else {
            Range.downTo(minimum, lowerBoundType)
        }
    } else {
        if (maximum != null) {
            Range.upTo(maximum, upperBoundType)
        } else {
            Range.all()
        }
    }
}

private fun applyRange(property: Schema<*>, range: Range<BigDecimal>) {
    val propRange: Range<BigDecimal> = property.buildRange({ it.minimum }, { it.maximum }, { it.exclusiveMinimum }, { it.exclusiveMaximum })
    val newRange = propRange.intersection(range)
    if (newRange.hasUpperBound()) {
        property.maximum = newRange.upperEndpoint()
        property.exclusiveMaximum = newRange.upperBoundType() == BoundType.OPEN
    }
    if (newRange.hasLowerBound()) {
        property.minimum = newRange.lowerEndpoint()
        property.exclusiveMinimum = newRange.lowerBoundType() == BoundType.OPEN
    }
}
