package competitive.programming.models;

import lombok.Data;

import java.util.List;

@Data
public class ProblemInput {
    private String name;
    private String url;
    private Integer memoryLimit;
    private Integer timeLimit;
    private List<TestCaseInput> tests;
    private LanguageInput languages;
}
