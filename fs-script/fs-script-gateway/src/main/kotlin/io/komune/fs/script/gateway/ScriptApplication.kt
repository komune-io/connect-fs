package io.komune.fs.script.gateway

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@SpringBootApplication(scanBasePackages = ["io.komune.fs.script"])
class ScriptApplication

fun main(args: Array<String>) {
	SpringApplication(ScriptApplication::class.java).run {
		run(*args)
	}
}
