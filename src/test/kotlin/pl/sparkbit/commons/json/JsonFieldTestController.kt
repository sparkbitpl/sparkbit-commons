package pl.sparkbit.commons.json

import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

interface JsonFieldService {
    fun patchResource(id: String, dto: PatchDTO)
}

open class JsonFieldServiceImpl : JsonFieldService {
    override fun patchResource(id: String, dto: PatchDTO) {
        println("Update $id")
        dto.name.ifPresent { newName ->
            println("Update name to $newName")
        }
        dto.width.ifPresent { newWidth ->
            println("Update width to $newWidth")
        }
        dto.height.ifPresent { newHeight ->
            println("Update height to $newHeight")
        }
    }
}

data class InnerDTO(
    @field:Positive
    val x: Int,
    val y: JsonField<@Positive Int> = JsonField.absent()
)

data class PatchDTO(
    val name: JsonField<@Size(min = 1, max = 3) String> = JsonField.absent(),
    val width: JsonField<@NotNull Int> = JsonField.absent(),
    val height: JsonField<Int> = JsonField.absent(),
    val inner: JsonField<@Valid InnerDTO> = JsonField.absent()
)

@RestController
@RequiredArgsConstructor
class JsonFieldTestController(private val service: JsonFieldService) {

    @PatchMapping("/testResources/{id}")
    fun get(@PathVariable("id") id: String, @RequestBody @Valid dto: PatchDTO) {
        service.patchResource(id, dto)
    }
}