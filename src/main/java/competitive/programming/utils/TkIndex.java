package competitive.programming.utils;

import org.apache.commons.io.IOUtils;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsHtml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class TkIndex implements Take {
    @Override
    public Response act(Request req) throws Exception {
        return new RsHtml(readIndexFileFromResource());
    }

    private String readIndexFileFromResource() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream in = classLoader.getResourceAsStream("public/index.html");
        if (in == null) {
            throw new FileNotFoundException("file not found! public/index.html");
        } else {
            return IOUtils.toString(in, Charset.defaultCharset());
        }
    }
}
