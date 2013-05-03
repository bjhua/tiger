package codegen.C.classs;

import codegen.C.Visitor;

public class Class extends T
{
  public String id;
  public java.util.LinkedList<codegen.C.Tuple> decs;

  public Class(String id, java.util.LinkedList<codegen.C.Tuple> decs)
  {
    this.id = id;
    this.decs = decs;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }

}
