package competitive.programming.practice.commons.io.output;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

public class OutputWriter {
    private final PrintWriter writer;

    public OutputWriter(OutputStream outputStream) {
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)));
    }

    public OutputWriter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }

    public void print(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(objects[i]);
        }
    }

    public void print(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(array[i]);
        }
    }

    public void print(double[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(array[i]);
        }
    }

    public void print(long[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(array[i]);
        }
    }

    public void print(char[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(array[i]);
        }
    }

    public void print(String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                writer.print(' ');
            }
            writer.print(array[i]);
        }
    }

    public void print(int[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            println(matrix[i]);
        }
    }

    public void print(double[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            println(matrix[i]);
        }
    }

    public void print(long[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            println(matrix[i]);
        }
    }

    public void print(char[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            println(matrix[i]);
        }
    }

    public void print(String[][] matrix) {
        for (int i = 0; i < matrix.length; ++i) {
            println(matrix[i]);
        }
    }

    public void println(int[] array) {
        print(array);
        writer.println();
    }

    public void println(double[] array) {
        print(array);
        writer.println();
    }

    public void println(long[] array) {
        print(array);
        writer.println();
    }

    public void println(String[] array) {
        print(array);
        writer.println();
    }

    public void println() {
        writer.println();
    }

    public void println(Object... objects) {
        print(objects);
        writer.println();
    }

    public void print(char i) {
        writer.print(i);
    }

    public void println(char i) {
        writer.println(i);
    }

    public void println(char[] array) {
        writer.println(array);
    }

    public void printf(String format, Object... objects) {
        writer.printf(format, objects);
    }

    public void close() {
        writer.close();
    }

    public void flush() {
        writer.flush();
    }

    public void print(long i) {
        writer.print(i);
    }

    public void println(long i) {
        writer.println(i);
    }

    public void print(int i) {
        writer.print(i);
    }

    public void println(int i) {
        writer.println(i);
    }

    public void separateLines(int[] array) {
        for (int i : array) {
            println(i);
        }
    }
}
