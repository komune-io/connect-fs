package io.komune.fs.s2.file.app.config

import io.komune.fs.s2.file.app.entity.FileEntity
import io.komune.fs.s2.file.domain.automate.FileId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper

@Configuration
class RedisConfig {

	@Bean
	fun reactiveRedisTemplate(
		factory: ReactiveRedisConnectionFactory,
		objectMapper: ObjectMapper
	): ReactiveRedisTemplate<FileId, FileEntity> {
		val keySerializer = StringRedisSerializer()
		val valueSerializer = JacksonJsonRedisSerializer(objectMapper, FileEntity::class.java)
		val builder = RedisSerializationContext.newSerializationContext<FileId, FileEntity>(keySerializer)
		val context = builder.value(valueSerializer).build()
		return ReactiveRedisTemplate(factory, context)
	}
}
