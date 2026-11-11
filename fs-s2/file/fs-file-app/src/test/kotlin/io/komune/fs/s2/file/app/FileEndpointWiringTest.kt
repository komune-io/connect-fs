package io.komune.fs.s2.file.app

import io.komune.fs.api.config.FsProperties
import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.api.config.S3Properties
import io.komune.fs.api.config.SpaceProperties
import f2.dsl.fnc.F2Function
import f2.dsl.fnc.invokeWith
import io.komune.fs.commons.kb.KbClient
import io.komune.fs.commons.kb.domain.command.VectorAskedEventDTOBase
import io.komune.fs.s2.file.app.config.FileSourcingS2Decider
import io.komune.fs.s2.file.app.config.FsSsmConfig
import io.komune.fs.s2.file.app.service.S3Service
import io.komune.fs.s2.file.domain.features.query.FileAskQuestionQuery
import io.komune.fs.s2.file.domain.model.FileAskMetadata
import io.minio.MinioClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.HttpStatus

class FileEndpointWiringTest {

    private val s3Properties = S3Properties(
        internalUrl = "http://localhost:9000",
        externalUrl = "http://localhost:9000",
        username = "user",
        password = "password",
    )
    private val bucketProvider = S3BucketProvider(
        FsProperties(s3 = s3Properties, init = null, space = SpaceProperties(name = "bucket", jwt = null))
    )

    private val runner = ApplicationContextRunner()
        .withBean(FileSourcingS2Decider::class.java)
        .withBean(S3Properties::class.java, { s3Properties })
        .withBean(S3BucketProvider::class.java, { bucketProvider })
        .withBean(FileDeciderSourcingImpl::class.java)
        .withBean(FsSsmConfig::class.java, {
            FsSsmConfig(channel = "c", chaincode = "cc", signerName = "s", signerFile = "f", directories = null)
        })
        .withBean(MinioClient::class.java, { MinioClient.builder().endpoint(s3Properties.internalUrl).build() })
        .withBean(S3Service::class.java)
        .withUserConfiguration(FileEndpoint::class.java)

    private val question = FileAskQuestionQuery(
        question = "What is it?",
        history = emptyList(),
        metadata = FileAskMetadata(targetedFiles = listOf("o/i/d/n")),
    )

    @Test
    fun `without a knowledge base, asking a question is refused with 503`() {
        runner.run { context ->
            assertThat(context).hasNotFailed()
            val endpoint = context.getBean(FileEndpoint::class.java)

            val refusal = assertThrows<KnowledgeBaseNotConfiguredException> {
                runBlocking { val _ = question.invokeWith(endpoint.fileAskQuestion()) }
            }

            assertThat(refusal.status).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
            assertThat(refusal.error.refusal?.code).isEqualTo(KnowledgeBaseNotConfiguredException.REFUSAL_CODE)
        }
    }

    @Test
    fun `with a knowledge base, the question is answered by it`() {
        val kbClient = mock(KbClient::class.java)
        `when`(kbClient.knowledgeAsk()).thenReturn(
            F2Function { queries -> queries.map { VectorAskedEventDTOBase(item = "answer to ${it.question}") } }
        )

        runner.withBean(KbClient::class.java, { kbClient }).run { context ->
            val endpoint = context.getBean(FileEndpoint::class.java)

            val result = runBlocking { question.invokeWith(endpoint.fileAskQuestion()) }

            assertThat(result.item).isEqualTo("answer to What is it?")
        }
    }
}
