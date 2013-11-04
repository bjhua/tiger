package codegen.C.stm;

import codegen.C.Visitor;

public class While extends T
{
  public codegen.C.exp.T condition;
  public T body;

  public While(codegen.C.exp.T condition, T body)
  {
    this.condition = condition;
    this.body = body;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
