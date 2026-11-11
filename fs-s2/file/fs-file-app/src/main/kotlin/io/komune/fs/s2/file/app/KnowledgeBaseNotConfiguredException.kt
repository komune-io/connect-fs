package io.komune.fs.s2.file.app

import f2.dsl.cqrs.error.F2Refusal
import f2.spring.exception.F2HttpException
import org.springframework.http.HttpStatus

/** The refusal `fs.KNOWLEDGE_BASE_NOT_CONFIGURED`: a knowledge base function was called while `fs.kb.url` is unset. */
class KnowledgeBaseNotConfiguredException : F2HttpException(
    status = HttpStatus.SERVICE_UNAVAILABLE,
    message = "No knowledge base is configured on this file service",
    refusal = F2Refusal(code = REFUSAL_CODE),
) {
    companion object {
        const val REFUSAL_CODE = "fs.KNOWLEDGE_BASE_NOT_CONFIGURED"
    }
}
