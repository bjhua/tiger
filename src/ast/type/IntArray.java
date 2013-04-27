package ast.type;

import ast.Visitor;

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
  public int getNum()
  {
    return 1;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
