package codegen.C.stm;

import codegen.C.Visitor;

public class If extends T
{
  public codegen.C.exp.T condition;
  public T thenn;
  public T elsee;

  public If(codegen.C.exp.T condition, T thenn, T elsee)
  {
    this.condition = condition;
    this.thenn = thenn;
    this.elsee = elsee;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
