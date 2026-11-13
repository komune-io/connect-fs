package io.komune.fs.api.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.komune.fs"])
class FsApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator", "MissingUseCall") // arguments read once; closing the context would stop the app
    runApplication<FsApplication>(*args)
}
