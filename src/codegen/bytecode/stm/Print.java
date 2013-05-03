package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Print extends T
{
  public Print()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
