package io.komune.fs.s2.file.domain.error

import f2.dsl.cqrs.error.F2Error

class NoBucketConfiguredError: F2Error(
    message = "Bucket not found from configuration or jwt token",
    code = 1,
    requestId = null
)
