package util;

public class Label
{
  private int i;
  private static int count = 0;

  public Label()
  {
    i = count++;
  }

  @Override
  public String toString()
  {
    return "L_" + (Integer.toString(this.i));
  }
}
