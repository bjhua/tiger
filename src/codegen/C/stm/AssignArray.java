package codegen.C.stm;

import codegen.C.Visitor;

public class AssignArray extends T
{
  public String id;
  public ast.exp.T index;
  public ast.exp.T exp;

  public AssignArray(String id, ast.exp.T index, ast.exp.T exp)
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
