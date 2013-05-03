package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Label extends T
{
  public util.Label l;

  public Label(util.Label l)
  {
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
