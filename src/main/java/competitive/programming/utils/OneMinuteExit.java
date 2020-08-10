package competitive.programming.utils;

import org.takes.http.Exit;

public class OneMinuteExit implements Exit {
    private final long startTime;

    public OneMinuteExit(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean ready() {
        long endTime = System.currentTimeMillis();
        System.out.println("Calculating timeout " + ((endTime - startTime) / 1000) + "s");
        return ((endTime - startTime) >= 60000);
    }
}
