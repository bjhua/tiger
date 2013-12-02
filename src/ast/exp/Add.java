package ast.exp;

public class Add extends T
{
  public T left;
  public T right;

  public Add(T left, T right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
