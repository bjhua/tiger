package ast.stm;

public class Assign extends T
{
  public String id;
  public ast.exp.T exp;
  public ast.type.T type; // type of the id

  public Assign(String id, ast.exp.T exp)
  {
    this.id = id;
    this.exp = exp;
    this.type = null;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
