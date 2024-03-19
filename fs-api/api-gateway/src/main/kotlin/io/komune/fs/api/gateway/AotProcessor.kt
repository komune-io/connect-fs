package io.komune.fs.api.gateway

import io.komune.fs.api.config.FsProperties
import io.komune.fs.api.config.JwtProperties
import io.komune.fs.api.config.S3Properties
import io.komune.fs.api.config.SpaceProperties
import io.komune.fs.s2.file.domain.features.command.FileDeleteCommand
import io.komune.fs.s2.file.domain.features.command.FileInitCommand
import io.komune.fs.s2.file.domain.features.command.FileInitPublicDirectoryCommand
import io.komune.fs.s2.file.domain.features.command.FileLogCommand
import io.komune.fs.s2.file.domain.features.command.FileRevokePublicDirectoryCommand
import io.komune.fs.s2.file.domain.features.command.FileUploadCommand
import io.komune.fs.s2.file.domain.features.query.FileDownloadQuery
import io.komune.fs.s2.file.domain.features.query.FileDownloadResult
import io.komune.fs.s2.file.domain.features.query.FileGetQuery
import io.komune.fs.s2.file.domain.features.query.FileGetResult
import io.komune.fs.s2.file.domain.features.query.FileListQuery
import io.komune.fs.s2.file.domain.features.query.FileListResult
import io.minio.BaseArgs
import io.minio.ListObjectsArgs
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.minio.StatObjectArgs
import io.minio.Xml
import io.minio.messages.ErrorResponse
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.io.Serializable
import java.lang.reflect.Method
import java.util.stream.Collectors
import org.reflections.Reflections
import org.springframework.aot.generate.GenerationContext
import org.springframework.aot.hint.ExecutableMode
import org.springframework.aot.hint.RuntimeHints
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.util.ReflectionUtils


class AotProcessor : BeanFactoryInitializationAotProcessor {

    override fun processAheadOfTime(bf: ConfigurableListableBeanFactory): BeanFactoryInitializationAotContribution {
        return  BeanFactoryInitializationAotContribution { ctx: GenerationContext, code: BeanFactoryInitializationCode? ->
            val hints = ctx.runtimeHints
            hints.resources().registerPattern("user/*")

            hints.reflection().registerType(FsProperties::class.java)
            hints.reflection().registerType(SpaceProperties::class.java)
            hints.reflection().registerType(JwtProperties::class.java)


            hints.register(FsProperties::class.java)
            hints.register(S3Properties::class.java)
            hints.register(SpaceProperties::class.java)
            hints.register(JwtProperties::class.java)


            hints.register(FileDeleteCommand::class.java)
            hints.register(FileInitCommand::class.java)
            hints.register(FileInitPublicDirectoryCommand::class.java)
            hints.register(FileLogCommand::class.java)
            hints.register(FileRevokePublicDirectoryCommand::class.java)
            hints.register(FileUploadCommand::class.java)

            hints.register(FileListQuery::class.java)
            hints.register(FileListResult::class.java)
            hints.register(FileGetQuery::class.java)
            hints.register(FileGetResult::class.java)
            hints.register(FileDownloadQuery::class.java)
            hints.register(FileDownloadResult::class.java)

            hints.register(StatObjectArgs::class.java)
            hints.register(PutObjectArgs::class.java)
            hints.register(RemoveObjectArgs::class.java)
            hints.register(ListObjectsArgs::class.java)

            registerXml(hints)
        }
    }

    private fun registerXml(hints: RuntimeHints) {
        hints.register(Xml::class.java)

        println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%")
        Reflections(ErrorResponse::class.java.packageName)
            .getSubTypesOf(Serializable::class.java).forEach {
            println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
            println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
            println(it)
            println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
            println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
            hints.register(it)
        }
        Reflections(BaseArgs::class.java.packageName)
            .getSubTypesOf(BaseArgs::class.java).forEach {
            println("////////////////////////////////////////////////////////")
            println("////////////////////////////////////////////////////////")
            println(it)
            println("////////////////////////////////////////////////////////")
            println("////////////////////////////////////////////////////////")
            hints.register(it)
        }

        val methodMarshal: Method = ReflectionUtils.findMethod(
            Xml::class.java,
            "marshal",
            Object::class.java,
        )!!
        hints.reflection().registerMethod(methodMarshal, ExecutableMode.INVOKE)

        val methodUnmarshalString: Method = ReflectionUtils.findMethod(
            Xml::class.java,
            "unmarshal",
            Class::class.java,
            String::class.java
        )!!
        hints.reflection().registerMethod(methodUnmarshalString, ExecutableMode.INVOKE)

        val methodUnmarshalReader: Method = ReflectionUtils.findMethod(
            Xml::class.java,
            "unmarshal",
            Class::class.java,
            Reader::class.java
        )!!
        hints.reflection().registerMethod(methodUnmarshalReader, ExecutableMode.INVOKE)

        val validateReader: Method = ReflectionUtils.findMethod(
            Xml::class.java,
            "validate",
            Class::class.java,
            String::class.java
        )!!
        hints.reflection().registerMethod(validateReader, ExecutableMode.INVOKE)
    }

    private fun <T> RuntimeHints.register(clazz: Class<T>) {
        reflection().registerType(clazz)
        clazz.declaredConstructors.forEach {
            reflection().registerConstructor(it, ExecutableMode.INVOKE)
        }
    }
}
