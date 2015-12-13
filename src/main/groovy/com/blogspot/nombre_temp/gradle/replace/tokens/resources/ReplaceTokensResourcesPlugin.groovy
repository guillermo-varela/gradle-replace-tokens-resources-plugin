package com.blogspot.nombre_temp.gradle.replace.tokens.resources

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * This plugin for Gradle adds the [ReplaceTokens] feature from Ant to the [processResources] Gradle task, which is used during the build process for projects
 * with code to be executed in the [JVM] (like Java and Groovy).
 */
class ReplaceTokensResourcesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        project.afterEvaluate { p ->
            def processResourcesTask = project.tasks.findByName('processResources')

            if (processResourcesTask == null) {
                println "processResources task not found. Make sure to apply the plugin that has it before (for example 'java' or 'groovy')."
            } else {
                def defaultEnvironment = project.properties.defaultEnvironment ? project.properties.defaultEnvironment : 'local'
                def environment = System.properties.env ? System.properties.env : defaultEnvironment
                def configEnvironmentFolder = project.properties.configEnvironmentFolder ? project.properties.configEnvironmentFolder : 'config'

                if (!project.file(configEnvironmentFolder).exists()) {
                    throw new InvalidUserDataException("Configuration environment folder not found: $configEnvironmentFolder")
                }
                if (!project.file("$configEnvironmentFolder/$environment").exists()) {
                    throw new InvalidUserDataException("Environment folder not found: $configEnvironmentFolder/$environment")
                }

                // Executed only if the configuration files or the system properties changed from previous execution
                processResourcesTask.inputs.dir project.file("$configEnvironmentFolder/$environment")
                processResourcesTask.inputs.properties System.properties

                processResourcesTask.doFirst {
                    println "***********************************************************"
                    println "Using environment: $environment"
                    println "***********************************************************"

                    // Gets configuration values according to the environment being built
                    def environmentProperties = new Properties()

                    project.file("$configEnvironmentFolder/$environment").listFiles().each { file ->
                        file.withInputStream{
                            environmentProperties.load(it);
                        }
                    }

                    // Overwrites the values in the file with the ones given from command line arguments -Dkey
                    System.properties.each { key, value ->
                        if (environmentProperties.containsKey(key)) {
                            environmentProperties.put(key, value)
                        }
                    }

                    // Replaces all values with @name@ in the resources folder(s) files with the ones in "environmentProperties"
                    filter(ReplaceTokens, tokens: environmentProperties)
                }
            }
        }
    }
}
