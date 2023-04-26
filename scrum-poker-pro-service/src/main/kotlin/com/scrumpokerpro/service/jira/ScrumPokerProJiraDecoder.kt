package com.scrumpokerpro.service.jira

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumpokerpro.exception.JiraBadRequestException
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus
import java.lang.Exception
import java.nio.charset.StandardCharsets

class ScrumPokerProJiraDecoder : ErrorDecoder {

    private val default: ErrorDecoder = ErrorDecoder.Default()

    override fun decode(methodKey: String, response: Response): Exception {
        if (response.status() == HttpStatus.BAD_REQUEST.value()) {
            val detail = ObjectMapper().readTree(response.body().asReader(StandardCharsets.UTF_8)).get("detail")?.textValue()
            return JiraBadRequestException(detail)
        }
        return default.decode(methodKey, response)
    }
}
