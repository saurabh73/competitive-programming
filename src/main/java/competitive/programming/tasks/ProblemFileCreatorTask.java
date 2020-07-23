package competitive.programming.tasks;

import com.google.common.base.CaseFormat;
import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.models.Platform;
import competitive.programming.utils.Constants;
import competitive.programming.utils.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * @author Saurabh Dutta <saurabh73>
 * Task method for initProblem Gradle task
 */
public class ProblemFileCreatorTask extends DefaultTask {

    private final TextIO textIO;
    private final CompetitiveProgrammingExtension extension;
    private final Project project;
    private final VelocityContext context;

    public ProblemFileCreatorTask() {
        super();
        setGroup(Constants.PLUGIN_TASK_GROUP);
        setDescription("Generate Problem Java Files");
        textIO = TextIoFactory.getTextIO();
        project = getProject();
        extension = getProject().getExtensions().findByType(CompetitiveProgrammingExtension.class);
        Utility.validatedExtension(extension);
        // Initialize Velocity
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        context = new VelocityContext();
        // add Variables to context
        context.put("StringUtils", StringUtils.class);
        context.put("NEWLINE", System.lineSeparator());
        context.put(Constants.AUTHOR, extension.getAuthor());
        context.put(Constants.GITHUB_USERNAME, extension.getGithubUsername());
        context.put(Constants.BASE_PACKAGE, extension.getBasePackage());
    }

    private void prepareAction() throws IOException {
        Path targetFilePath = Paths.get(Utility.getBasePath(project, extension), "base", "ISolution.java");
        if (!targetFilePath.toFile().exists()) {
            // Add Missing Directory
            if (!targetFilePath.toFile().getParentFile().exists()) {
                targetFilePath.toFile().getParentFile().mkdirs();
            }
            Utility.writeFileWithVelocityTemplate("/templates/base.vm", targetFilePath.toFile(), context);
        }
    }


    @TaskAction
    public void taskAction() throws IOException {
        try {
            this.prepareAction();
            Platform platform = this.textIO.newEnumInputReader(Platform.class).read("Select problem platform");
            switch (platform) {
                case LEETCODE:
                    leetcodeProblemGenerator();
                    break;
                case CODECHEF:
                    codechefProblemGenerator();
                default:
                    project.getLogger().error("Platform not supported");
                    throw new RuntimeException("Platform not supported");
            }
        }
        finally {
            this.textIO.dispose();
        }
    }

    private void codechefProblemGenerator() {
    }

    private void leetcodeProblemGenerator() throws IOException {
        String link = this.textIO.newStringInputReader().read("Enter problem link");
        String problemName = parseLeetcodeFileName(link);
        if (Character.isDigit(problemName.charAt(0))) {
            problemName = "Problem" + problemName;
        }
        generateProblemFile(Platform.LEETCODE, problemName, link);
    }


    private String parseLeetcodeFileName(String link) throws MalformedURLException {
        URL problemUrl = new URL(link);
        if (!problemUrl.getHost().equals("leetcode.com")) {
            throw new MalformedURLException("Domain not Leetcode");
        }
        String[] path = problemUrl.getPath().split("/");
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, path[path.length - 1].toLowerCase());
    }

    private void generateProblemFile(Platform platform, String name, String link) throws IOException {
        String platformName = platform.name().toLowerCase();
        String baseFileName = name + Constants.JAVA_EXTENSION;
        String platformPath = platform.name().toLowerCase();
        Path targetFilePath = Paths.get(Utility.getBasePath(project, extension), platformPath);
        String serialNo = getProblemSerialNumber(targetFilePath.toFile());

        context.put(Constants.PLATFORM, platformName);
        context.put(Constants.NAME, name);
        context.put(Constants.LINK, link);
        context.put("serial_no", serialNo);

        // Determine File name
        File problemFile = Paths.get(Utility.toAbsolutePath(targetFilePath), "problem" + serialNo, baseFileName).toFile();
        Utility.writeFileWithVelocityTemplate(Constants.TEMPLATES_PROBLEM, problemFile, context);

        // Generate Input Text File
        Paths.get(problemFile.getParentFile().getAbsolutePath(), "input.txt").toFile().createNewFile();
    }


    private String getProblemSerialNumber(File file) {
        OptionalInt maxNumber = OptionalInt.of(0);
        if (file.exists()) {
            maxNumber = Arrays.stream(Objects.requireNonNull(file.list((current, name) -> new File(current, name).isDirectory())))
                    .filter(folder -> folder.matches("^problem[\\d]+"))
                    .mapToInt(folder -> Integer.parseInt(folder.substring(7)))
                    .max();
        }
        return String.format("%04d", maxNumber.isPresent() ? maxNumber.getAsInt() + 1 : 1);
    }
}
