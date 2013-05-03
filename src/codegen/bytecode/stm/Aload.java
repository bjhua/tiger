package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Aload extends T
{
  public int index;

  public Aload(int index)
  {
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
