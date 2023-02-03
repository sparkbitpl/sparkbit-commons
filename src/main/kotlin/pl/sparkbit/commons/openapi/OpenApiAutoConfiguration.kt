package pl.sparkbit.commons.openapi

import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(PropertyCustomizer::class)
open class OpenApiAutoConfiguration {

    @Bean
    open fun enumPropertyCustomizer(): EnumPropertyCustomizer {
        return EnumPropertyCustomizer()
    }

    @Bean
    open fun digitsCustomizer(): DigitsCustomizer {
        return DigitsCustomizer()
    }

    @Bean
    open fun inRangeCustomizer(): InRangeCustomizer {
        return InRangeCustomizer()
    }

    @Bean
    open fun negativeCustomizer(): NegativeCustomizer {
        return NegativeCustomizer()
    }

    @Bean
    open fun positiveCustomizer(): PositiveCustomizer {
        return PositiveCustomizer()
    }

    @Bean
    open fun positiveOrZeroCustomizer(): PositiveOrZeroCustomizer {
        return PositiveOrZeroCustomizer()
    }

    @Bean
    open fun negativeOrZeroCustomizer(): NegativeOrZeroCustomizer {
        return NegativeOrZeroCustomizer()
    }

    @Bean
    open fun notNullCustomizer(): RequiredFieldCustomizer {
        return RequiredFieldCustomizer()
    }

    @Bean
    open fun patternCustomizer(): PatternCustomizer {
        return PatternCustomizer()
    }

    @Bean
    open fun minCustomizer(): MinCustomizer {
        return MinCustomizer()
    }

    @Bean
    open fun maxCustomizer(): MaxCustomizer {
        return MaxCustomizer()
    }

    @Bean
    open fun sizeCustomizer(): SizeCustomizer {
        return SizeCustomizer()
    }

    @Bean
    open fun decimalMinCustomizer(): DecimalMinCustomizer {
        return DecimalMinCustomizer()
    }

    @Bean
    open fun decimalMaxCustomizer(): DecimalMaxCustomizer {
        return DecimalMaxCustomizer()
    }

    @Bean
    open fun hibernateModel(customizers: List<JavaBeansAwarePropertyCustomizer>): BeansValidationModel {
        return BeansValidationModel(customizers)
    }
}
