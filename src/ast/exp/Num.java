package ast.exp;

public class Num extends T
{
  public int num;

  public Num(int num)
  {
    this.num = num;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
