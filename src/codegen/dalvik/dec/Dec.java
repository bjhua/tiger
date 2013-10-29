package codegen.dalvik.dec;

import codegen.dalvik.Visitor;

public class Dec extends T
{
  public codegen.dalvik.type.T type;
  public String id;

  public Dec(codegen.dalvik.type.T type, String id)
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
