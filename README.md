# Competitive Programming Gradle Plugin

Java only version of competitive programming toolkit, inspired by [Source: Manwe56/competitive-programming](https://github.com/Manwe56/competitive-programming)

> **Credit:** `BuildSolution.java`  - [Source: Manwe56](https://github.com/Manwe56/competitive-programming/blob/master/src/main/java/builder/FileBuilder.java)

## Why?
This project aims at sharing with the community those Java code I appreciate in the challenges and are helping you gain time, allowing you to develop in several files with a one click build, and sharing a common set of codes between challenges. Don't reinvent the wheel, focus on the subject!

## Dependency
 - JDK 1.8

## Features: 

- Create Problem file from template
- Reuse common utility class, avoid writing same core logic with ugly templates
- Problem parsing with [Competitive Companion](https://github.com/jmerle/competitive-companion)
- Manual input with local server hosted on http://localhost:7373
- Generate a single solution file to be uploaded to platform.


#### Initialize Problem file

Generate Problem Java Files:

Run command: 

```shell
./gradlew initProblem
```

Parse problem with [Competitive Companion](https://github.com/jmerle/competitive-companion) plugin with `7373` custom port enabled 

**Install Links:**

- [**Chrome** extension](https://chrome.google.com/webstore/detail/competitive-companion/cjnmckjndlpiamhfimnnjmnckgghkjbl)
- [**Firefox** add-on](https://addons.mozilla.org/en-US/firefox/addon/competitive-companion/)

**or** 

use http://localhost:7373 (for manual input eg Leetcode, Hackerearth Codemonk)

![Screenshot](https://res.cloudinary.com/dren4jgbp/image/upload/v1611277157/Screenshot_2021-01-22_Competitive_Programming_Gradle_Plugin_Input_Form_dtzcpo.png)

### Generated Files: 

`TwoSum.java`
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
`TwoSumTest.java`
```java
package platform.leetcode.problem0001;
import base.ISolution;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
*
* Test for TwoSum.java
* @author Saurabh Dutta<saurabh73>
* @see <a href="https://leetcode.com/problems/two-sum/">https://leetcode.com/problems/two-sum/</a> 
*
**/
public class TwoSumTest {

    private ByteArrayOutputStream buffer;

    @BeforeEach
    public void setup() {
        buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
    }
    
    @Test
    public void case1Test() throws Exception {
        // Input
        InputStream inputStream = this.getClass().getResourceAsStream("/leetcode/problem0001/input1.txt");
        // Output
        InputStream outPutStream = this.getClass().getResourceAsStream("/leetcode/problem0001/output1.txt");
        // Call Method Under Test
        ISolution problem = new TwoSum();
        problem.solve(inputStream);
        //Assertion
        String actual = buffer.toString().trim();
        String expected = IOUtils.toString(outPutStream, Charset.defaultCharset()).trim();
        assertEquals(expected, actual);
    }

    @Test
    public void case2Test() throws Exception {
        // Input
        InputStream inputStream = this.getClass().getResourceAsStream("/leetcode/problem0001/input2.txt");
        // Output
        InputStream outPutStream = this.getClass().getResourceAsStream("/leetcode/problem0001/output2.txt");
        // Call Method Under Test
        ISolution problem = new TwoSum();
        problem.solve(inputStream);
        //Assertion
        String actual = buffer.toString().trim();
        String expected =  IOUtils.toString(outPutStream, Charset.defaultCharset()).trim();
        assertEquals(expected, actual);
    }
    
    @Test
    public void case3Test() throws Exception {
        // Input
        InputStream inputStream = this.getClass().getResourceAsStream("/leetcode/problem0001/input3.txt");
        // Output
        InputStream outPutStream = this.getClass().getResourceAsStream("/leetcode/problem0001/output3.txt");
        // Call Method Under Test
        ISolution problem = new TwoSum();
        problem.solve(inputStream);
        //Assertion
        String actual = buffer.toString().trim();
        String expected =  IOUtils.toString(outPutStream, Charset.defaultCharset()).trim();
        assertEquals(expected, actual);
    }
    
    @AfterEach
    public void cleanup() {
        buffer.reset();
    }
}
```

#### Build Single Solution File

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

Note: The generated source is added to system clipboard (might be buggy in linux openjdk [Issue](https://bugs.openjdk.java.net/browse/JDK-8179547))

## code layout
- Common utility source code is in the `src/main/java` folder. 
- Problem files is generated in `/src/main/java` folder categorized by platforms.
- Junit Test case  is generated in `/src/test/java` folder categorized by platforms.
- Default Test input file will be in `src/test/resources/` folder categorized by platforms.
- Solution file is generated in `output` folder.

### Download Example
[competitive-programming-example.zip](https://github.com/saurabh73/competitive-programming-practice/archive/gradle-plugin-example-repo.zip)