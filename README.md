# Competitive Programming Gradle Plugin

Java only version of competitive programming toolkit, inspired by [Source: Manwe56/competitive-programming](https://github.com/Manwe56/competitive-programming)

> **Credit:** `ProblemBuildTask.java`  - [Source: Manwe56](https://github.com/Manwe56/competitive-programming/blob/master/src/main/java/builder/FileBuilder.java)

## Why?
This project aims at sharing with the community those Java code I appreciate in the challenges and are helping you gain time, allowing you to develop in several files with a one click build, and sharing a common set of codes between challenges. Don't reinvent the wheel, focus on the subject!

## Dependency
 - JDK 1.8

## Features: 

- Create Problem file from template
- Test problem logic with timeout and input file configuration
- Generate solution file to be uploaded to platform.


#### Initialize Problem file

Generate Problem Java Files:

Run command: 

```shell
./gradlew initProblem
```

outputs interactive terminal input:
```shell
> Enter Problem Platform: Leetcode
> Enter Problem Link: https://leetcode.com/problems/two-sum/
File Path: /workbench/competitive-programming/coding-problems/src/main/java/platform/leetcode/problem0001/TwoSum.java
```


Generated File: `TwoSum.java`

```java
package platform.leetcode.problem0001;
import base.ISolution;
import java.io.InputStream;

/**
*
* @author Saurabh Dutta
* @see <a href="https://leetcode.com/problems/two-sum/">https://leetcode.com/problems/two-sum/</a> 
*
**/
public class TwoSum implements ISolution {

    @Override
    public void solve(InputStream in) {
        //TODO: Implement Solution
    }
}
```


#### Test locally with timeout and input file

Run command: 
```shell
./gradlew executeWithTimeout -q --console=plain
```

outputs interactive terminal input:
```shell
> Enter class name to execute: platform.leetcode.problem0001.TwoSum
> Enter input file path: <Defaults to /src/main/resources/input/input.txt>
Time Taken: 0.000003 seconds
```



#### Test locally with timeout and input file

Run command: 
```shell
./gradlew buildSolution -q --console=plain
```
outputs interactive terminal input:
```shell
> Enter class name to execute: platform.leetcode.problem0001.TwoSum
File Path: /workbench/competitive-programming/src/main/java/Solution.java
reading class content of /workbench/competitive-programming/src/main/java/Solution.java
reading class content of /workbench/competitive-programming/coding-problems/src/main/java/base/ISolution.java
Standard import:import java.io.InputStream;
reading class content of /workbench/competitive-programming/coding-problems/src/main/java/platform/leetcode/problem0001/TwoSum.java
OUTPUT PATH : /workbench/competitive-programming/output/Solution.java
Deleting path: /workbench/competitive-programming/src/main/java/Solution.java
```

Generated File: `Solution.java`
```java
import java.io.InputStream;
class Solution {
	private static interface ISolution {
		default int getTimeoutInSeconds() {
			return 2;
		}
		void solve(InputStream in);
	}
	private static class TwoSum implements ISolution {
		@Override
		public void solve(InputStream in) {
		}
	}
	public static void main(String[] args) {
		ISolution solution = new Test();
		solution.solve(System.in);
	}
}
```

## code layout

- The utility source code is in the `src/main/java` folder. 
- Problem files is generated in `coding-problems/src/main/java` folder categorized by platforms.
- Default Test input file will be in `src/main/resources/input` folder.
- Solution file is generated in `output` folder.

### Download Example
[competitive-programming-example.zip](https://res.cloudinary.com/dren4jgbp/raw/upload/v1595816744/competitive-programming-example.zip)