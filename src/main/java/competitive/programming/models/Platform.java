package competitive.programming.models;

public enum Platform {
    LEETCODE, HACKEREARTH, CODECHEF, HACKERRANK;

    @Override
    public String toString() {
        return this.name().toLowerCase()+".com";
    }
}
