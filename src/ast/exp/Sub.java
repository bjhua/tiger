package ast.exp;

public class Sub extends T
{
  public T left;
  public T right;

  public Sub(T left, T right)
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
