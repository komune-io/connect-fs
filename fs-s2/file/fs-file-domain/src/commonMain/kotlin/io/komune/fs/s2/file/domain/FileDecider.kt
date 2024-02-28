package io.komune.fs.s2.file.domain

import io.komune.fs.s2.file.domain.features.command.FileDeleteByIdCommand
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvent
import io.komune.fs.s2.file.domain.features.command.FileInitCommand
import io.komune.fs.s2.file.domain.features.command.FileInitiatedEvent
import io.komune.fs.s2.file.domain.features.command.FileLogCommand
import io.komune.fs.s2.file.domain.features.command.FileLoggedEvent

interface FileDecider {
	suspend fun init(cmd: FileInitCommand): FileInitiatedEvent
	suspend fun log(cmd: FileLogCommand): FileLoggedEvent
	suspend fun delete(cmd: FileDeleteByIdCommand): FileDeletedEvent
}
