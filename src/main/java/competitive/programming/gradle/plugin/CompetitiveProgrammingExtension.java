package competitive.programming.gradle.plugin;

import lombok.Data;

/**
 * @author Saurabh Dutta <saurabh73>
 */
@Data
public class CompetitiveProgrammingExtension {
    private String basePackage = "competitive.programming.practice";
    private String author;
    private String githubUsername;
    private String baseOutputPath = "output";
    private String baseSourcePath = "src/main/java";
    private String baseTestPath = "src/test/java";
    private String baseTestResourcePath = "src/test/resources";
    private int port = 6174;
}
