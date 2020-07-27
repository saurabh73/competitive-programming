package competitive.programming.practice.hackerearth.problem0001;

import competitive.programming.practice.base.ISolution;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Saurabh Dutta<saurabh73>
 *
 * @see <a href="https://www.hackerearth.com/practice-onboarding/sum-of-two-1/">https://www.hackerearth.com/practice-onboarding/sum-of-two-1/</a>
 *
 **/
public class SumOfTwo implements ISolution {

    @Override
    public void solve(InputStream in) throws Exception {
        Scanner s = new Scanner(System.in);

        int num1 = s.nextInt(); // Get first integer
        int num2 = s.nextInt(); // Get second integer

        int sum = num1 + num2;

        // Print the sum
        System.out.println(sum);
        s.close();
    }
}
