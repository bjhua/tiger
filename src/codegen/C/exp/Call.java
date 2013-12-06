package codegen.C.exp;

import codegen.C.Visitor;

public class Call extends T
{
  public String assign;
  public T exp;
  public String id;
  public java.util.LinkedList<T> args;
  public codegen.C.type.T retType; // return type for the call

  public Call(String assign, T exp, String id
      , java.util.LinkedList<T> args
      , codegen.C.type.T retType)
  {
    this.assign = assign;
    this.exp = exp;
    this.id = id;
    this.args = args;
    this.retType = retType;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
