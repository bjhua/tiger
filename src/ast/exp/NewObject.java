package ast.exp;

public class NewObject extends T
{
  public String id;

  public NewObject(String id)
  {
    this.id = id;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
    return;
  }
}
