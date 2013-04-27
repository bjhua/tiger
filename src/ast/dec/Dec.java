package ast.dec;

import ast.Visitor;

public class Dec extends T
{
  public ast.type.T type;
  public String id;

  public Dec(ast.type.T type, String id)
  {
    this.type = type;
    this.id = id;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
