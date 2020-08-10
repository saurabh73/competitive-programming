package competitive.programming.utils;

import org.takes.http.Exit;

public class OneMinuteExit implements Exit {
    private final long startTime;
    public OneMinuteExit(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean ready() {
        System.out.println("Calculating timeout");
        long endTime = System.currentTimeMillis();
        return ( (endTime - startTime) >= 60000) ;
    }
}
