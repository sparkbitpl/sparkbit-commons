package pl.sparkbit.commons.openapi

import org.springdoc.core.customizers.PropertyCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(PropertyCustomizer::class)
open class OpenApiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    open fun enumPropertyCustomizer(): EnumPropertyCustomizer {
        return EnumPropertyCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun digitsCustomizer(): DigitsCustomizer {
        return DigitsCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun inRangeCustomizer(): InRangeCustomizer {
        return InRangeCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun negativeCustomizer(): NegativeCustomizer {
        return NegativeCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun positiveCustomizer(): PositiveCustomizer {
        return PositiveCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun positiveOrZeroCustomizer(): PositiveOrZeroCustomizer {
        return PositiveOrZeroCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun negativeOrZeroCustomizer(): NegativeOrZeroCustomizer {
        return NegativeOrZeroCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun notNullCustomizer(): RequiredFieldCustomizer {
        return RequiredFieldCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun patternCustomizer(): PatternCustomizer {
        return PatternCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun minCustomizer(): MinCustomizer {
        return MinCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun maxCustomizer(): MaxCustomizer {
        return MaxCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun sizeCustomizer(): SizeCustomizer {
        return SizeCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun emailCustomizer(): EmailCustomizer {
        return EmailCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun decimalMinCustomizer(): DecimalMinCustomizer {
        return DecimalMinCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun decimalMaxCustomizer(): DecimalMaxCustomizer {
        return DecimalMaxCustomizer()
    }

    @Bean
    @ConditionalOnMissingBean
    open fun hibernateModel(customizers: List<JavaBeansAwarePropertyCustomizer>): BeansValidationModel {
        return BeansValidationModel(customizers)
    }
}
