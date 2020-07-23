/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package competitive.programming.gradle.plugin;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * A simple unit test for the 'competitive.programming.gradle.plugin.greeting' plugin.
 */
public class CompetitiveProgrammingPluginTest {
    @Test
    @Ignore
    public void pluginRegistersATask() {
        // Create a test project and apply the plugin
        Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply("competitive.programming");

        // Verify the result
        assertNotNull(project.getTasks().findByName("greeting"));
    }
}
