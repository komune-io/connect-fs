package city.smartb.fs.s2.file.domain.features.command

import city.smartb.fs.s2.file.domain.automate.FileId
import city.smartb.fs.s2.file.domain.model.Base64String
import city.smartb.fs.s2.file.domain.model.FilePath
import f2.dsl.fnc.F2Function
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Upload a file to a given path. If a file already exists at this path, overwrite it.
 * @d2 section
 * @parent [city.smartb.fs.s2.file.domain.D2FileCommandSection]
 * @order 10
 */
typealias FileUploadFunction = F2Function<FileUploadCommand, FileUploadedEvent>

/**
 * @d2 command
 * @parent [FileUploadFunction]
 */
@Serializable
data class FileUploadCommand(
	/**
	 * Path of the file to upload.
	 */
	val path: FilePath,

	/**
	 * Additional metadata of the file
	 */
	val metadata: Map<String, String>,

	/**
	 * Content of the file as Base64
	 * @example "Q291Y291IGplIHNhcHBlbCBjw6lkcmlr"
	 */
	val content: Base64String
)

/**
 * @d2 event
 * @parent [FileUploadFunction]
 */
@Serializable
@SerialName("FileUploadedEvent")
data class FileUploadedEvent(
	/**
	 * Identifier of the uploaded file.
	 */
	val id: FileId,

	/**
	 * Path of the uploaded file.
	 */
	val path: FilePath,

	/**
	 * External URL to access the uploaded file.
	 * @example [city.smartb.fs.s2.file.domain.model.File.url]
	 */
	val url: String,

	/**
	 * Hash of the uploaded file content.
	 * @example "rPpebW/JVqIlEg/HLu4H3GXS2dB+34TZnZjz54wm2a4="
	 */
	val hash: String,

	/**
	 * Additional metadata of the uploaded file.
	 * @example [city.smartb.fs.s2.file.domain.model.File.metadata]
	 */
	val metadata: Map<String, String>,

	/**
	 * File upload date
	 * @example 1650356237000
	 */
	val time: Long
)
