package competitive.programming.models;

/**
 * @author Saurabh Dutta <saurabh73>
 */
public enum Platform {
    LEETCODE, HACKEREARTH, CODECHEF, HACKERRANK;

    public static String getPattern() {
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append("package [\\w\\.]*(");
        for (Platform value : Platform.values()) {
            patternBuilder.append(value.name().toLowerCase()).append("|");
        }
        // delete last |
        patternBuilder.deleteCharAt(patternBuilder.length() - 1);
        patternBuilder.append(")\\.problem[\\d]*;");
        return patternBuilder.toString();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase() + ".com";
    }
}
