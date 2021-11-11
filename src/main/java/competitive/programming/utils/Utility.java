package competitive.programming.utils;

import com.google.common.net.InternetDomainName;
import competitive.programming.gradle.plugin.CompetitiveProgrammingExtension;
import competitive.programming.models.RunnableExc;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author Saurabh Dutta (saurabh73)
 * Utility Class
 */
public class Utility {

    public static void validatedExtension(CompetitiveProgrammingExtension extension) {
        if (extension == null) {
            throw new IllegalStateException("Gradle Plugin not initialized");
        }
        String basePackage = extension.getBasePackage();

        if (basePackage == null || !basePackage.matches(Constants.PACKAGE_REGEX)) {
            throw new IllegalStateException("Invalid Base Package");
        }
        if (StringUtils.isEmpty(extension.getAuthor())) {
            throw new IllegalStateException("Invalid Author Name");
        }
    }

    public static String readProperty(Properties properties, String propertyName) {
        return (String) properties.getOrDefault(propertyName, Constants.EMPTY_STRING);
    }

    public static URLClassLoader getClassLoader(Project project) throws MalformedURLException {
        File buildDir = new File(project.getBuildDir(), String.format(Constants.BUILD_PATH, File.separator));
        return new URLClassLoader(new URL[]{buildDir.toURI().toURL()});
    }

    public static void writeFileWithVelocityTemplate(String templateFile, File file, VelocityContext context) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        System.out.println("Writing File to Path: " + path.toUri());
        // generate structure
        file.getParentFile().mkdirs();
        Writer writer = new FileWriter(file);
        Velocity.mergeTemplate(templateFile, StandardCharsets.UTF_8.displayName(), context, writer);
        writer.flush();
        writer.close();
    }

    public static String getBasePath(Project project, CompetitiveProgrammingExtension extension) {
        String projectDir = project.getProjectDir().getAbsolutePath();
        String baseSourcePath = extension.getBaseSourcePath();
        String basePackagePath = extension.getBasePackage().replaceAll("\\.", File.separator);
        return Paths.get(projectDir, baseSourcePath, basePackagePath).toFile().getAbsolutePath();
    }

    public static String getBaseTestPath(Project project, CompetitiveProgrammingExtension extension) {
        String projectDir = project.getProjectDir().getAbsolutePath();
        String baseTestPath = extension.getBaseTestPath();
        String basePackagePath = extension.getBasePackage().replaceAll("\\.", File.separator);
        return Paths.get(projectDir, baseTestPath, basePackagePath).toFile().getAbsolutePath();
    }

    public static String toAbsolutePath(Path path) {
        return path.toFile().getAbsolutePath();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String getPlatform(URL problemUrl) {
        String host = problemUrl.getHost();
        try {
            return InternetDomainName.from(host).topPrivateDomain().toString().split("\\.")[0];
        } catch (Exception ex) {
            return "misc";
        }
    }

    public static void ignoringExc(RunnableExc r) {
        try {
            r.run();
        } catch (Exception e) {
            // ignore
        }
    }

    public static String getClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
    }

}