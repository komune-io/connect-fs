package io.komune.fs.s2.file.app

import io.komune.fs.api.config.FsProperties
import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.api.config.S3Properties
import io.komune.fs.api.config.SpaceProperties
import io.komune.fs.commons.kb.KbClient
import io.komune.fs.s2.file.app.config.FileSourcingS2Decider
import io.komune.fs.s2.file.app.config.FsSsmConfig
import io.komune.fs.s2.file.app.service.S3Service
import io.minio.MinioClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.test.util.ReflectionTestUtils

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

    @Test
    fun `the endpoint starts without a knowledge base`() {
        runner.run { context ->
            assertThat(context).hasNotFailed()
            assertThat(kbClientOf(context.getBean(FileEndpoint::class.java)).ifAvailable).isNull()
        }
    }

    @Test
    fun `the endpoint receives the knowledge base client when one is configured`() {
        val kbClient = mock(KbClient::class.java)

        runner.withBean(KbClient::class.java, { kbClient }).run { context ->
            assertThat(kbClientOf(context.getBean(FileEndpoint::class.java)).ifAvailable).isSameAs(kbClient)
        }
    }

    private fun kbClientOf(endpoint: FileEndpoint): ObjectProvider<*> =
        checkNotNull(ReflectionTestUtils.getField(endpoint, "kbClient")) as ObjectProvider<*>
}
