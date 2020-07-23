package competitive.programming.tasks;

import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.utils.Constants;
import competitive.programming.utils.Utility;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Saurabh Dutta <saurabh73>
 */
public class ProblemExecutorTask extends DefaultTask {
    private final CompetitiveProgrammingExtension extension;
    private final Project project;

    public ProblemExecutorTask() {
        super();
        setGroup(Constants.PLUGIN_TASK_GROUP);
        setDescription("Run solution with input file");
        setDependsOn(Collections.singleton("build"));
        project = getProject();
        extension = getProject().getExtensions().findByType(CompetitiveProgrammingExtension.class);
        Utility.validatedExtension(extension);
    }


    @TaskAction
    public void taskAction() throws Exception {
        // take input
        Map<String, String> inputMap = takeUserInput();

        String inputClassName = inputMap.get("INPUT_CLASS");
        String testInputFileName = inputMap.get("INPUT_FILE");
        // process input
        String projectDir = this.project.getProjectDir().getAbsolutePath();
        String baseSourcePath = this.extension.getBaseSourcePath();
        File javaFileDirePath = Paths.get(projectDir, baseSourcePath, inputClassName.replaceAll("\\.", File.separator) + Constants.JAVA_EXTENSION).toFile().getParentFile();

        InputStream inputStream = new FileInputStream(Paths.get(javaFileDirePath.getAbsolutePath(), testInputFileName).toFile());
        URLClassLoader classLoader = Utility.getClassLoader(project);
        runExecutor(classLoader.loadClass(inputClassName), inputStream);
    }

    public Map<String, String> takeUserInput() {
        Map<String, String> inputMap = new HashMap<>();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full class name to execute");
        inputMap.put("INPUT_CLASS", scanner.nextLine());
        System.out.println("Test input file [default: input.txt]");
        String testInputFileName = scanner.nextLine();
        testInputFileName = StringUtils.isEmpty(testInputFileName) ? "input.txt" : testInputFileName;
        inputMap.put("INPUT_FILE", testInputFileName);
        return inputMap;
    }

    private void runExecutor(Class solutionClass, InputStream input) {
        ExecutorService executors = Executors.newSingleThreadExecutor();
        try {
            Callable<Object> methodCall = () -> {
                long startTime = System.nanoTime();
                Method solveMethod = solutionClass.getMethod("solve", InputStream.class);
                Constructor constructor = solutionClass.getConstructor();
                Object instance = constructor.newInstance();
                solveMethod.invoke(instance, input);
                double elapsedTime = ((double) (System.nanoTime() - startTime) / (double) Constants.NANO_SECOND_FACTOR);
                System.out.println(String.format("Time Taken: %.6f", elapsedTime) + " seconds");
                return null;
            };
            executors.submit(methodCall);
        }
        finally {
            executors.shutdownNow();
        }
    }
}
