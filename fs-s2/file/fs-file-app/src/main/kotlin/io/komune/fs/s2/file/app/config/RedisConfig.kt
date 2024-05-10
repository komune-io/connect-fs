package io.komune.fs.s2.file.app.config

import io.komune.fs.s2.file.app.entity.FileEntity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.komune.fs.s2.file.domain.automate.FileId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

	@Bean
	fun reactiveRedisTemplate(
		factory: ReactiveRedisConnectionFactory
	): ReactiveRedisTemplate<FileId, FileEntity> {
		val keySerializer = StringRedisSerializer()

		val valueSerializer: Jackson2JsonRedisSerializer<FileEntity>
			= Jackson2JsonRedisSerializer(jacksonObjectMapper(), FileEntity::class.java)
		val builder: RedisSerializationContext.RedisSerializationContextBuilder<FileId, FileEntity> =
			RedisSerializationContext.newSerializationContext(keySerializer)
		val context: RedisSerializationContext<FileId, FileEntity> = builder.value(valueSerializer).build()
		return ReactiveRedisTemplate(factory, context)
	}

}
