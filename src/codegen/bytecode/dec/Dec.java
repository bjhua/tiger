package codegen.bytecode.dec;

import codegen.bytecode.Visitor;

public class Dec extends T
{
  public codegen.bytecode.type.T type;
  public String id;

  public Dec(codegen.bytecode.type.T type, String id)
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
