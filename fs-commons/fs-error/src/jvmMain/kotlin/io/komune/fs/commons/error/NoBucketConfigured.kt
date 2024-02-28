package io.komune.fs.commons.error

import f2.dsl.cqrs.error.F2Error
import f2.dsl.cqrs.exception.F2Exception

import java.util.UUID

class NoBucketConfigured: F2Error(
    id = UUID.randomUUID().toString(),
    message = "Bucket not found from configuration or jwt token",
    code = 1,
    timestamp = System.currentTimeMillis().toString(),
    requestId = null
)

class NoBucketConfiguredException: F2Exception(NoBucketConfigured())
