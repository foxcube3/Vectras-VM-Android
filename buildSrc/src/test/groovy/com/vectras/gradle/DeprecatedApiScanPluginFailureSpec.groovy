package com.vectras.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.Test

import static org.junit.Assert.*

class DeprecatedApiScanPluginFailureSpec {
    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder()

    @Test
    void 'scan fails with deprecated usage when failOnDeprecated set'() {
        def root = testProjectDir.root
        // Fake subproject structure
        File demoDir = new File(root, 'demo/src/main/java/com/example')
        demoDir.mkdirs()
        new File(root, 'demo/build.gradle').text = ''
        new File(root, 'build.gradle').text = "plugins { id 'com.vectras.deprecated-scan' }\n"
        new File(root, 'settings.gradle').text = "include ':demo'\n"
        new File(demoDir, 'UseDeprecated.java').text = """
            package com.example; import android.os.Handler; class UseDeprecated { void x(){ new Handler(); } }
        """.stripIndent()

        def runner = GradleRunner.create()
            .withProjectDir(root)
            .withArguments('deprecatedApiScan','-PfailOnDeprecated','--stacktrace')
            .withPluginClasspath()
        boolean threw = false
        try {
            runner.build()
        } catch (org.gradle.testkit.runner.UnexpectedBuildSuccess ignored) {
            // did not fail when expected
        } catch (Exception e) {
            threw = true
            assertTrue(e.message?.contains('Deprecated API scan failed'))
        }
        assertTrue("Expected scan to fail with -PfailOnDeprecated", threw)
    }
}
