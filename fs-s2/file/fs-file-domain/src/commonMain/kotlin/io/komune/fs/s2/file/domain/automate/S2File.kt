package io.komune.fs.s2.file.domain.automate

import io.komune.fs.s2.file.domain.features.command.FileDeleteByIdCommand
import io.komune.fs.s2.file.domain.features.command.FileDeletedEvent
import io.komune.fs.s2.file.domain.features.command.FileInitCommand
import io.komune.fs.s2.file.domain.features.command.FileInitiatedEvent
import io.komune.fs.s2.file.domain.features.command.FileLogCommand
import io.komune.fs.s2.file.domain.features.command.FileLoggedEvent
import kotlinx.serialization.Serializable
import s2.dsl.automate.S2Role
import s2.dsl.automate.S2State
import s2.dsl.automate.builder.s2Sourcing

/**
 * Identifier of a file
 * @d2 model
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @visual json "91541047-5da8-4161-af79-3fd367fc014e"
 * @order 0
 */
typealias FileId = String

object S2 {
	val traceSourcing = s2Sourcing {
		name = "FileSourcing"
		transaction<FileInitCommand, FileInitiatedEvent> {
			to = FileState.Exists
			role = FileRole.Tracer
		}
		selfTransaction<FileLogCommand, FileLoggedEvent> {
			states += FileState.Exists
			role = FileRole.Tracer
		}
		transaction<FileDeleteByIdCommand, FileDeletedEvent> {
			from = FileState.Exists
			to = FileState.Deleted
			role = FileRole.Tracer
		}
	}
}

@Serializable
open class FileRole(open val name: String): S2Role {
	object Tracer: FileRole("Tracer")
	override fun toString(): String = name
}

@Serializable
enum class FileState(override var position: Int): S2State {
	Exists(0),
	Deleted(1)
}
