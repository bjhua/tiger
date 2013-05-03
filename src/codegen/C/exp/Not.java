package codegen.C.exp;

import codegen.C.Visitor;

public class Not extends T
{
  public T exp;

  public Not(T exp)
  {
    this.exp = exp;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
