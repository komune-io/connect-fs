package io.komune.fs.script.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@SpringBootApplication(scanBasePackages = ["io.komune.fs.script"])
class ScriptApplication

fun main(args: Array<String>) {
    runApplication<ScriptApplication>(*args)
}
