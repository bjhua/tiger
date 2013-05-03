package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Iload extends T
{
  public int index;

  public Iload(int index)
  {
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
