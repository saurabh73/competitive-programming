package competitive.programming.utils;

import org.apache.commons.io.FileUtils;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsHtml;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.apache.commons.io.Charsets.UTF_8;

public class TkIndex  implements Take {
    @Override
    public  Response act(Request req) throws Exception {
        try {
            return new RsHtml(FileUtils.readFileToString(getIndexFileFromResource(), UTF_8));
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(fileNotFoundException.getMessage());
            return new RsHtml("<h1 style=\"color:red\">Hello World</h1>");
        }

    }

    private File getIndexFileFromResource() throws URISyntaxException, FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("public/index.html");
        if (resource == null) {
            throw new FileNotFoundException("file not found! public/index.html");
        } else {
            return new File(resource.toURI());
        }
    }
}
