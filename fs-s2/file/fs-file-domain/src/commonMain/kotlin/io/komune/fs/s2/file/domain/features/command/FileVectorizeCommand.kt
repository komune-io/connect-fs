package io.komune.fs.s2.file.domain.features.command

import f2.dsl.fnc.F2Function
import io.komune.fs.s2.file.domain.model.FilePath
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Vectorize the file saved at a given path, then save it inside a vector store.
 * @d2 function
 * @parent [io.komune.fs.s2.file.domain.D2FilePage]
 * @order 50
 */
typealias FileVectorizeFunction = F2Function<FileVectorizeCommand, FileVectorizedEvent>

/**
 * @d2 command
 * @parent [FileVectorizeFunction]
 */
@Serializable
data class FileVectorizeCommand(
	/**
	 * Path of the file to vectorize.
	 */
	val path: FilePath,

	/**
	 * Metadata to add to the vectorized chunks of texts.
	 */
	val metadata: Map<String, String> = emptyMap()
)

/**
 * @d2 event
 * @parent [FileVectorizeFunction]
 */
@Serializable
@SerialName("FileVectorizedEvent")
data class FileVectorizedEvent(
	/**
	 * Path of the vectorized file.
	 */
	val path: FilePath
)
