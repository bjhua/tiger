package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Const extends T
{
  public String dst;
  public int i;

  public Const(String dst, int i)
  {
    this.dst = dst;
    this.i = i;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
