package competitive.programming.utils;

public interface Constants {

    String PLUGIN_TASK_GROUP = "Competitive Programming Tasks";

    interface FileCreator {
        String PLATFORM = "platform";
        String NAME = "name";
        String AUTHOR = "author";
        String LINK = "link";
    }

    String IMPORT = "import ";
    String SRC_ROOT_JAVA = "src/main/java/";
    String LINE_COMMENT = "//";
    String START_COMMENT = "/*";
    String END_COMMENT = "*/";
    String PRIVATE_STATIC = "private static";
    String TAB = "\t";
    String JAVA_EXTENSION = ".java";
    String NEW_LINE = "\n";
    String EMPTY_STRING = "";
    String COMMA = ",";
    String SOLUTION_JAVA_PATH = "/src/main/java/Solution.java";
    String PATH_SEPARATOR = "/";
    String CODING_PROBLEMS_SRC = "/coding-problems/src/main/java/";
    String ROOT_PACKAGE = "/competitive-programming/";
    String CODING_PROBLEM_PACKAGE = "/competitive-programming/coding-problems/";
    String PACKAGE = "package";
    String CLASS = "class";
    String ABSTRACT_CLASS = "abstract class";
    String PUBLIC_CLASS = "public class";
    String INTERFACE = "interface";
    String ENUM = "enum";
    String SEMI_COLON = ";";
    String ANNOTATION_ENTRY = "@Entry";
    String SPACE = " ";
    String CLASS_EXTENSION = ".class";
    String START_CURLY_BRACE = "{";
    String TEMPLATES_SOLUTION = "/templates/solution.vm";
    String TEMPLATES_PROBLEM = "/templates/%s/problem.vm";
}
