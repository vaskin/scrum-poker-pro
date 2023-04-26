package com.scrumpokerpro.exception

import com.scrumpokerpro.utils.logger
import feign.FeignException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebInputException

@Component
class ErrorAttributes : DefaultErrorAttributes() {

    val log by logger()

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val error = getError(request)
        log.error("web exception handler", error)
        return assembleError(request)
    }

    private fun assembleError(request: ServerRequest): MutableMap<String, Any> {
        return when (val error = getError(request)) {
            is EntityNotFoundException -> fillAttributes(RestErrorReason.RESOURCE_NOT_FOUND, error.message.orEmpty(), request.path())
            is AccessDeniedException -> fillAttributes(RestErrorReason.ACCESS_DENIED, error.message.orEmpty(), request.path())
            is ServerWebInputException -> fillAttributes(RestErrorReason.BAD_REQUEST, error.message, request.path())
            is JiraBadRequestException -> fillAttributes(RestErrorReason.BAD_REQUEST, error.message ?: "", request.path())
            is FeignException.Forbidden -> fillAttributes(RestErrorReason.JIRA_ACCESS_DENIED, error.message ?: "", request.path())
            else -> fillAttributes(RestErrorReason.INTERNAL, error.message.orEmpty(), request.path())
        }
    }

    private fun fillAttributes(reason: RestErrorReason, detail: String, path: String): MutableMap<String, Any> {
        return LinkedHashMap<String, Any>().apply {
            this["type"] = reason.code
            this["status"] = reason.status.value()
            this["title"] = reason.message
            this["detail"] = detail
            this["path"] = path
        }
    }
}
