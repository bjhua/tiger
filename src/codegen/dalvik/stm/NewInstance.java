package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class NewInstance extends T
{
  public String dst;
  public String c;

  public NewInstance(String dst, String c)
  {
    this.dst = dst;
    this.c = c;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
