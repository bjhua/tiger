package codegen.dalvik.type;

import codegen.dalvik.Visitor;

public class IntArray extends T
{
  public IntArray()
  {
  }

  @Override
  public String toString()
  {
    return "@int[]";
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
