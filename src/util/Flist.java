package util;

public class Flist<X>
{
  public Flist()
  {
  }

  public java.util.LinkedList<X> list(
      @SuppressWarnings("unchecked") X... args)
  {
    java.util.LinkedList<X> list = new java.util.LinkedList<X>();
    for (X arg : args)
      list.addLast(arg);
    return list;
  }
}
