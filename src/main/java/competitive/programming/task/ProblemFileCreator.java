package competitive.programming.task;

import com.google.common.base.CaseFormat;
import competitive.programming.models.Platform;
import competitive.programming.utils.Constants;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.OptionalInt;

@Data
public class ProblemFileCreator {

    private Platform platform;
    private String author;

    @Getter(AccessLevel.NONE)
    private final TextIO textIO;

    public ProblemFileCreator(String author) {
        this.author = author;

        this.textIO = TextIoFactory.getTextIO();
    }

    public void takePlatformInput() {
        this.platform = this.textIO.newEnumInputReader(Platform.class).read("Select Problem Platform");
    }

    public void generateLeetcodeFile(){
        String link = Constants.EMPTY_STRING;
        try {
            link = this.textIO.newStringInputReader().read("Enter Problem Link");
            String problemName = parseLeetcodeFileName(link);
            generateProblemFile(Platform.LEETCODE, problemName, link, null);
            System.out.println(problemName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid URL "+link);
        } catch (IOException e) {
            throw new RuntimeException("Unable to Generate file");
        } finally {
            this.textIO.dispose();
        }
    }

    private String parseLeetcodeFileName(String link) throws MalformedURLException {
        URL problemUrl = new URL(link);
        if (!problemUrl.getHost().equals("leetcode.com")) {
            throw new MalformedURLException("Domain not Leetcode");
        }
        String[] path = problemUrl.getPath().split("/");
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL, path[path.length-1].toLowerCase());
    }

    private void generateProblemFile(Platform platform, String name, String link, String author) throws IOException {
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        Velocity.init();
        VelocityContext context = new VelocityContext();
        context.put("StringUtils", StringUtils.class);
        context.put(Constants.FileCreator.PLATFORM, platform.name().toLowerCase());
        context.put(Constants.FileCreator.NAME, name);
        context.put(Constants.FileCreator.AUTHOR, author);
        context.put(Constants.FileCreator.LINK, link);
        context.put("basePackage", "dev.saurabhdutta.competitive.programming.practice");
        context.put("NEWLINE", "\r\n");

        String srcPath = "/home/saurabh/workspace/github/competitive-programming-practice/src/main/java/dev/saurabhdutta/competitive/programming/practice";

        StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder
                .append(srcPath)
                .append(Constants.FileCreator.PLATFORM)
                .append(Constants.PATH_SEPARATOR)
                .append(platform);

        String serialNo = getProblemSerialNumber(filePathBuilder.toString());
        context.put("serial_no", serialNo);

        filePathBuilder
                .append(Constants.PATH_SEPARATOR)
                .append("problem")
                .append(serialNo)
                .append(Constants.PATH_SEPARATOR)
                .append(name)
                .append(Constants.JAVA_EXTENSION);

        System.out.println("File Path: "+ filePathBuilder);

        // Determine File name
        File problemFile = new File(filePathBuilder.toString());
        // generate structure
        problemFile.getParentFile().mkdirs();

        Writer writer = new FileWriter(problemFile);

        Velocity.mergeTemplate(String.format(Constants.TEMPLATES_PROBLEM, platform.name().toLowerCase()), StandardCharsets.UTF_8.displayName(), context, writer);
        writer.flush();
        writer.close();
    }


    private String getProblemSerialNumber(String folderPath) {
        File file = new File(folderPath);
        OptionalInt maxNumber = OptionalInt.of(0);
        if (file.exists()) {
            maxNumber = Arrays.stream(Objects.requireNonNull(file.list((current, name) -> new File(current, name).isDirectory())))
                    .filter(folder -> folder.matches("^problem[\\d]+"))
                    .mapToInt(folder -> Integer.parseInt(folder.substring(7)))
                    .max();
        }
        return String.format("%04d", maxNumber.isPresent() ? maxNumber.getAsInt() + 1: 1);
    }
}
