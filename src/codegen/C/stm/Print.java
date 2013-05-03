package codegen.C.stm;

import codegen.C.Visitor;

public class Print extends T
{
  public codegen.C.exp.T exp;

  public Print(codegen.C.exp.T exp)
  {
    this.exp = exp;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
