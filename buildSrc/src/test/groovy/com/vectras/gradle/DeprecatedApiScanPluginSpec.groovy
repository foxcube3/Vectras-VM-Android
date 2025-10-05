package com.vectras.gradle

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.Test

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.junit.Assert.*

class DeprecatedApiScanPluginSpec {

    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder()

    @Test
    void 'scan task runs with zero findings'() {
        def root = testProjectDir.root
        // settings.gradle
        new File(root, 'settings.gradle').text = ''
        // build.gradle applying plugin under test via included buildSrc automatically
        new File(root, 'build.gradle').text = """
            plugins { id 'com.vectras.deprecated-scan' }
        """.stripIndent()
        // Create empty subproject dir structure to simulate scanning
        def appDir = new File(root, 'app/src/main/java').mkdirs()
        def result = GradleRunner.create()
            .withProjectDir(root)
            .withArguments('deprecatedApiScan')
            .withPluginClasspath() // ensures buildSrc classes are visible
            .build()
        assertEquals(SUCCESS, result.task(':deprecatedApiScan').outcome)
        assertTrue(result.output.contains('TOTAL'))
    }
}
