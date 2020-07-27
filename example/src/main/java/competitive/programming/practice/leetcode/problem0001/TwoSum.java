package competitive.programming.practice.leetcode.problem0001;

import competitive.programming.annotation.leetcode.Entry;
import competitive.programming.practice.base.ISolution;
import competitive.programming.practice.commons.utils.Utility;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
/**
 *
 * @author Saurabh Dutta<saurabh73>
 *
 * @see <a href="https://leetcode.com/problems/two-sum/">https://leetcode.com/problems/two-sum/</a>
 *
 **/
public class TwoSum implements ISolution {

    @Override
    public void solve(InputStream in) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        int[] nums = Utility.getIntArray(reader.readLine());
        int target = Integer.parseInt(reader.readLine());

        int[] ans = twoSum(nums, target);
        System.out.println(ans[0]+" "+ ans[1]);
    }

    @Entry
    public int[] twoSum(int[] nums, int target) {
        for(int i=0; i<nums.length -1 ; i++) {
            for (int j=i+1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
