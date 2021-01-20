package competitive.programming.models;

import lombok.Getter;

import java.net.URL;

/**
 * @author Saurabh Dutta <saurabh73>
 */
public enum Platform {
    // supported platforms
    LEETCODE("leetcode.com"),
    HACKEREARTH("hackerearth.com"),
    CODECHEF("codechef.com"),
    HACKERRANK("hackerrank.com"),
    DEFAULT(null);

    @Getter
    public final String host;
    Platform(String host) {
        this.host = host;
    }

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

    public static Platform getPlatform(URL problemUrl) {
        String host = problemUrl.getHost();
        for (Platform platform : values()) {
            if (platform.getHost() != null && platform.getHost().contains(host)){
                return platform;
            }
        }
        return DEFAULT;
    }
}
