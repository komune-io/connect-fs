package io.komune.fs.s2.file.app.view

import io.komune.fs.s2.file.app.entity.FileEntity
import io.komune.fs.s2.file.domain.automate.FileId
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import s2.sourcing.dsl.snap.SnapRepository

@Component
class RedisSnapView(
    private val template: ReactiveRedisTemplate<FileId, FileEntity>,
): SnapRepository<FileEntity, FileId>  {

    override suspend fun get(id: FileId): FileEntity? {
        return template.opsForSet().pop(id).awaitFirstOrNull()
    }

    override suspend fun save(entity: FileEntity): FileEntity {
        return template.opsForSet().add(entity.id, entity).asFlow().toList().let {
            entity
        }
    }

    override suspend fun remove(id: FileId): Boolean {
        return template.opsForSet().delete(id).awaitSingle()
    }
}
