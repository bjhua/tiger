package codegen.C.exp;

import codegen.C.Visitor;

public class This extends T
{
  public This()
  {
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
