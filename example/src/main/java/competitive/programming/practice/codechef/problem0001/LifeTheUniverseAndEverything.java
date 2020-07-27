package competitive.programming.practice.codechef.problem0001;

import competitive.programming.practice.base.ISolution;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Saurabh Dutta<saurabh73>
 *
 * @see <a href="https://www.codechef.com/problems/TEST">https://www.codechef.com/problems/TEST</a>
 *
 **/
public class LifeTheUniverseAndEverything implements ISolution {

    @Override
    public void solve(InputStream in) throws Exception {
        Scanner scanner = new Scanner(in);
        while (true) {
        int a = scanner.nextInt();
        if (a != 42) System.out.println(a); else System.exit(0);
        }
    }
}
