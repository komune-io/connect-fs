package io.komune.fs.api.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.komune.fs"])
class FsApplication

fun main(args: Array<String>) {
	runApplication<FsApplication>(*args)
}
