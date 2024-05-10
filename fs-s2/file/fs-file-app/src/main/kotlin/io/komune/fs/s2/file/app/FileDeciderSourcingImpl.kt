package io.komune.fs.s2.file.app

import io.komune.fs.api.config.S3BucketProvider
import io.komune.fs.api.config.S3Properties
import io.komune.fs.s2.file.app.config.FileSourcingS2Decider
import io.komune.fs.s2.file.domain.FileDecider
import io.komune.fs.s2.file.domain.features.command.FileDeleteByIdCommand
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvent
import io.komune.fs.s2.file.domain.features.command.FileInitCommand
import io.komune.fs.s2.file.domain.features.command.FileInitiatedEvent
import io.komune.fs.s2.file.domain.features.command.FileLogCommand
import io.komune.fs.s2.file.domain.features.command.FileLoggedEvent
import io.komune.fs.s2.file.domain.model.FilePath
import org.springframework.stereotype.Service

@Service
class FileDeciderSourcingImpl(
	private val decider: FileSourcingS2Decider,
	private val s3Properties: S3Properties,
	private val s3BucketProvider: S3BucketProvider
): FileDecider {

	override suspend fun init(cmd: FileInitCommand): FileInitiatedEvent = decider.init(cmd) {
		FileInitiatedEvent(
			id = cmd.id,
			path = cmd.path,
			url = cmd.url,
			hash = cmd.hash,
			metadata = cmd.metadata,
			time = System.currentTimeMillis(),
		)
	}

	override suspend fun log(cmd: FileLogCommand): FileLoggedEvent = decider.transition(cmd) { file ->
		val path = FilePath.from(cmd.path)
		FileLoggedEvent(
			id = file.id,
			path = path,
			url = path.buildUrl(),
			hash = cmd.hash,
			metadata = cmd.metadata,
			time = System.currentTimeMillis(),
		)
	}

	override suspend fun delete(cmd: FileDeleteByIdCommand): FileDeletedEvent = decider.transition(cmd) { file ->
		FileDeletedEvent(
			id = file.id,
			path = FilePath(
				objectType = file.objectType,
				objectId = file.objectId,
				directory = file.directory,
				name = file.name
			)
		)
	}

	private suspend fun FilePath.buildUrl() = buildUrl(
		s3Properties.externalUrl, s3BucketProvider.getBucket(), s3Properties.dns
	)
}
