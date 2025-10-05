package com.vectras.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import groovy.json.JsonOutput

/**
 * Extension allowing users to override severity per category.
 * Example in root build.gradle:
 * deprecatedApiScanConfig {
 *   severities = [ handler: 'CRITICAL', progressDialog: 'LOW' ]
 * }
 */
class DeprecatedApiScanExtension {
    Map<String,String> severities = [:]
}

class DeprecatedApiScanTask extends DefaultTask {
    @TaskAction
    void runScan() {
        def patterns = [
            handler       : /new\s+Handler\s*\(\s*\)/,
            progressDialog: /new\s+ProgressDialog\s*\(/,
            onCreateDialog: /onCreateDialog\s*\(\s*int\s+\w+\s*\)/
        ]
        def defaults = [
            handler       : 'HIGH',
            progressDialog: 'MEDIUM',
            onCreateDialog: 'MEDIUM'
        ]
        def ext = (project.rootProject.extensions.findByName('deprecatedApiScanConfig') as DeprecatedApiScanExtension)
        def severities = defaults + (ext?.severities ?: [:]) // user overrides take precedence
        boolean includeKotlin = project.hasProperty('scanKotlin')
        File baselineFile = project.rootProject.file('deprecation-baseline.txt')
        Set<String> baseline = baselineFile.exists() ? (baselineFile.readLines().findAll{ it }.toSet()) : [] as Set<String>
        boolean updateBaseline = project.hasProperty('updateDeprecationBaseline')
        boolean failOnDeprecated = project.hasProperty('failOnDeprecated')
        Set<String> failOnCategories = (project.findProperty('failOn') ?: '')
            .toString().split(',')*.trim().findAll{ it }.toSet()
        boolean jsonReport = project.hasProperty('jsonReport')

        Map<String, List<String>> findings = [:].withDefault { [] }
        def rootPath = project.rootProject.projectDir.toPath()

        project.rootProject.subprojects.each { sp ->
            File mainDir = new File(sp.projectDir, 'src/main')
            List<File> dirs = []
            File javaDir = new File(mainDir, 'java'); if (javaDir.exists()) dirs << javaDir
            if (includeKotlin) { File kDir = new File(mainDir, 'kotlin'); if (kDir.exists()) dirs << kDir }
            dirs.each { dir ->
                dir.eachFileRecurse { f ->
                    if (f.isFile() && (f.name.endsWith('.java') || f.name.endsWith('.kt'))) {
                        int lineNo = 0
                        f.eachLine { line ->
                            lineNo++
                            patterns.each { cat, rx ->
                                if (line =~ rx) {
                                    def rel = rootPath.relativize(f.toPath()).toString().replace('\\','/')
                                    def key = "${rel}:${lineNo}:${cat}"
                                    findings[cat] << key
                                }
                            }
                        }
                    }
                }
            }
        }

        if (updateBaseline) {
            def all = findings.values().flatten().sort()
            baselineFile.text = all.join(System.lineSeparator()) + (all ? System.lineSeparator() : '')
            println "[deprecatedApiScan] Baseline updated (${all.size()} entries)."
            return
        }

        println "\nDeprecated API Pattern Summary (Groovy plugin):"
        int total = 0
        patterns.keySet().each { cat ->
            def active = findings[cat].findAll { !(it in baseline) }
            total += active.size()
            println "  ${cat.padRight(18)} ${String.format('%4d', active.size())}"
        }
    println "  ${'TOTAL'.padRight(18)} ${String.format('%4d', total)}"

        boolean failing = false
        if (failOnDeprecated && total > 0) {
            if (failOnCategories.isEmpty()) {
                failing = total > 0
            } else {
                failing = failOnCategories.any { cat -> findings[cat].any { !(it in baseline) } }
            }
        }

        if (jsonReport) {
            def categoriesJson = patterns.keySet().collectEntries { cat ->
                def active = findings[cat].findAll { !(it in baseline) }
                [ (cat): [
                    severity: severities[cat] ?: 'INFO',
                    count: active.size(),
                    occurrences: active.collect { k -> [ key: k ] }
                ]]
            }
            def report = [
                generatedAt: new Date().toString(),
                total: total,
                baselineSize: baseline.size(),
                failed: failing,
                failOnCategories: failOnCategories.sort(),
                categories: categoriesJson
            ]
            def outFile = project.rootProject.file('build/deprecated-api-report.json')
            outFile.parentFile.mkdirs()
            outFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(report))
            println "[deprecatedApiScan] JSON report -> ${outFile}"
        }

        // Export custom values to Gradle Enterprise build scan (if plugin present)
        try {
            def geExt = project.rootProject.extensions.findByName('gradleEnterprise')
            if (geExt && geExt.hasProperty('buildScan')) {
                def bs = geExt.buildScan
                bs.value 'deprecatedApi.total', total.toString()
                patterns.keySet().each { cat ->
                    def activeCount = findings[cat].count { !(it in baseline) }
                    bs.value "deprecatedApi.${cat}", activeCount.toString()
                    if (activeCount > 0) {
                        bs.tag "deprecatedApi-${cat}"
                    }
                }
                if (failing) {
                    bs.tag 'deprecatedApiFailed'
                } else {
                    bs.tag 'deprecatedApiClean'
                }
            }
        } catch (Throwable t) {
            // Swallow any reflection / API issues silently to avoid build disruption
        }

        if (failing) {
            throw new RuntimeException("Deprecated API scan failed: total=${total}")
        }
    }
}

class GroovyDeprecatedApiScanPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        if (project != project.rootProject) return
        // Register extension on root only
        if (!project.extensions.findByName('deprecatedApiScanConfig')) {
            project.extensions.create('deprecatedApiScanConfig', DeprecatedApiScanExtension)
        }
        def task = project.tasks.register('deprecatedApiScan', DeprecatedApiScanTask) {
            group = 'verification'
            description = 'Scans for selected deprecated Android API usage (Groovy plugin).'
        }
        project.gradle.projectsEvaluated {
            project.rootProject.allprojects.each { p ->
                def checkTask = p.tasks.findByName('check')
                if (checkTask) { checkTask.dependsOn(task) }
            }
        }
    }
}
