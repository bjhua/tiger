package ast.stm;

public class Print extends T
{
  public ast.exp.T exp;

  public Print(ast.exp.T exp)
  {
    this.exp = exp;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
