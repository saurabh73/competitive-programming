package competitive.programming.tasks;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import competitive.programming.annotation.MethodSignature;
import competitive.programming.annotation.EntryAnnotationProcessor;
import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.utils.Constants;
import competitive.programming.utils.Utility;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import org.joor.CompileOptions;
import org.joor.Reflect;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Saurabh Dutta (saurabh73)
 * Task method for buildProblem Gradle task
 */
public class BuildSolutionTask extends DefaultTask {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String PATH_SEPARATOR = File.separator.equalsIgnoreCase("\\") ? "\\" : "/";
    private final CompetitiveProgrammingExtension extension;
    private final Project project;
    private final VelocityContext context;
    private final File outputFile;
    private final Formatter formatter;
    private final Logger logger;

    private final Set<String> imports = new HashSet<>();
    private final Set<String> knownFiles = new HashSet<>();
    private final Map<String, ClassCode> innerClasses = new LinkedHashMap<>();


    public BuildSolutionTask() {
        super();
        setGroup(Constants.PLUGIN_TASK_GROUP);
        setDescription("Build consolidated solution file");
        setDependsOn(Collections.singleton("build"));
        project = getProject();
        extension = getProject().getExtensions().findByType(CompetitiveProgrammingExtension.class);
        formatter = new Formatter();
        logger = project.getLogger();
        Utility.validatedExtension(extension);
        // Initialize Velocity
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        context = new VelocityContext();
        context.put(Constants.BASE_PACKAGE, extension.getBasePackage());
        String fileName = "Solution.java";
        this.outputFile = Paths.get(this.project.getProjectDir().getAbsolutePath(), extension.getBaseOutputPath(), fileName).toFile();

        // TODO: add more config properties
        // Properties properties = new Properties();
        // properties.load(ProblemBuildTask.class.getClassLoader().getResourceAsStream("config.properties"));

    }

    @TaskAction
    public void taskAction() throws Exception {
        // cleanup
        logger.info("Running Clean up");
        this.outputFile.delete();
        // take input
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter full class name to build");
        String inputClassName = scanner.nextLine();

        // process input
        URLClassLoader classLoader = Utility.getClassLoader(project);
        classLoader.loadClass(inputClassName);
        // String projectDir = this.project.getProjectDir().getAbsolutePath();
        // String baseSourcePath = this.extension.getBaseSourcePath();
        // File javaFile = Paths.get(projectDir, baseSourcePath, inputClassName.replaceAll("\\.", PATH_SEPARATOR) + Constants.JAVA_EXTENSION).toFile();

        String entryFile = this.buildMainFile(inputClassName);
        final ClassCode treated = this.processFile(entryFile);
        String formattedSource = this.createFormattedSource(treated);

        if (formattedSource.contains("@Entry")) {
            formattedSource = processEntryCode(formattedSource);
        }
        // copy to clipboard
        this.copyToClipBoard(formattedSource);
        // write source to file
        FileUtils.writeStringToFile(this.outputFile, formattedSource, StandardCharsets.UTF_8);
        // print path
        //System.out.println("Writing File to Path: " + this.outputFile.toURI());
        System.out.println("Writing File to Path: " + Paths.get(this.outputFile.getAbsolutePath()).toUri());
    }

    private String processEntryCode(String source) throws FormatterException {
        EntryAnnotationProcessor annotationProcessor = new EntryAnnotationProcessor();
        String supportedAnnotation = annotationProcessor.getSupportedAnnotationTypes().iterator().next();
        String className = this.outputFile.getName().replace(Constants.JAVA_EXTENSION, Constants.EMPTY_STRING);
        String finalSource = source;
        Utility.ignoringExc(() -> Reflect.compile(className, finalSource, new CompileOptions().processors(annotationProcessor)));
        if (!annotationProcessor.getMethodSignatures().isEmpty()) {
            source = source
                    .replace(Constants.IMPORT + supportedAnnotation + Constants.SEMI_COLON, Constants.EMPTY_STRING)
                    .replace("@" + Utility.getClassName(supportedAnnotation), Constants.EMPTY_STRING);
            StringBuilder sourceBuilder = new StringBuilder(source.substring(0, source.lastIndexOf(Constants.END_CURLY_BRACE)));
            MethodSignature signature = annotationProcessor.getMethodSignatures().get(0);


            String entryMethod = String.join(NEW_LINE,
                    String.format("%s {", signature),
                    String.format("%1$s solution = new %1$s();", context.get("className")),
                    signature.getMethodCall("solution"),
                    "}");
            sourceBuilder.append(entryMethod);
            sourceBuilder.append(Constants.END_CURLY_BRACE);
            return formatter.formatSource(sourceBuilder.toString());
        }
        return source;
    }


    private String createFormattedSource(ClassCode treated) throws Exception {
        final List<String> lines = new ArrayList<>();
        lines.addAll(imports);
        lines.addAll(treated.beforeClassContent);
        lines.add(Constants.CLASS + treated.getClassName() + Constants.START_CURLY_BRACE);
        for (final ClassCode innerClass : innerClasses.values()) {
            for (final String line : innerClass.beforeClassContent) {
                lines.add(Constants.TAB + line);
            }
            lines.add(Constants.TAB + Constants.PRIVATE_STATIC + innerClass.declaration() + Constants.START_CURLY_BRACE);
            for (final String line : innerClass.afterClassContent) {
                lines.add(Constants.TAB + line);
            }
        }
        lines.addAll(treated.afterClassContent);
        String source = String.join(NEW_LINE, lines);
        return formatter.formatSource(source);
    }

    private ClassCode processFile(String fileName) {
        logger.info("reading class content of {}", fileName);
        knownFiles.add(Utility.toAbsolutePath(Paths.get(fileName)));
        final List<String> fileContent = readFile(fileName);
        final ClassCode code = readFileContent(fileName, fileContent);
        readPackageClasses(fileName);
        return code;
    }

    private void readPackageClasses(String fileName) {
        final Path directory = Paths.get(fileName).getParent();
        // Do no include inner problems file
        DirectoryStream<Path> ds;
        try {
            ds = Files.newDirectoryStream(directory);
            for (final Path child : ds) {
                final String absolutePath = Utility.toAbsolutePath(child);
                if (!Files.isDirectory(child) && absolutePath.endsWith(Constants.JAVA_EXTENSION) && !knownFiles.contains(absolutePath)) {
                    innerClasses.put(absolutePath, processFile(absolutePath));
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }


    private String importToPath(String importStr) {
        final String className = importStr.substring(Constants.IMPORT.length()).replaceAll(Constants.SEMI_COLON, Constants.EMPTY_STRING);
        return Utility.toAbsolutePath(Paths.get(Constants.SRC_ROOT_JAVA + className.replaceAll("\\.", PATH_SEPARATOR) + Constants.JAVA_EXTENSION));

    }

    private boolean addLineToCode(final ClassCode code, boolean fileKeyWordRead, final String line) {
        if (!line.startsWith(Constants.PACKAGE)) {
            if (line.startsWith(Constants.IMPORT)) {
                final String importedClassPath = importToPath(line);
                if (!knownFiles.contains(importedClassPath)) {
                    if (Paths.get(importedClassPath).toFile().exists()) {
                        logger.debug("Non Standard inport {}", line);
                        innerClasses.put(importedClassPath, processFile(importedClassPath));
                    } else {
                        logger.debug("Standard inport {}", line);
                        imports.add(line);
                    }
                }
            } else {
                if (fileKeyWordRead) {
                    code.afterClassContent.add(line);
                } else {
                    if (line.contains(Constants.ABSTRACT_CLASS)) {
                        code.declaration(line, Constants.ABSTRACT_CLASS);
                        fileKeyWordRead = true;
                    } else if (line.contains(Constants.CLASS)) {
                        code.declaration(line, Constants.CLASS);
                        fileKeyWordRead = true;
                    } else if (line.contains(Constants.INTERFACE)) {
                        code.declaration(line, Constants.INTERFACE);
                        fileKeyWordRead = true;
                    } else if (line.contains(Constants.ENUM)) {
                        code.declaration(line, Constants.ENUM);
                        fileKeyWordRead = true;
                    } else {
                        code.beforeClassContent.add(line);
                    }
                }
            }
        }
        return fileKeyWordRead;
    }

    private ClassCode readFileContent(String fileName, List<String> fileContent) {
        final ClassCode code = new ClassCode(fileName);
        boolean fileKeyWordRead = false;
        boolean insideComment = false;
        for (final String line : fileContent) {
            final String trimmedLine = line.trim();
            if (insideComment) {
                if (trimmedLine.contains(Constants.END_COMMENT)) {
                    insideComment = false;
                    final String remainingCode = trimmedLine.substring(trimmedLine.indexOf(Constants.END_COMMENT) + Constants.END_COMMENT.length());
                    if (!remainingCode.trim().isEmpty()) {
                        fileKeyWordRead = addLineToCode(code, fileKeyWordRead, remainingCode);
                    }
                }
                // We can skip comments since generated file size might be
                // limited
            } else if (trimmedLine.isEmpty()) {
                // We don't need to empty lines
            } else if (trimmedLine.startsWith(Constants.LINE_COMMENT)) {
                // We can skip comments since generated file size might be limited
            } else if (trimmedLine.startsWith(Constants.START_COMMENT)) {
                // We can skip comments since generated file size might be limited
                if (!trimmedLine.contains(Constants.END_COMMENT)) {
                    insideComment = true;
                }
            } else {
                fileKeyWordRead = addLineToCode(code, fileKeyWordRead, line);
            }
        }
        return code;
    }

    private List<String> readFile(String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            System.err.println("Error while reading file " + fileName);
            throw new IllegalStateException("Unable to continue");
        }
    }

    private String buildMainFile(String className) throws Exception {
        context.put("importPath", className);
        context.put("className", Utility.getClassName(className));
        // Determine File name
        Utility.writeFileWithVelocityTemplate(Constants.TEMPLATE_SOLUTION, this.outputFile, context);
        return this.outputFile.getAbsolutePath();
    }

    private void copyToClipBoard(String formattedSource) {
        Utility.ignoringExc(() -> {
            Toolkit.getDefaultToolkit()
                    .getSystemClipboard()
                    .setContents(new StringSelection(formattedSource), null);
        });
    }

    // Static Class Code to be built
    @Data
    private static class ClassCode {
        private final String classFile;
        private final List<String> beforeClassContent;
        private final List<String> afterClassContent;
        private String className;
        private String keyword;

        public ClassCode(String classFile) {
            this.classFile = classFile;
            this.beforeClassContent = new ArrayList<>();
            this.afterClassContent = new ArrayList<>();
        }

        public String declaration() {
            return keyword + className;
        }

        public void declaration(String line, String keyword) {
            className = extractDeclaration(line, keyword);
            this.keyword = keyword;
        }

        private String extractDeclaration(String line, String str) {
            return line.substring(line.indexOf(str) + str.length()).replaceAll("\\{", Constants.EMPTY_STRING).trim();
        }
    }
}
