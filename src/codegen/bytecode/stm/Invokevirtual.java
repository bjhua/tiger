package codegen.bytecode.stm;

import codegen.bytecode.Visitor;

public class Invokevirtual extends T
{
  public String f;
  public String c;
  public java.util.LinkedList<codegen.bytecode.type.T> at;
  public codegen.bytecode.type.T rt;

  public Invokevirtual(String f, String c,
      java.util.LinkedList<codegen.bytecode.type.T> at,
      codegen.bytecode.type.T rt)
  {
    this.f = f;
    this.c = c;
    this.at = at;
    this.rt = rt;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
