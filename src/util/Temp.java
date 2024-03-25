package util;

public class Temp {
    private static int count = 0;

    private Temp() {
    }

    // Factory pattern
    public static String fresh() {
        return "x_" + (Temp.count++);
    }
}

