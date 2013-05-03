package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Imul extends T
{
  public Imul()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
