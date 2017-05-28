# How to use

To import a build script from this library, use something like this

```$groovy
buildscript {
    def localMavenRepository = 'file://' + new File(System.getProperty('user.home'), '.mavenRepo').absolutePath

    repositories {
        maven {url localMavenRepository}
    }

    dependencies {
        classpath 'com.yngvark.gridwalls:common_buildscript:1.0.0'
    }
}

// Resources below are found in JAR from common_buildscript
afterEvaluate { project ->
    apply from: project.buildscript.classLoader.getResource('com/yngvark/gridwalls/common_buildscript/uploadArchives.gradle').toURI()
    apply from: project.buildscript.classLoader.getResource('com/yngvark/gridwalls/common_buildscript/installDist.gradle').toURI()
}

```

