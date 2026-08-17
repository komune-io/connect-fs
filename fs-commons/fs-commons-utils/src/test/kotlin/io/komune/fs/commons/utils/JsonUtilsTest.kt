package io.komune.fs.commons.utils

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class JsonUtilsTest {

    data class Sample(val name: String = "", val count: Int = 0)

    @Test
    fun `toJson then parseJsonTo round-trips`() {
        val original = Sample(name = "file.txt", count = 3)

        val parsed = original.toJson().parseJsonTo(Sample::class.java)

        assertThat(parsed).isEqualTo(original)
    }

    @Test
    fun `unknown properties are ignored rather than failing`() {
        val json = """{"name":"file.txt","count":3,"somethingNobodyDeclared":true}"""

        val parsed = json.parseJsonTo(Sample::class.java)

        assertThat(parsed.name).isEqualTo("file.txt")
        assertThat(parsed.count).isEqualTo(3)
    }

    @Test
    fun `unquoted property names are accepted`() {
        val json = """{name:"file.txt",count:7}"""

        val parsed = json.parseJsonTo(Sample::class.java)

        assertThat(parsed.count).isEqualTo(7)
    }

    @Test
    fun `an array parses to a list`() {
        val json = """[{"name":"a","count":1},{"name":"b","count":2}]"""

        val parsed: List<Sample> = json.parseJsonTo(Array<Sample>::class.java)

        assertThat(parsed).containsExactly(Sample("a", 1), Sample("b", 2))
    }

    @Test
    fun `malformed json throws rather than returning null`() {
        assertThatThrownBy { """{"name":""".parseJsonTo(Sample::class.java) }
            .isInstanceOf(Exception::class.java)
    }
}
