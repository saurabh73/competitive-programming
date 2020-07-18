package competitive.programming.utils;

import java.net.URL;

public class Utility {
    public static final String OUT_PRODUCTION_CLASSES = "/out/production/classes/";
    public static final String BUILD_CLASSES = "/build/classes/";

    public static String getRootPath(Class srcClass) {
        final String classLocation = srcClass.getName().replace('.', '/')+ Constants.CLASS_EXTENSION;
        final ClassLoader loader = srcClass.getClassLoader();
        if (loader != null) {
            final URL location = loader.getResource(classLocation);
            if (location != null) {
                String path = location.toString().replace("file:", "");
                if (path.contains(OUT_PRODUCTION_CLASSES)) {
                    path = path.split(OUT_PRODUCTION_CLASSES)[0];
                }
                else if(path.contains(BUILD_CLASSES)) {
                    path = path.split(BUILD_CLASSES)[0];
                }
                return path;
            }
        }
        return  null;
    }
}