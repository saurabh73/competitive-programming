package competitive.programming.practice.commons.utils;

import java.util.Arrays;

public class Utility {

    public static String[] getStringArray(String line) {
        return Arrays.stream(
                line.replace("[", "")
                        .replace("]", "")
                        .replaceAll("\"", "")
                        .split(",")
        ).map(String::trim).toArray(String[]::new);
    }

    public static int[] getIntArray(String line) {
        return Arrays.stream(getStringArray(line)).mapToInt(Integer::parseInt).toArray();
    }

}
