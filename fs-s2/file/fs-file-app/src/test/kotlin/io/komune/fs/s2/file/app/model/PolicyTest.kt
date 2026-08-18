package io.komune.fs.s2.file.app.model

import io.komune.fs.commons.utils.parseJsonTo
import io.komune.fs.commons.utils.toJson
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PolicyTest {

    @Test
    fun `getOrAddStatementWith creates a statement when none matches`() {
        val policy = Policy()

        val statement = policy.getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)

        assertThat(policy.statements).containsExactly(statement)
        assertThat(statement.effect).isEqualTo("Allow")
        assertThat(statement.actions).containsExactly("s3:GetObject")
    }

    @Test
    fun `getOrAddStatementWith returns the existing statement instead of duplicating`() {
        val policy = Policy()

        val first = policy.getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)
        val second = policy.getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)

        assertThat(second).isSameAs(first)
        assertThat(policy.statements).hasSize(1)
    }

    @Test
    fun `effect is part of the match, so Allow and Deny are separate statements`() {
        val policy = Policy()

        policy.getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)
        policy.getOrAddStatementWith(S3Effect.DENY, S3Action.GET_OBJECT)

        assertThat(policy.statements).hasSize(2)
    }

    @Test
    fun `getStatementWith returns null when nothing matches`() {
        assertThat(Policy().getStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT)).isNull()
    }

    @Test
    fun `resources are namespaced with the s3 arn prefix`() {
        val statement = Statement.with(S3Effect.ALLOW, S3Action.GET_OBJECT)

        statement.addResource("my-bucket", "public/*")

        assertThat(statement.resources).containsExactly("arn:aws:s3:::my-bucket/public/*")
    }

    @Test
    fun `removeResource undoes addResource`() {
        val statement = Statement.with(S3Effect.ALLOW, S3Action.GET_OBJECT)

        statement.addResource("my-bucket", "public/*")
        statement.removeResource("my-bucket", "public/*")

        assertThat(statement.resources).isEmpty()
    }

    @Test
    fun `serializes with the capitalised keys S3 expects`() {
        val policy = Policy().apply {
            getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT).addResource("b", "p/*")
        }

        val json = policy.toJson()

        // These are wire-format names read by MinIO, not internal field names.
        assertThat(json).contains("\"Version\"", "\"Statement\"", "\"Effect\"", "\"Principal\"")
        assertThat(json).contains("\"Action\"", "\"Resource\"")
    }

    @Test
    fun `round-trips through the json mapper`() {
        val policy = Policy().apply {
            getOrAddStatementWith(S3Effect.ALLOW, S3Action.GET_OBJECT).addResource("b", "p/*")
        }

        val parsed = policy.toJson().parseJsonTo(Policy::class.java)

        assertThat(parsed.version).isEqualTo(policy.version)
        assertThat(parsed.statements).hasSize(1)
        assertThat(parsed.statements.first().resources).containsExactly("arn:aws:s3:::b/p/*")
    }

    @Test
    fun `default version is the AWS policy language date`() {
        assertThat(Policy().version).isEqualTo("2012-10-17")
    }
}
