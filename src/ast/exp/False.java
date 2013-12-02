package ast.exp;

public class False extends T
{
  public False()
  {
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
