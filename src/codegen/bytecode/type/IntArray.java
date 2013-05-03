package codegen.bytecode.type;

import codegen.bytecode.Visitor;

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
