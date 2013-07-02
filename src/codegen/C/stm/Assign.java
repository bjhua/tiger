package codegen.C.stm;

import codegen.C.Visitor;

public class Assign extends T
{
  public codegen.C.exp.Id id;
  public codegen.C.exp.T exp;

  public Assign(codegen.C.exp.Id id, codegen.C.exp.T exp)
  {
    this.id = id;
    this.exp = exp;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
