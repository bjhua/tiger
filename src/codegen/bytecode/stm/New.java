package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class New extends T
{
  public String c;

  public New(String c)
  {
    this.c = c;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
