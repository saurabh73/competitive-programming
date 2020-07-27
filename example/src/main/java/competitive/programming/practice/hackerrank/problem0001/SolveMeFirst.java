package competitive.programming.practice.hackerrank.problem0001;

import competitive.programming.practice.base.ISolution;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Saurabh Dutta<saurabh73>
 *
 * @see <a href="https://www.hackerrank.com/challenges/solve-me-first/problem">https://www.hackerrank.com/challenges/solve-me-first/problem</a>
 *
 **/
public class SolveMeFirst implements ISolution {

  @Override
  public void solve(InputStream in) throws Exception {
    Scanner scan = new Scanner(in);
    int a;
    a = scan.nextInt();
    int b;
    b = scan.nextInt();
    int sum;
    sum = solveMeFirst(a, b);
    System.out.println(sum);
  }

  int solveMeFirst(int a, int b) {
    return a + b;
  }
}
