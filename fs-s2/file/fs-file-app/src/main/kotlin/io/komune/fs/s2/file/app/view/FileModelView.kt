package io.komune.fs.s2.file.app.view

import io.komune.fs.s2.file.app.entity.FileEntity
import io.komune.fs.s2.file.domain.automate.FileState
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvent
import io.komune.fs.s2.file.domain.features.command.FileEvent
import io.komune.fs.s2.file.domain.features.command.FileInitiatedEvent
import io.komune.fs.s2.file.domain.features.command.FileLoggedEvent
import s2.sourcing.dsl.view.View

class FileModelView: View<FileEvent, FileEntity> {

	override suspend fun evolve(event: FileEvent, model: FileEntity?): FileEntity? = when (event) {
		is FileInitiatedEvent -> created(event)
		is FileLoggedEvent -> model?.logged(event)
		is FileDeletedEvent -> model?.delete(event)
	}

	private fun created(event: FileInitiatedEvent) = FileEntity(
		id = event.id,
		name = event.path.name,
		objectType = event.path.objectType,
		objectId = event.path.objectId,
		directory = event.path.directory,
		metadata = event.metadata,
		uploadDate = event.time,
		status = FileState.Exists
	)

	private fun FileEntity.logged(event: FileLoggedEvent) = copy(
		name = event.path.name,
		objectType = event.path.objectType,
		objectId = event.path.objectId,
		directory = event.path.directory,
		metadata = event.metadata,
		uploadDate = event.time
	)

	@Suppress("UnusedParameter")
	private fun FileEntity.delete(event: FileDeletedEvent) = copy(
		status = FileState.Deleted
	)
}
