package com.scrumpokerpro.service.retrotemplate

import com.scrumpokerpro.PostgreSqlContainerInitializer
import com.scrumpokerpro.dto.meeting.RetroColumnDto
import com.scrumpokerpro.dto.meeting.RetroTemplateDto
import com.scrumpokerpro.entity.meeting.retro.RetroTemplate
import com.scrumpokerpro.repository.RetroTemplateRepository
import io.r2dbc.postgresql.codec.Json
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.UUID

@SpringBootTest
@ContextConfiguration(initializers = [PostgreSqlContainerInitializer::class])
@ActiveProfiles("test")
class RetroTemplateServiceImplTest(
    @Autowired
    val retroTemplateService: RetroTemplateService,
    @Autowired
    val retroTemplateRepository: RetroTemplateRepository
) {

    @Test
    fun `should create new retro template`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val dto = RetroTemplateDto(
                columns = listOf(RetroColumnDto(name = "Action items", color = "#FF0000"))
            )
            retroTemplateService.createTemplate(
                userId = userId,
                dto = dto
            )

            val createdTemplate = retroTemplateService.getTemplate(userId)
            assertEquals(dto.columns[0].name, createdTemplate?.columns?.get(0)?.name)
            assertEquals(dto.columns[0].color, createdTemplate?.columns?.get(0)?.color)
        }
    }

    @Test
    fun `should get retro template`() {
        runBlocking {
            val userId = UUID.randomUUID()
            val retroTemplate = RetroTemplate(
                userId = userId,
                columns = Json.of("""[{"name":"What went well?","color":"#FF0000"}]"""),
                created = LocalDateTime.now(),
                modified = LocalDateTime.now()
            ).let {
                retroTemplateRepository.save(it)
            }

            val fromDb = retroTemplateService.getTemplate(userId)
            assertNotNull(fromDb?.columns)
        }
    }
}
