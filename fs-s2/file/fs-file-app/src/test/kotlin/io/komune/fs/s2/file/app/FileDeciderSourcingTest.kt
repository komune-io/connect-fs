package io.komune.fs.s2.file.app

import io.komune.fs.s2.file.app.config.SpringTestBase
import org.springframework.beans.factory.annotation.Autowired

internal class FileDeciderSourcingTest: SpringTestBase() {

	@Autowired
	lateinit var fundReceiptDeciderSourcingImpl: FileDeciderSourcingImpl

}
