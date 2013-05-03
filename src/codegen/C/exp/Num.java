package codegen.C.exp;

import codegen.C.Visitor;

public class Num extends T
{
  public int num;

  public Num(int num)
  {
    this.num = num;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
