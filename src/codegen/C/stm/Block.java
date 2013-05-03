package codegen.C.stm;

import codegen.C.Visitor;

public class Block extends T
{
  public java.util.LinkedList<T> stms;

  public Block(java.util.LinkedList<T> stms)
  {
    this.stms = stms;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
