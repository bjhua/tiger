package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Move16 extends T
{
  public String left, right;

  public Move16(String left, String right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
