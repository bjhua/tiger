package codegen.bytecode.mainClass;

import codegen.bytecode.Visitor;

public class MainClass extends T
{
  public String id;
  public String arg;
  public java.util.LinkedList<codegen.bytecode.stm.T> stms;

  public MainClass(String id, String arg,
      java.util.LinkedList<codegen.bytecode.stm.T> stms)
  {
    this.id = id;
    this.arg = arg;
    this.stms = stms;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }

}
