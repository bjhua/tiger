package codegen.C.stm;

import codegen.C.Visitor;

public class AssignArray extends T
{
  public codegen.C.exp.Id id;
  public codegen.C.exp.T index;
  public codegen.C.exp.T exp;

  public AssignArray(codegen.C.exp.Id id, codegen.C.exp.T index, codegen.C.exp.T exp)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
