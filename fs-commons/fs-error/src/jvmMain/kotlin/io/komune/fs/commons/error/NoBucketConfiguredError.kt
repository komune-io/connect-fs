package io.komune.fs.commons.error

import f2.dsl.cqrs.error.F2Error

import java.util.UUID

class NoBucketConfiguredError: F2Error(
    id = UUID.randomUUID().toString(),
    message = "Bucket not found from configuration or jwt token",
    code = 1,
    timestamp = System.currentTimeMillis().toString(),
    requestId = null
)
