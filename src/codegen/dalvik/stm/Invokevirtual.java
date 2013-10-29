package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Invokevirtual extends T
{
  public String f;
  public String c;
  public java.util.LinkedList<codegen.dalvik.type.T> at;
  public codegen.dalvik.type.T rt;

  public Invokevirtual(String f, String c,
      java.util.LinkedList<codegen.dalvik.type.T> at,
      codegen.dalvik.type.T rt)
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
