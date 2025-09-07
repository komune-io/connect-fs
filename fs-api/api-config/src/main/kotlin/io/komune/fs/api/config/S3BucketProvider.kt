package io.komune.fs.api.config

import io.komune.f2.spring.boot.auth.AuthenticationProvider
import io.komune.fs.s2.file.domain.error.NoBucketConfiguredError
import f2.dsl.cqrs.error.asException

class S3BucketProvider(
    private val fsProperties: FsProperties
) {

    suspend fun getBucket(): String {
        return getSpace() ?: throw NoBucketConfiguredError().asException()
    }

    private suspend fun getSpace(): String? {
        return fsProperties.space?.name ?: AuthenticationProvider.getTenant()
    }

}
