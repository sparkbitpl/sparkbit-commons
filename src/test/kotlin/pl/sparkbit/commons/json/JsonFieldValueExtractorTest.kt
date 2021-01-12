package pl.sparkbit.commons.json

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.eq
import org.assertj.core.api.Assertions
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.MethodArgumentNotValidException
import pl.sparkbit.commons.jackson.JsonFieldDeserializer


class JsonFieldValueExtractorTest {
    private val service = Mockito.mock(JsonFieldService::class.java)

    private val api = JsonFieldTestController(service)
    private val mvc = MockMvcBuilders.standaloneSetup(api)
        .setMessageConverters(jacksonConverter(
            jsonFieldModule(), KotlinModule()
        ))
        .setValidator(CustomLocalValidatorFactoryBean(listOf(JsonFieldValueExtractor())))
        .build()

    @Test
    fun testMappingJsonToObject() {
        sendPatchRequest("""{"name": "FGH", "width": 1, "inner": {"x": 1}}""")
            .andExpect(status().isOk)

        verify(service).patchResource(eq("123"), argThat {
            this.name == JsonField.wrap("FGH") &&
                this.width == JsonField.wrap(1) &&
                this.height == JsonField.absent<Int>() &&
                this.inner == JsonField.wrap(InnerDTO(1))
        })
    }

    @Test
    fun testValidateFields() {
        val exc = sendPatchRequest("""{"name": "", "width": 1}""")
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException as MethodArgumentNotValidException

        Assertions.assertThat(exc.bindingResult.fieldErrorCount).isEqualTo(1)
        Assertions.assertThat(exc.bindingResult.getFieldError("name")!!.defaultMessage).isEqualTo("size must be between 1 and 3")
        verifyNoInteractions(service)
    }

    @Test
    fun testValidateNotNullFields() {
        val exc = sendPatchRequest("""{"name": "ABC", "width": null}""")
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException as MethodArgumentNotValidException

        Assertions.assertThat(exc.bindingResult.fieldErrorCount).isEqualTo(1)
        Assertions.assertThat(exc.bindingResult.getFieldError("width")!!.defaultMessage).isEqualTo("must not be null")
        verifyNoInteractions(service)
    }

    @Test
    @Ignore("Fails because Spring does not know how to extract wrapped value: https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/validation/beanvalidation/SpringValidatorAdapter.java#L312")
    fun testValidateInnerDto() {
        val exc = sendPatchRequest("""{"inner": {"x": -1, "y": -2}}""")
            .andExpect(status().isBadRequest)
            .andReturn().resolvedException as MethodArgumentNotValidException

        Assertions.assertThat(exc.bindingResult.fieldErrorCount).isEqualTo(2)
        Assertions.assertThat(exc.bindingResult.getFieldError("inner.x")!!.defaultMessage).isEqualTo("must not be null")
        Assertions.assertThat(exc.bindingResult.getFieldError("inner.y")!!.defaultMessage).isEqualTo("must not be null")
        verifyNoInteractions(service)
    }

    private fun sendPatchRequest(body: String) = mvc.perform(MockMvcRequestBuilders.patch("/testResources/123")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andDo(MockMvcResultHandlers.print())

    companion object {
        private fun jsonFieldModule(): SimpleModule {
            val module = SimpleModule()
            module.addDeserializer(JsonField::class.java, JsonFieldDeserializer())
            return module
        }

        private fun jacksonConverter(vararg modules: Module): MappingJackson2HttpMessageConverter {
            val objectMapper = ObjectMapper()
            objectMapper.registerModules(*modules)
            val converter = MappingJackson2HttpMessageConverter()
            converter.objectMapper = objectMapper
            return converter
        }
    }
}