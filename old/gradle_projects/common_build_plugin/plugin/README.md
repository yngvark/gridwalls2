# Common Build Plugin

Acceptance requirements for this project is (could be used for testing):
* As a developer, I want to make an executable of my Gradle project, so that I can run my microservices
    * As a developer, I want to specify the Main class of my application
* As a developer, I want to publish my Gradle project, so that I can distribute libraries

# How to set up your build.gradle

```$groovy
buildscript {
    repositories {
        maven {
            def fetchMavenRepoUrl = { ->
                String mavenDir = new URL('https://bitbucket.org/yngvark/shared_config/raw/7cff02f13b49ea79e9aef6f47ab509bfcccd0a25/mavenRepoUrl').text
                "file://" + new File(System.getProperty("user.home"), mavenDir).getAbsolutePath()
            }
            url fetchMavenRepoUrl()
        }
    }

    dependencies {
        classpath 'com.yngvark.gridwalls:common_build_plugin:1.0.0'
    }
}

rootProject.ext.commonBuildPlugin = [
        applyApplicationPlugin: true, // You can omit this line if false. (Not exists is the same as false.)
        applyMavenPublishPlugin: true // Same goes here.
]
apply plugin: 'com.yngvark.gridwalls.common_build_plugin'

commonConfigApplicationPlugin { // Only needed if 'applyApplicationPlugin' is true above.
    mainClassName = 'myTest.Main'
}

model { // Only needed if 'applyMavenPublishPlugin' is true above. TODO doesn't work sigh.  
    commonConfig {
        componentToMavenPublish = components.java
    }
}
```