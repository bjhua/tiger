package util;

public class Temp
{
  private static int count = 0;

  private Temp()
  {
  }

  // Factory pattern
  public static String next()
  {
    return "x_" + (Temp.count++);
  }
}
