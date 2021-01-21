package competitive.programming.models;

import lombok.Data;

import java.net.URL;
import java.util.List;

@Data
public class ProblemInput {
    private String name;
    private URL url;
    private Integer memoryLimit;
    private Integer timeLimit;
    private List<TestCaseInput> tests;
    private LanguageInput languages;
}
