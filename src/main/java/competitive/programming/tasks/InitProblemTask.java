package competitive.programming.tasks;

import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.models.ProblemInput;
import competitive.programming.utils.Constants;
import competitive.programming.utils.MarkdownUtility;
import competitive.programming.utils.TakeProblemInput;
import competitive.programming.utils.TkIndex;
import competitive.programming.utils.Utility;
import net.steppschuh.markdowngenerator.table.Table;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * @author Saurabh Dutta (saurabh73)
 * Task method for initProblem Gradle task
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class InitProblemTask extends DefaultTask {

    private final CompetitiveProgrammingExtension extension;
    private final Project project;
    private final VelocityContext context;

    public InitProblemTask() {
        super();
        setGroup(Constants.PLUGIN_TASK_GROUP);
        setDescription("Generate Problem Java Files");
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
        context.put("TAB", "\t");
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
            Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_BASE_SOLUTION, targetFilePath.toFile(), context);
        }
        Path targetTestFilePath = Paths.get(Utility.getBaseTestPath(project, extension), "base", "BaseTest.java");
        if (!targetTestFilePath.toFile().exists()) {
            // Add Missing Directory
            if (!targetTestFilePath.toFile().getParentFile().exists()) {
                targetTestFilePath.toFile().getParentFile().mkdirs();
            }
            Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_BASE_TEST, targetTestFilePath.toFile(), context);
        }
        Path targetMarkdownFile = Paths.get(project.getProjectDir().getAbsolutePath(), "solutions.md");
        if (!targetMarkdownFile.toFile().exists()) {
            context.put("markdownTable", MarkdownUtility.defaultTable());
            Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_MARKDOWN, targetMarkdownFile.toFile(), context);
        }
    }


    @TaskAction
    public void taskAction() throws IOException {
        this.prepareAction();
        // Start Server
        int port = extension.getPort();
        System.out.printf("Competitive Plugin Enabled.\nClick on Parse Task Button or input with http://localhost:%d\n", port);
        TakeProblemInput pluginInput = new TakeProblemInput();
        FkRegex pathMethod = new FkRegex("/", new TkFork(
                new FkMethods("POST", pluginInput),
                new FkMethods("GET", new TkIndex())
        ));
        FtBasic post = new FtBasic(new TkFork(pathMethod), extension.getPort());
        post.start(new Exit.Not(() -> pluginInput.getProblemInput() == null));

        // Take I
        ProblemInput parsedInput = pluginInput.getProblemInput();
        URL link = parsedInput.getUrl();
        String platform = Utility.getPlatform(link);

        Path targetFilePath = Paths.get(Utility.getBasePath(project, extension), "platform", platform);
        String serialNo = getProblemSerialNumber(targetFilePath.toFile());

        // Update Context
        context.put(Constants.PLATFORM, platform);
        context.put(Constants.LINK, link.toString());
        context.put(Constants.SERIAL_NO, serialNo);

        // Generate Problem
        Path problemFile = generateProblemFile(parsedInput, platform, serialNo);
        // Generate Markdown File
        updateMarkdownFile(parsedInput, platform, problemFile);

        // Generate Test Files
        Path baseTargetTestResourceFile = prepareTestResource(platform, serialNo);
        for (int i = 0; i < parsedInput.getTests().size(); i++) {
            generateTestResource(baseTargetTestResourceFile, "input", i + 1, parsedInput.getTests().get(i).getInput());
            generateTestResource(baseTargetTestResourceFile, "output", i + 1, parsedInput.getTests().get(i).getOutput());
        }
        generateTestFile(platform, serialNo, parsedInput.getLanguages().getJava().getTaskClass(), parsedInput.getTests().size());

    }


    private Path prepareTestResource(String platform, String serialNo) throws IOException {
        String projectDir = project.getProjectDir().getAbsolutePath();
        String baseTestResourcePath = extension.getBaseTestResourcePath();
        Path baseTargetTestResourceFile = Paths.get(projectDir, baseTestResourcePath, platform, "problem" + serialNo);
        boolean resourceExists = baseTargetTestResourceFile.toFile().exists();
        if (!resourceExists) {
            resourceExists = baseTargetTestResourceFile.toFile().mkdirs();
        }
        if (!resourceExists) {
            throw new IOException("Cannot create path: " + baseTargetTestResourceFile.toUri());
        }
        return baseTargetTestResourceFile;
    }

    private void generateTestResource(Path baseTargetTestFile, String fileBaseName, int index, String data) throws IOException {
        Path path = Paths.get(Utility.toAbsolutePath(baseTargetTestFile), fileBaseName + index + Constants.TXT_EXTENSION);
        System.out.println("Writing File to Path: " + path.toUri());
        Files.write(path, data.getBytes(StandardCharsets.UTF_8));
    }

    private Path generateProblemFile(ProblemInput parsedInput, String platform, String serialNo) throws IOException {
        String name = parsedInput.getLanguages().getJava().getTaskClass();
        String baseFileName = name + Constants.JAVA_EXTENSION;
        context.put(Constants.NAME, name);
        // Generate Problem File
        Path targetFilePath = Paths.get(Utility.getBasePath(project, extension), "platform", platform);
        Path problemFile = Paths.get(Utility.toAbsolutePath(targetFilePath), "problem" + serialNo, baseFileName);
        Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_PROBLEM, problemFile.toFile(), context);
        return problemFile;
    }

    private void updateMarkdownFile(ProblemInput parsedInput, String platform, Path problemFile) throws IOException {
        Path targetMarkdownFile = Paths.get(project.getProjectDir().getAbsolutePath(), "solutions.md");
        final Table.Builder tableBuilder = parseMarkdownFile(targetMarkdownFile);

        // Add to Solutions.md
        String baseFileName = parsedInput.getLanguages().getJava().getTaskClass() + Constants.JAVA_EXTENSION;
        String problemLink = String.format("[%s](%s)", parsedInput.getName(), parsedInput.getUrl().toString());
        String fileLink = String.format("[%s](%s)", baseFileName, problemFile.relativize(Paths.get(project.getProjectDir().getAbsolutePath())));
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        tableBuilder.addRow(problemLink, fileLink, platform, today, "", "", "");

        System.out.println(tableBuilder.build());
        context.put("markdownTable", tableBuilder.build().toString());
        // Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_MARKDOWN, targetMarkdownFile.toFile(), context);

    }


    private Table.Builder parseMarkdownFile(Path targetMarkdownFile) {
        String markdownTable = MarkdownUtility.parseMarkdownTable(targetMarkdownFile);
        List<Extension> extensions = Collections.singletonList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node table = parser.parse(markdownTable).getFirstChild();

        // Parse Table Head
        Table.Builder tableBuilder = new Table.Builder();
        TableHead tableHead = (TableHead) table.getFirstChild();
        MarkdownUtility.paresTableHead(tableHead, tableBuilder);
        // Parse Table Body
        Node tableBody = table.getLastChild();
        MarkdownUtility.parseTableBody(tableBody, tableBuilder);

        return tableBuilder;
    }


    private void generateTestFile(String platform, String serialNo, String name, int size) throws IOException {
        String baseFileName = name + "Test" + Constants.JAVA_EXTENSION;
        context.put(Constants.NAME, name);
        context.put(Constants.SIZE, size);

        // Generate Test File
        Path targetFilePath = Paths.get(Utility.getBaseTestPath(project, extension), "platform", platform);
        File testFile = Paths.get(Utility.toAbsolutePath(targetFilePath), "problem" + serialNo, baseFileName).toFile();
        Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_TEST, testFile, context);
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
