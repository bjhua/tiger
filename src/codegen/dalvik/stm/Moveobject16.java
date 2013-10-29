package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Moveobject16 extends T
{
  public String left, right;

  public Moveobject16(String left, String right)
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
