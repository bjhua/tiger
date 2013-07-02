package ast.stm;

public class AssignArray extends T
{
  public ast.exp.Id id;
  public ast.exp.T index;
  public ast.exp.T exp;

  public AssignArray(ast.exp.Id id, ast.exp.T index, ast.exp.T exp)
  {
    this.id = id;
    this.index = index;
    this.exp = exp;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
