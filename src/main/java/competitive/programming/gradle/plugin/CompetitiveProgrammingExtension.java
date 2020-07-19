package competitive.programming.gradle.plugin;

import lombok.Data;

@Data
public class CompetitiveProgrammingExtension {
    private String basePackage;
    private String author;
    private String githubUsername;
    private String baseSourcePath = "src/main/java";
}
