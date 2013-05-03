package codegen.C.dec;

import codegen.C.Visitor;

public class Dec extends T
{
  public codegen.C.type.T type;
  public String id;

  public Dec(codegen.C.type.T type, String id)
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
