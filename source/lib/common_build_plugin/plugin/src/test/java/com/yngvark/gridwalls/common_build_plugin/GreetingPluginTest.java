package com.yngvark.gridwalls.common_build_plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Ignore;
import org.junit.Test;

public class GreetingPluginTest {
    @Ignore
    @Test
    public void plugin_should_add_task_to_project() {
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("com.yngvark.gridwalls.common_build_plugin");

        for (String s : project.getTasks().getNames()) {
            System.out.println(s);
        }

//        assertEquals(1, project.getTasks().size());
    }
}