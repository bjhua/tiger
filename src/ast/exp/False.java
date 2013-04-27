package ast.exp;

public class False extends T
{
  public False(T left, T right)
  {
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
