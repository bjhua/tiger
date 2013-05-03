package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Isub extends T
{
  public Isub()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
