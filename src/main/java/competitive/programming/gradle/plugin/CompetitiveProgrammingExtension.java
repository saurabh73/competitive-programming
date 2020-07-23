package competitive.programming.gradle.plugin;

import lombok.Data;

/**
 * @author Saurabh Dutta <saurabh73>
 */
@Data
public class CompetitiveProgrammingExtension {
    private String basePackage;
    private String author;
    private String githubUsername;
    private String baseOutputPath = "output";
    private String baseSourcePath = "src/main/java";
}
