package competitive.programming.tasks;

import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.models.ProblemInput;
import competitive.programming.utils.Constants;
import competitive.programming.utils.TakeProblemInput;
import competitive.programming.utils.TkIndex;
import competitive.programming.utils.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Saurabh Dutta <saurabh73>
 * Task method for initProblem Gradle task
 */
public class ProblemFileCreatorTask extends DefaultTask {

    private final CompetitiveProgrammingExtension extension;
    private final Project project;
    private final VelocityContext context;

    public ProblemFileCreatorTask() {
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
        context.put(Constants.AUTHOR, extension.getAuthor());
        context.put(Constants.GITHUB_USERNAME, extension.getGithubUsername());
        context.put(Constants.BASE_PACKAGE, extension.getBasePackage());
        context.put("end", 4);
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
        this.prepareAction();
        int port = extension.getPort();
        System.out.println("Competitive Plugin Enabled. " +
                "\nClick on Parse Task Button or input with http://localhost:7373");
        TakeProblemInput pluginInput = new TakeProblemInput();
        FkRegex pathMethod = new FkRegex("/", new TkFork(
                new FkMethods("POST", pluginInput),
                new FkMethods("GET", new TkIndex())
        ));
        FtBasic post = new FtBasic(new TkFork(pathMethod), port);
        post.start(new Exit.Not(() -> pluginInput.getProblemInput() != null));
        ProblemInput parsedInput = pluginInput.getProblemInput();

        System.out.println(parsedInput.getName());
        System.out.println(parsedInput.getLanguages().getJava().getTaskClass());

//        Scanner scanner = new Scanner(System.in);
//        URL problemUrl = takeLinkInput(scanner);
//        Platform platform = Platform.getPlatform(problemUrl);
//        if (platform.equals(Platform.LEETCODE)) {
//
//        } else {
//            boolean manualMode = !(extension.isUseCompetitiveCompanionPlugin());
//            if (!manualMode) {
//                int port = extension.getPort();
//                System.out.println("Competitive Plugin Enabled. Click on Parse Task Button");
//                TakeProblemInput pluginInput = new TakeProblemInput();
//                FkRegex pathMethod = new FkRegex("/", new TkFork(new FkMethods("POST", pluginInput)));
//                FtBasic post = new FtBasic(new TkFork(pathMethod), port);
//                post.start(new Exit.Or(new OneMinuteExit(System.currentTimeMillis()), () -> pluginInput.getProblemInput() != null));
//                ProblemInput parsedInput = pluginInput.getProblemInput();
//                if (parsedInput != null) {
//                    generateProblemFile(platform, parsedInput.getLanguages().getJava().getTaskClass(), parsedInput.getUrl());
//                } else {
//                    System.out.println("Parse Task Timeout. Switching to manual mode");
//                    manualMode = true;
//                }
//            }
//            if (manualMode) {
//
//            }
//        }
//
//
//        if (platform != null) {
//            boolean manualMode = !(extension.isUseCompetitiveCompanionPlugin() && !platform.equals(Platform.LEETCODE));
//            if (!manualMode) {
//
//            }
//
//            if (manualMode) {
//                switch (platform) {
//                    case LEETCODE:
//                    case HACKEREARTH:
//                    case HACKERRANK:
//                        commonProblemGenerator(scanner, platform);
//                        break;
//                    case CODECHEF:
//                        codechefProblemGenerator(scanner, platform);
//                        break;
//                    default:
//                        project.getLogger().error("Platform not supported");
//                        throw new RuntimeException("Platform not supported");
//                }
//            }
//        } else {
//            throw new RuntimeException("Invalid options selected");
//        }

    }


//    private Platform takePlatformInput(Scanner scanner) {
//        StringBuilder optionsBuilder = new StringBuilder("Select problem platform").append("\n");
//        for (Platform value : Platform.values()) {
//            optionsBuilder.append(value.ordinal() + 1).append(" ").append(value).append("\n");
//        }
//        System.out.println(optionsBuilder.toString().trim());
//        int platformInput = Integer.parseInt(scanner.nextLine());
//        for (Platform value : Platform.values()) {
//            if ((value.ordinal() + 1) == platformInput) {
//                return value;
//            }
//        }
//        return null;
//    }

//    private void codechefProblemGenerator(Scanner scanner, Platform platform) throws IOException {
//        System.out.println("Enter Problem Name:");
//        String problemName = scanner.nextLine();
//        problemName = takeProblemNameInput(problemName);
//        generateProblemFile(platform, problemName, problemUrl.toString());
//    }

//    private String takeProblemNameInput(String problemName) {
//        problemName = problemName.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", " ").replaceAll("[ ]+", "_");
//        System.out.println(problemName);
//        problemName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, problemName);
//        if (Character.isDigit(problemName.charAt(0))) {
//            problemName = "Problem" + problemName;
//        }
//        return problemName;
//    }

//    private URL takeLinkInput(Scanner scanner) throws IOException {
//        System.out.println("Enter Problem Link:");
//        String link = scanner.nextLine();
//        return new URL(link);
//    }


//    private void commonProblemGenerator(Scanner scanner, Platform platform) throws IOException {
//        URL problemUrl = takeLinkInput(scanner, platform);
//        String problemName = parseFileNameFromLink(problemUrl, platform.name().toLowerCase() + " problem");
//        problemName = takeProblemNameInput(problemName);
//        generateProblemFile(platform, problemName, problemUrl.toString());
//    }


//    private String parseFileNameFromLink(URL problemUrl, String defaultName) {
//        String[] path = problemUrl.getPath().split("/");
//        ArrayUtils.reverse(path);
//        for (String pathSegment : path) {
//            if (!pathSegment.matches("[\\d]+") && !pathSegment.equalsIgnoreCase("problem")) {
//                return pathSegment.toLowerCase();
//            }
//        }
//        return defaultName;
//    }

//    private void generateProblemFile(Platform platform, String name, String link) throws IOException {
//        String platformName = platform.name().toLowerCase();
//        String baseFileName = name + Constants.JAVA_EXTENSION;
//        String platformPath = platform.name().toLowerCase();
//        Path targetFilePath = Paths.get(Utility.getBasePath(project, extension), platformPath);
//        String serialNo = getProblemSerialNumber(targetFilePath.toFile());
//
//        context.put(Constants.PLATFORM, platformName);
//        context.put(Constants.NAME, name);
//        context.put(Constants.LINK, link);
//        context.put("serial_no", serialNo);
//
//        // Determine File name
//        File problemFile = Paths.get(Utility.toAbsolutePath(targetFilePath), "problem" + serialNo, baseFileName).toFile();
//        Utility.writeFileWithVelocityTemplate(Constants.TEMPLATES_PROBLEM, problemFile, context);
//
//        // Generate Input Text File
//        Paths.get(problemFile.getParentFile().getAbsolutePath(), "input.txt").toFile().createNewFile();
//    }


//    private String getProblemSerialNumber(File file) {
//        OptionalInt maxNumber = OptionalInt.of(0);
//        if (file.exists()) {
//            maxNumber = Arrays.stream(Objects.requireNonNull(file.list((current, name) -> new File(current, name).isDirectory())))
//                    .filter(folder -> folder.matches("^problem[\\d]+"))
//                    .mapToInt(folder -> Integer.parseInt(folder.substring(7)))
//                    .max();
//        }
//        return String.format("%04d", maxNumber.isPresent() ? maxNumber.getAsInt() + 1 : 1);
//    }
}
