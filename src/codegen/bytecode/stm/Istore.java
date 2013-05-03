package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Istore extends T
{
  public int index;

  public Istore(int index)
  {
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
